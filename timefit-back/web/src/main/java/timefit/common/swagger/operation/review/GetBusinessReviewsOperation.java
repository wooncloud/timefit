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
        summary = "업체 리뷰 목록 조회",
        description = """
                특정 업체의 리뷰 목록을 조회합니다 (공개 API).
                
                **응답 데이터:**
                - 리뷰 통계 (평균 평점, 평점별 분포)
                - 리뷰 목록
                - 페이징 정보
                
                **필터:**
                - minRating: 최소 평점 (선택)
                
                **권한:**
                - 인증 불필요 (공개 API)
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReviewResponseDto.ReviewList.class)
                )
        ),
        @ApiResponse(responseCode = "404", description = "업체를 찾을 수 없음")
})
@Parameter(name = "businessId", description = "업체 ID", required = true)
@Parameter(name = "minRating", description = "최소 평점 필터 (1~5)", example = "4")
@Parameter(name = "page", description = "페이지 번호", example = "0")
@Parameter(name = "size", description = "페이지 크기", example = "20")
public @interface GetBusinessReviewsOperation {
}
