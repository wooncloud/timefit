package timefit.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.review.dto.ReviewRequestDto;
import timefit.review.dto.ReviewResponseDto;

import java.util.UUID;

/**
 * Review Facade Service
 *
 * 역할:
 * - Controller와 Service 계층 사이의 단일 진입점
 * - 트랜잭션 경계 설정
 * - Command/Query Service에 단순 위임
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewCommandService commandService;
    private final ReviewQueryService queryService;

    /**
     * 업체별 리뷰 목록 조회 (통계 포함)
     *
     * @param businessId 업체 ID
     * @param minRating 최소 평점 필터
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 리뷰 목록 + 통계
     */
    public ReviewResponseDto.ReviewList getBusinessReviews(
            UUID businessId,
            Integer minRating,
            int page,
            int size) {

        log.debug("Facade: 업체 리뷰 목록 조회 - businessId={}, minRating={}, page={}, size={}",
                businessId, minRating, page, size);

        return queryService.getBusinessReviews(businessId, minRating, page, size);
    }

    /**
     * 내 리뷰 목록 조회
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

        log.debug("Facade: 내 리뷰 목록 조회 - userId={}, page={}, size={}", userId, page, size);
        return queryService.getMyReviews(userId, page, size);
    }

    /**
     * 리뷰 상세 조회
     *
     * @param reviewId 리뷰 ID
     * @return 리뷰 상세
     */
    public ReviewResponseDto.ReviewDetail getReview(UUID reviewId) {
        log.debug("Facade: 리뷰 상세 조회 - reviewId={}", reviewId);
        return queryService.getReview(reviewId);
    }

    /**
     * 리뷰 통계 조회
     *
     * @param businessId 업체 ID
     * @return 리뷰 통계
     */
    public ReviewResponseDto.ReviewStatistics getReviewStatistics(UUID businessId) {
        log.debug("Facade: 리뷰 통계 조회 - businessId={}", businessId);
        return queryService.getReviewStatistics(businessId);
    }

    /**
     * 리뷰 작성
     *
     * @param userId 사용자 ID
     * @param request 리뷰 작성 요청
     * @return 작성된 리뷰
     */
    @Transactional
    public ReviewResponseDto.ReviewDetail createReview(
            UUID userId,
            ReviewRequestDto.CreateReview request) {

        log.debug("Facade: 리뷰 작성 - userId={}, reservationId={}", userId, request.reservationId());
        return commandService.createReview(userId, request);
    }

    /**
     * 리뷰 수정
     *
     * @param userId 사용자 ID
     * @param reviewId 리뷰 ID
     * @param request 리뷰 수정 요청
     * @return 수정된 리뷰
     */
    @Transactional
    public ReviewResponseDto.ReviewDetail updateReview(
            UUID userId,
            UUID reviewId,
            ReviewRequestDto.UpdateReview request) {

        log.debug("Facade: 리뷰 수정 - userId={}, reviewId={}", userId, reviewId);
        return commandService.updateReview(userId, reviewId, request);
    }

    /**
     * 리뷰 삭제
     *
     * @param userId 사용자 ID
     * @param reviewId 리뷰 ID
     */
    @Transactional
    public void deleteReview(UUID userId, UUID reviewId) {
        log.debug("Facade: 리뷰 삭제 - userId={}, reviewId={}", userId, reviewId);
        commandService.deleteReview(userId, reviewId);
    }

    /**
     * 사용자 리뷰 개수 조회
     *
     * @param userId 사용자 ID
     * @return 리뷰 개수
     */
    public long getUserReviewCount(UUID userId) {
        log.debug("Facade: 사용자 리뷰 개수 조회 - userId={}", userId);
        return queryService.getUserReviewCount(userId);
    }
}