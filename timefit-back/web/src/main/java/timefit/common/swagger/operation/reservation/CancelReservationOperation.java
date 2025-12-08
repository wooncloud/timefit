package timefit.common.swagger.operation.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.reservation.dto.ReservationResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "예약 취소 (고객용)",
        description = """
            예약을 취소합니다.
            
            1. Path Parameter
               - reservationId: 예약 ID (UUID)
            
            2. Request Body 필수값
               - reason: 취소 사유 (최대 200자)
            
            3. 제약사항
               - PENDING, CONFIRMED 상태에서만 취소 가능
            
            4. 권한
               - 로그인 필요 (CUSTOMER)
               - 본인의 예약만 취소 가능
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "취소 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReservationResponseDto.ReservationActionResult.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                1. reason 은(는) 필수 값입니다.
                
                CANNOT_CANCEL_RESERVATION - 취소할 수 없는 상태
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "RESERVATION_NOT_FOUND - 예약을 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "RESERVATION_ACCESS_DENIED - 본인의 예약이 아님",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface CancelReservationOperation {
}