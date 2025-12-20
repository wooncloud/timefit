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
        summary = "향후 활성 슬롯 조회",
        description = """
            오늘 날짜 이후 예약 가능한 상태의 모든 슬롯을 조회합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. 조회 조건
               - slotDate >= 오늘
               - isAvailable = true
            
            3. 권한
               - 인증 불필요 (공개 조회)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "슬롯 목록 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookingSlotResponse.BookingSlotList.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "BUSINESS_NOT_FOUND - 업체를 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface GetUpcomingSlotsOperation {
}