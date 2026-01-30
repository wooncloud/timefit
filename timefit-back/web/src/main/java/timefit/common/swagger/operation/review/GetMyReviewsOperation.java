package timefit.common.swagger.operation.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.review.dto.ReviewResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "내 리뷰 목록 조회",
        description = """
                내가 작성한 리뷰 목록을 조회합니다.
                
                **응답 데이터:**
                - 리뷰 목록 (업체 정보 포함)
                - 페이징 정보
                
                **권한:**
                - 로그인한 고객만 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReviewResponseDto.MyReviewList.class)
                )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패")
})
@Parameter(name = "page", description = "페이지 번호", example = "0")
@Parameter(name = "size", description = "페이지 크기", example = "20")
public @interface GetMyReviewsOperation {
}
