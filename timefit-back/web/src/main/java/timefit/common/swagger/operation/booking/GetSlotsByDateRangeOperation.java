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
        summary = "기간별 예약 슬롯 조회",
        description = """
            시작 날짜부터 종료 날짜까지 생성된 모든 예약 슬롯을 조회합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. Query Parameter 필수값
               - startDate: 조회 시작 날짜 (YYYY-MM-DD)
               - endDate: 조회 종료 날짜 (YYYY-MM-DD)
            
            3. 제약사항
               - startDate는 endDate보다 이전이어야 함
            
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
                responseCode = "400",
                description = "INVALID_DATE_RANGE - 시작일이 종료일보다 늦음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
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
public @interface GetSlotsByDateRangeOperation {
}