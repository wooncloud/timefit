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
        summary = "예약 수정 (고객용)",
        description = """
            예약 정보를 수정합니다.
            
            1. Path Parameter
               - reservationId: 예약 ID (UUID)
            
            2. Request Body (변경할 필드만 입력)
               - reservationDate: 예약 날짜
               - reservationTime: 예약 시간
               - customerName: 예약자 이름
               - customerPhone: 연락처
               - notes: 메모
               - reason: 수정 사유 (필수)
            
            3. 제약사항
               - PENDING 상태에서만 수정 가능
               - reason: 최대 200자
            
            4. 권한
               - 로그인 필요 (CUSTOMER)
               - 본인의 예약만 수정 가능
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReservationResponseDto.CustomerReservation.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                1. reason 은(는) 필수 값입니다.
                
                CANNOT_UPDATE_RESERVATION - 수정할 수 없는 상태
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
public @interface UpdateReservationOperation {
}