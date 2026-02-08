package timefit.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.service.validator.BusinessValidator;
import timefit.review.dto.ReviewResponseDto;
import timefit.review.entity.Review;
import timefit.review.repository.ReviewQueryRepository;
import timefit.review.repository.ReviewRepository;
import timefit.review.service.validator.ReviewValidator;
import timefit.auth.service.validator.AuthValidator;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Review 조회 전담 서비스 (Read)
 *
 * 주요 역할:
 * - 업체별 리뷰 목록 조회 (통계 포함)
 * - 내 리뷰 목록 조회
 * - 리뷰 상세 조회
 * - 리뷰 통계 계산
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewValidator reviewValidator;
    private final BusinessValidator businessValidator;
    private final AuthValidator authValidator;

    /**
     * 업체별 리뷰 목록 조회 (페이징 + 통계)
     *
     * 프로세스:
     * 1. 업체 검증
     * 2. 리뷰 통계 계산
     * 3. 리뷰 목록 조회 (최신순, 평점 필터)
     * 4. DTO 변환
     *
     * @param businessId 업체 ID
     * @param minRating 최소 평점 필터 (null 가능)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 리뷰 목록 + 통계
     */
    public ReviewResponseDto.ReviewList getBusinessReviews(
            UUID businessId,
            Integer minRating,
            int page,
            int size) {

        log.info("업체 리뷰 목록 조회 시작: businessId={}, minRating={}, page={}, size={}",
                businessId, minRating, page, size);

        // 1. 업체 검증
        businessValidator.validateBusinessExists(businessId);

        // 2. 리뷰 통계 계산
        ReviewResponseDto.ReviewStatistics statistics = getReviewStatistics(businessId);

        // 3. 리뷰 목록 조회 (QueryDSL)
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage;

        if (minRating != null) {
            reviewPage = reviewQueryRepository.findByBusinessIdWithRatingFilter(
                    businessId, minRating, pageable);
        } else {
            reviewPage = reviewRepository.findByBusinessIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                    businessId, pageable);
        }

        // 4. DTO 변환
        List<ReviewResponseDto.ReviewSummary> reviews = reviewPage.getContent()
                .stream()
                .map(ReviewResponseDto.ReviewSummary::from)
                .toList();

        log.info("업체 리뷰 목록 조회 완료: businessId={}, count={}, totalElements={}",
                businessId, reviews.size(), reviewPage.getTotalElements());

        return ReviewResponseDto.ReviewList.of(
                statistics,
                reviews,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalPages(),
                reviewPage.getTotalElements()
        );
    }

    /**
     * 내 리뷰 목록 조회 (페이징)
     *
     * 프로세스:
     * 1. 사용자 검증
     * 2. 내 리뷰 조회 (최신순)
     * 3. DTO 변환
     *
     * @param userId 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 내 리뷰 목록
     */
    public ReviewResponseDto.MyReviewList getMyReviews(
            UUID userId,
            int page,
            int size) {

        log.info("내 리뷰 목록 조회 시작: userId={}, page={}, size={}", userId, page, size);

        // 1. 사용자 검증
        authValidator.validateUserExists(userId);

        // 2. 내 리뷰 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                userId, pageable);

        // 3. DTO 변환
        List<ReviewResponseDto.MyReviewItem> reviews = reviewPage.getContent()
                .stream()
                .map(ReviewResponseDto.MyReviewItem::from)
                .toList();

        log.info("내 리뷰 목록 조회 완료: userId={}, count={}, totalElements={}",
                userId, reviews.size(), reviewPage.getTotalElements());

        return ReviewResponseDto.MyReviewList.of(
                reviews,
                reviewPage.getTotalElements(),
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalPages()
        );
    }

    /**
     * 리뷰 상세 조회
     *
     * @param reviewId 리뷰 ID
     * @return 리뷰 상세
     */
    public ReviewResponseDto.ReviewDetail getReview(UUID reviewId) {
        log.debug("리뷰 상세 조회: reviewId={}", reviewId);

        Review review = reviewValidator.validateExists(reviewId);
        reviewValidator.validateNotDeleted(review);

        return ReviewResponseDto.ReviewDetail.from(review);
    }

    /**
     * 리뷰 통계 계산
     *
     * 프로세스:
     * 1. 평균 평점 계산
     * 2. 전체 리뷰 수 계산
     * 3. 평점별 분포 계산 (1~5점)
     *
     * @param businessId 업체 ID
     * @return 리뷰 통계
     */
    public ReviewResponseDto.ReviewStatistics getReviewStatistics(UUID businessId) {
        log.debug("리뷰 통계 계산: businessId={}", businessId);

        // 1. 평균 평점 계산
        Double averageRating = reviewQueryRepository.calculateAverageRatingByBusinessId(businessId);

        // 2. 전체 리뷰 수 계산
        long totalReviews = reviewRepository.countByBusinessIdAndDeletedAtIsNull(businessId);

        // 3. 평점별 분포 계산 (QueryDSL)
        Map<Integer, Long> ratingDistribution = reviewQueryRepository
                .getRatingDistributionByBusinessId(businessId);

        // 4. 1~5점 모두 포함 (없는 평점은 0으로 설정)
        Map<Integer, Long> completeDistribution = IntStream.rangeClosed(1, 5)
                .boxed()
                .collect(Collectors.toMap(
                        rating -> rating,
                        rating -> ratingDistribution.getOrDefault(rating, 0L)
                ));

        log.debug("리뷰 통계 계산 완료: businessId={}, avgRating={}, total={}",
                businessId, averageRating, totalReviews);

        return ReviewResponseDto.ReviewStatistics.of(
                averageRating,
                totalReviews,
                completeDistribution
        );
    }

    /**
     * 사용자의 리뷰 개수 조회
     *
     * @param userId 사용자 ID
     * @return 리뷰 개수
     */
    public long getUserReviewCount(UUID userId) {
        log.debug("사용자 리뷰 개수 조회: userId={}", userId);

        long count = reviewRepository.countByUserIdAndDeletedAtIsNull(userId);

        log.debug("사용자 리뷰 개수 조회 결과: userId={}, count={}", userId, count);

        return count;
    }
}