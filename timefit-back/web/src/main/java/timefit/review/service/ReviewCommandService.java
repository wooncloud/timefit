package timefit.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.reservation.entity.Reservation;
import timefit.review.dto.ReviewRequestDto;
import timefit.review.dto.ReviewResponseDto;
import timefit.review.entity.Review;
import timefit.review.repository.ReviewQueryRepository;
import timefit.review.repository.ReviewRepository;
import timefit.review.service.validator.ReviewValidator;
import timefit.user.entity.User;
import timefit.auth.service.validator.AuthValidator;

import java.util.UUID;

/**
 * Review 데이터 변경 전담 서비스 (CUD)
 *
 * 주요 역할:
 * - 리뷰 작성
 * - 리뷰 수정
 * - 리뷰 삭제 (Soft Delete)
 * - Business 평점 재계산
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewValidator reviewValidator;
    private final AuthValidator authValidator;

    /**
     * 리뷰 작성
     *
     * 프로세스:
     * 1. 사용자 검증
     * 2. 예약 검증 (완료 상태, 소유권, 중복 리뷰)
     * 3. Review 엔티티 생성
     * 4. Business 평점 재계산
     *
     * @param userId 사용자 ID
     * @param request 리뷰 작성 요청
     * @return 작성된 리뷰
     */
    public ReviewResponseDto.ReviewDetail createReview(
            UUID userId,
            ReviewRequestDto.CreateReview request) {

        log.info("리뷰 작성 시작: userId={}, reservationId={}", userId, request.reservationId());

        // 1. 사용자 검증
        User user = authValidator.validateUserExists(userId);

        // 2. 예약 검증 (완료 상태, 소유권, 중복 확인)
        Reservation reservation = reviewValidator.validateCanWriteReview(request.reservationId(), userId);

        // 3. Review 생성 (메뉴명 스냅샷)
        Business business = reservation.getMenu().getBusiness();
        String menuName = reservation.getMenu().getServiceName();

        Review review = Review.create(
                business,
                user,
                reservation,
                menuName,
                request.rating(),
                request.comment()
        );

        reviewRepository.save(review);

        log.info("리뷰 작성 완료: reviewId={}, userId={}, businessId={}",
                review.getId(), userId, business.getId());

        // 4. Business 평점 재계산
        updateBusinessRating(business);

        return ReviewResponseDto.ReviewDetail.from(review);
    }

    /**
     * 리뷰 수정
     *
     * 프로세스:
     * 1. Review 조회 및 검증
     * 2. 소유권 검증
     * 3. Review 수정
     * 4. Business 평점 재계산
     *
     * @param userId 사용자 ID
     * @param reviewId 리뷰 ID
     * @param request 리뷰 수정 요청
     * @return 수정된 리뷰
     */
    public ReviewResponseDto.ReviewDetail updateReview(
            UUID userId,
            UUID reviewId,
            ReviewRequestDto.UpdateReview request) {

        log.info("리뷰 수정 시작: userId={}, reviewId={}", userId, reviewId);

        // 1. Review 조회 및 검증
        Review review = reviewValidator.validateExists(reviewId);
        reviewValidator.validateNotDeleted(review);

        // 2. 소유권 검증
        reviewValidator.validateOwnership(review, userId);

        // 3. Review 수정
        review.update(request.rating(), request.comment());

        log.info("리뷰 수정 완료: reviewId={}, rating={}", reviewId, request.rating());

        // 4. Business 평점 재계산
        updateBusinessRating(review.getBusiness());

        return ReviewResponseDto.ReviewDetail.from(review);
    }

    /**
     * 리뷰 삭제 (Soft Delete)
     *
     * 프로세스:
     * 1. Review 조회 및 검증
     * 2. 소유권 검증
     * 3. Soft Delete 처리
     * 4. Business 평점 재계산
     *
     * @param userId 사용자 ID
     * @param reviewId 리뷰 ID
     */
    public void deleteReview(UUID userId, UUID reviewId) {
        log.info("리뷰 삭제 시작: userId={}, reviewId={}", userId, reviewId);

        // 1. Review 조회 및 검증
        Review review = reviewValidator.validateExists(reviewId);
        reviewValidator.validateNotDeleted(review);

        // 2. 소유권 검증
        reviewValidator.validateOwnership(review, userId);

        // 3. Soft Delete
        Business business = review.getBusiness();
        review.delete();

        log.info("리뷰 삭제 완료: reviewId={}, businessId={}", reviewId, business.getId());

        // 4. Business 평점 재계산
        updateBusinessRating(business);
    }

    /**
     * Business 평점 재계산
     *
     * 프로세스:
     * 1. 해당 Business의 활성 리뷰 평균 평점 계산 (QueryDSL)
     * 2. 해당 Business의 활성 리뷰 개수 계산
     * 3. Business 엔티티 업데이트
     *
     * @param business 평점을 재계산할 Business
     */
    private void updateBusinessRating(Business business) {
        log.debug("Business 평점 재계산 시작: businessId={}", business.getId());

        // 1. 평균 평점 계산 (QueryDSL로 DB에서 계산)
        Double averageRating = reviewQueryRepository.calculateAverageRatingByBusinessId(business.getId());

        // 2. 리뷰 개수 계산
        long reviewCount = reviewRepository.countByBusinessIdAndDeletedAtIsNull(business.getId());

        // 3. Business 엔티티 업데이트
        business.updateRating(
                averageRating != null ? averageRating : 0.0,
                (int) reviewCount
        );

        log.info("Business 평점 재계산 완료: businessId={}, averageRating={}, reviewCount={}",
                business.getId(), averageRating, reviewCount);
    }
}