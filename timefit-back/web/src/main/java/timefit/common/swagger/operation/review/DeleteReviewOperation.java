package timefit.common.swagger.operation.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "리뷰 삭제",
        description = """
                작성한 리뷰를 삭제(Soft Delete)합니다.
                
                **검증:**
                - 리뷰 존재 여부 확인
                - 본인의 리뷰인지 확인
                
                **권한:**
                - 로그인한 고객만 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
})
@Parameter(name = "reviewId", description = "리뷰 ID", required = true)
public @interface DeleteReviewOperation {
}
