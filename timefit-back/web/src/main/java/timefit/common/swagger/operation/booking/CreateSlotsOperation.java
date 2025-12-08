package timefit.common.swagger.operation.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.booking.dto.BookingSlotResponse;
import timefit.common.ResponseData;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "예약 슬롯 일괄 생성 (업체용)",
        description = """
            메뉴, 간격, 날짜/시간 스케줄을 지정하여 예약 슬롯을 일괄 생성합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. Request Body 필수값
               - menuId: 슬롯을 생성할 메뉴 ID
               - slotIntervalMinutes: 슬롯 간격 (분)
               - schedules: 일별 슬롯 생성 스케줄 목록
            
            3. 슬롯 생성 규칙
               - RESERVATION_BASED 메뉴만 슬롯 생성 가능
               - slotIntervalMinutes는 메뉴 소요 시간 이상이어야 함
               - 영업시간 내에서만 슬롯 생성
               - 중복 슬롯은 자동으로 건너뜀
            
            4. schedules 구조
               - date: 슬롯 생성 날짜 (오늘 또는 미래)
               - timeRanges: 시간대 목록 (비어있으면 전체 영업시간 사용)
                 * startTime: 시작 시간 (HH:mm)
                 * endTime: 종료 시간 (HH:mm)
            
            5. 응답
               - totalRequested: 총 요청된 슬롯 수
               - created: 실제 생성된 슬롯 수
               - skipped: 건너뛴 슬롯 수 (중복 또는 영업시간 외)
            
            6. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "슬롯 생성 요청 처리 완료",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookingSlotResponse.CreationResult.class),
                        examples = @ExampleObject(
                                value = """
                        {
                          "success": true,
                          "data": {
                            "totalRequested": 100,
                            "created": 95,
                            "skipped": 5
                          }
                        }
                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                1. menuId 은(는) 필수 값입니다.
                2. slotIntervalMinutes 은(는) 필수 값입니다.
                3. schedules 은(는) 필수 값입니다.
                
                SLOT_INVALID_MENU_TYPE - 슬롯 생성 불가능한 메뉴 타입 (ONDEMAND_BASED)
                
                INVALID_SLOT_INTERVAL - 슬롯 간격이 메뉴 소요 시간보다 짧음
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
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
                BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                
                MENU_NOT_FOUND - 메뉴를 찾을 수 없음
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface CreateSlotsOperation {
}