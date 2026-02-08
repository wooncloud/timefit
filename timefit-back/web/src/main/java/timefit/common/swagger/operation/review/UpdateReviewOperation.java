package timefit.common.swagger.operation.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.review.dto.ReviewResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "리뷰 수정",
        description = """
                작성한 리뷰를 수정합니다.
                
                **요청 데이터:**
                - rating: 평점 1~5 (필수)
                - comment: 리뷰 내용 (선택, 최대 1000자)
                
                **검증:**
                - 리뷰 존재 여부 확인
                - 본인의 리뷰인지 확인
                - 삭제되지 않은 리뷰인지 확인
                
                **권한:**
                - 로그인한 고객만 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "리뷰 수정 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReviewResponseDto.ReviewDetail.class)
                )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
})
@Parameter(name = "reviewId", description = "리뷰 ID", required = true)
public @interface UpdateReviewOperation {
}
