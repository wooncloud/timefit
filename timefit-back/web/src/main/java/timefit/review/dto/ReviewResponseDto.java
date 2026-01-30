package timefit.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import timefit.review.entity.Review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Review 응답 DTO
 */
@Schema(description = "리뷰 응답")
public class ReviewResponseDto {

    /**
     * 리뷰 상세 정보
     */
    @Schema(description = "리뷰 상세")
    public record ReviewResponse(
            @Schema(description = "리뷰 ID", example = "660e8400-e29b-41d4-a716-446655440000")
            UUID reviewId,

            @Schema(description = "업체 ID", example = "30000000-0000-0000-0000-000000000001")
            UUID businessId,

            @Schema(description = "업체명", example = "스타일 헤어샵")
            String businessName,

            @Schema(description = "사용자 ID", example = "20000000-0000-0000-0000-000000000001")
            UUID userId,

            @Schema(description = "작성자명", example = "홍길동")
            String userName,

            @Schema(description = "예약 ID", example = "40000000-0000-0000-0000-000000000001")
            UUID reservationId,

            @Schema(description = "서비스명 (스냅샷)", example = "커트")
            String menuName,

            @Schema(description = "평점 (1~5)", example = "5")
            Integer rating,

            @Schema(description = "리뷰 내용", example = "매우 만족스러웠습니다!")
            String comment,

            @Schema(description = "작성일시", example = "2026-01-30T14:00:00")
            LocalDateTime createdAt,

            @Schema(description = "수정일시", example = "2026-01-30T15:00:00")
            LocalDateTime updatedAt
    ) {
        /**
         * Review 엔티티를 ReviewResponse로 변환
         */
        public static ReviewResponse from(Review review) {
            return new ReviewResponse(
                    review.getId(),
                    review.getBusiness().getId(),
                    review.getBusiness().getBusinessName(),
                    review.getUser().getId(),
                    review.getUser().getName(),
                    review.getReservation() != null ? review.getReservation().getId() : null,
                    review.getMenuName(),
                    review.getRating(),
                    review.getComment(),
                    review.getCreatedAt(),
                    review.getUpdatedAt()
            );
        }
    }

    /**
     * 리뷰 요약 (목록용)
     */
    @Schema(description = "리뷰 요약")
    public record ReviewSummary(
            @Schema(description = "리뷰 ID", example = "660e8400-e29b-41d4-a716-446655440000")
            UUID reviewId,

            @Schema(description = "작성자명", example = "홍길동")
            String userName,

            @Schema(description = "서비스명", example = "커트")
            String menuName,

            @Schema(description = "평점 (1~5)", example = "5")
            Integer rating,

            @Schema(description = "리뷰 내용", example = "매우 만족스러웠습니다!")
            String comment,

            @Schema(description = "작성일시", example = "2026-01-30T14:00:00")
            LocalDateTime createdAt
    ) {
        /**
         * Review 엔티티를 ReviewSummary로 변환
         */
        public static ReviewSummary from(Review review) {
            return new ReviewSummary(
                    review.getId(),
                    review.getUser().getName(),
                    review.getMenuName(),
                    review.getRating(),
                    review.getComment(),
                    review.getCreatedAt()
            );
        }
    }

    /**
     * 리뷰 통계 정보
     */
    @Schema(description = "리뷰 통계")
    public record ReviewStatistics(
            @Schema(description = "평균 평점", example = "4.5")
            Double averageRating,

            @Schema(description = "전체 리뷰 수", example = "120")
            Long totalReviews,

            @Schema(description = "평점별 분포 (1~5점)", example = "{\"5\": 80, \"4\": 30, \"3\": 8, \"2\": 2, \"1\": 0}")
            Map<Integer, Long> ratingDistribution
    ) {
        /**
         * 통계 데이터로 ReviewStatistics 생성
         */
        public static ReviewStatistics of(
                Double averageRating,
                Long totalReviews,
                Map<Integer, Long> ratingDistribution) {
            return new ReviewStatistics(
                    averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0,
                    totalReviews,
                    ratingDistribution
            );
        }
    }

    /**
     * 업체별 리뷰 목록 응답 (통계 + 목록)
     */
    @Schema(description = "업체별 리뷰 목록")
    public record ReviewListResponse(
            @Schema(description = "리뷰 통계")
            ReviewStatistics statistics,

            @Schema(description = "리뷰 목록")
            List<ReviewSummary> reviews,

            @Schema(description = "현재 페이지 (0부터 시작)", example = "0")
            int page,

            @Schema(description = "페이지 크기", example = "20")
            int size,

            @Schema(description = "전체 페이지 수", example = "6")
            int totalPages,

            @Schema(description = "전체 리뷰 수", example = "120")
            long totalElements
    ) {
        /**
         * 통계와 리뷰 목록으로 응답 생성
         */
        public static ReviewListResponse of(
                ReviewStatistics statistics,
                List<ReviewSummary> reviews,
                int page,
                int size,
                int totalPages,
                long totalElements) {
            return new ReviewListResponse(
                    statistics,
                    reviews,
                    page,
                    size,
                    totalPages,
                    totalElements
            );
        }
    }

    /**
     * 내 리뷰 아이템 (업체 정보 포함)
     */
    @Schema(description = "내 리뷰 아이템")
    public record MyReviewItem(
            @Schema(description = "리뷰 ID", example = "660e8400-e29b-41d4-a716-446655440000")
            UUID reviewId,

            @Schema(description = "업체 ID", example = "30000000-0000-0000-0000-000000000001")
            UUID businessId,

            @Schema(description = "업체명", example = "스타일 헤어샵")
            String businessName,

            @Schema(description = "서비스명", example = "커트")
            String menuName,

            @Schema(description = "평점 (1~5)", example = "5")
            Integer rating,

            @Schema(description = "리뷰 내용", example = "매우 만족스러웠습니다!")
            String comment,

            @Schema(description = "작성일시", example = "2026-01-30T14:00:00")
            LocalDateTime createdAt,

            @Schema(description = "수정일시", example = "2026-01-30T15:00:00")
            LocalDateTime updatedAt
    ) {
        /**
         * Review 엔티티를 MyReviewItem으로 변환
         */
        public static MyReviewItem from(Review review) {
            return new MyReviewItem(
                    review.getId(),
                    review.getBusiness().getId(),
                    review.getBusiness().getBusinessName(),
                    review.getMenuName(),
                    review.getRating(),
                    review.getComment(),
                    review.getCreatedAt(),
                    review.getUpdatedAt()
            );
        }
    }

    /**
     * 내 리뷰 목록 응답
     */
    @Schema(description = "내 리뷰 목록")
    public record MyReviewListResponse(
            @Schema(description = "내 리뷰 목록")
            List<MyReviewItem> reviews,

            @Schema(description = "전체 리뷰 수", example = "5")
            long totalCount,

            @Schema(description = "현재 페이지 (0부터 시작)", example = "0")
            int page,

            @Schema(description = "페이지 크기", example = "20")
            int size,

            @Schema(description = "전체 페이지 수", example = "1")
            int totalPages
    ) {
        /**
         * 내 리뷰 목록과 페이지 정보로 응답 생성
         */
        public static MyReviewListResponse of(
                List<MyReviewItem> reviews,
                long totalCount,
                int page,
                int size,
                int totalPages) {
            return new MyReviewListResponse(reviews, totalCount, page, size, totalPages);
        }
    }
}