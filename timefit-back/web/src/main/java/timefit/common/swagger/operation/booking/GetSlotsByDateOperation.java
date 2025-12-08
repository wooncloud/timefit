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
        summary = "특정 날짜의 예약 슬롯 조회",
        description = """
            해당 날짜에 생성된 모든 예약 슬롯을 조회합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. Query Parameter 필수값
               - date: 조회할 날짜 (YYYY-MM-DD)
            
            3. 응답
               - businessId: 업체 ID
               - startDate: 조회 시작 날짜 (= date)
               - endDate: 조회 종료 날짜 (= date)
               - slots: 해당 날짜의 슬롯 목록
            
            4. 권한
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
public @interface GetSlotsByDateOperation {
}