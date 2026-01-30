package timefit.common.swagger.requestbody.review;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.review.dto.ReviewRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "리뷰 수정 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReviewRequestDto.UpdateReview.class),
                examples = @ExampleObject(
                        value = """
                                {
                                  "rating": 4,
                                  "comment": "좋았습니다!"
                                }
                                """
                )
        )
)
public @interface UpdateReviewBody {
}