package timefit.common.swagger.requestbody.review;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.review.dto.ReviewRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "리뷰 작성 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReviewRequestDto.CreateReview.class),
                examples = @ExampleObject(
                        value = """
                                {
                                  "reservationId": "40000000-0000-0000-0000-000000000001",
                                  "rating": 5,
                                  "comment": "매우 만족스러웠습니다!"
                                }
                                """
                )
        )
)
public @interface CreateReviewRequestBody {
}