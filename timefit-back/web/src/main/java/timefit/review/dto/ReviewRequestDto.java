package timefit.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.UUID;

/**
 * Review 요청 DTO
 */
@Schema(description = "리뷰 요청")
public class ReviewRequestDto {

    /**
     * 리뷰 작성 요청
     */
    @Schema(description = "리뷰 작성 요청")
    public record CreateReviewRequest(
            @Schema(description = "예약 ID", required = true, example = "40000000-0000-0000-0000-000000000001")
            @NotNull(message = "예약 ID는 필수입니다")
            UUID reservationId,

            @Schema(description = "평점 (1~5)", required = true, example = "5")
            @NotNull(message = "평점은 필수입니다")
            @Min(value = 1, message = "평점은 최소 1점입니다")
            @Max(value = 5, message = "평점은 최대 5점입니다")
            Integer rating,

            @Schema(description = "리뷰 내용", example = "매우 만족스러웠습니다!")
            @Size(max = 1000, message = "리뷰는 1000자 이하로 입력해주세요")
            String comment
    ) {
    }

    /**
     * 리뷰 수정 요청
     */
    @Schema(description = "리뷰 수정 요청")
    public record UpdateReviewRequest(
            @Schema(description = "평점 (1~5)", required = true, example = "4")
            @NotNull(message = "평점은 필수입니다")
            @Min(value = 1, message = "평점은 최소 1점입니다")
            @Max(value = 5, message = "평점은 최대 5점입니다")
            Integer rating,

            @Schema(description = "리뷰 내용", example = "좋았습니다!")
            @Size(max = 1000, message = "리뷰는 1000자 이하로 입력해주세요")
            String comment
    ) {
    }
}