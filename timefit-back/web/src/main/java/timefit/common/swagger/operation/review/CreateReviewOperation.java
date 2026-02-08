package timefit.common.swagger.operation.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.review.dto.ReviewResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "리뷰 작성",
        description = """
                예약에 대한 리뷰를 작성합니다.
                
                **요청 데이터:**
                - reservationId: 예약 ID (필수)
                - rating: 평점 1~5 (필수)
                - comment: 리뷰 내용 (선택, 최대 1000자)
                
                **검증:**
                - 예약 존재 여부 확인
                - 예약이 완료(COMPLETED) 상태인지 확인
                - 이미 리뷰를 작성했는지 확인
                - 본인의 예약인지 확인
                
                **권한:**
                - 로그인한 고객만 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "리뷰 작성 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReviewResponseDto.ReviewDetail.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "success": true,
                                          "data": {
                                            "reviewId": "660e8400-e29b-41d4-a716-446655440000",
                                            "businessId": "30000000-0000-0000-0000-000000000001",
                                            "businessName": "스타일 헤어샵",
                                            "userId": "20000000-0000-0000-0000-000000000001",
                                            "userName": "홍길동",
                                            "reservationId": "40000000-0000-0000-0000-000000000001",
                                            "menuName": "커트",
                                            "rating": 5,
                                            "comment": "매우 만족스러웠습니다!",
                                            "createdAt": "2026-01-30T14:00:00",
                                            "updatedAt": "2026-01-30T14:00:00"
                                          }
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (이미 리뷰 작성, 완료되지 않은 예약 등)"
        ),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패 - 로그인 필요"
        ),
        @ApiResponse(
                responseCode = "403",
                description = "권한 없음 - 본인의 예약이 아님"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "예약을 찾을 수 없음"
        )
})
public @interface CreateReviewOperation {
}