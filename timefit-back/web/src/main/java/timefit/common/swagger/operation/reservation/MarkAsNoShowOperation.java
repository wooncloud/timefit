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
        summary = "노쇼 처리 (업체용)",
        description = """
            예약을 노쇼로 처리합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
               - reservationId: 예약 ID (UUID)
            
            2. Request Body
               - notes: 노쇼 메모
            
            3. 제약사항
               - CONFIRMED 상태에서만 노쇼 처리 가능
            
            4. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "노쇼 처리 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReservationResponseDto.ReservationActionResult.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "CANNOT_MARK_AS_NO_SHOW - 노쇼 처리할 수 없는 상태",
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
                description = """
                BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음
                
                RESERVATION_ACCESS_DENIED - 해당 업체의 예약이 아님
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface MarkAsNoShowOperation {
}