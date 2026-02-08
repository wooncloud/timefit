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
        summary = "리뷰 통계 조회",
        description = """
                특정 업체의 리뷰 통계를 조회합니다 (공개 API).
                
                **응답 데이터:**
                - 평균 평점
                - 전체 리뷰 수
                - 평점별 분포 (1~5점)
                
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
                        schema = @Schema(implementation = ReviewResponseDto.ReviewStatistics.class)
                )
        ),
        @ApiResponse(responseCode = "404", description = "업체를 찾을 수 없음")
})
@Parameter(name = "businessId", description = "업체 ID", required = true)
public @interface GetReviewStatisticsOperation {
}
