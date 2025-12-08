package timefit.common.swagger.operation.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.booking.dto.BookingSlotResponse;
import timefit.common.ResponseData;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "예약 슬롯 비활성화 (업체용)",
        description = """
            슬롯을 예약 불가능 상태로 변경합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
               - slotId: 슬롯 ID (UUID)
            
            2. 처리 내용
               - isAvailable = false로 변경
               - 고객이 더 이상 예약할 수 없음
               - 기존 예약은 유지됨
            
            3. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "슬롯 비활성화 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookingSlotResponse.BookingSlot.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = """
                AVAILABLE_SLOT_NOT_FOUND - 슬롯을 찾을 수 없음
                
                BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface DeactivateSlotOperation {
}