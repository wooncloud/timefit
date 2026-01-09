package timefit.common.swagger.requestbody.menu;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.menu.dto.MenuRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "메뉴 생성 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MenuRequestDto.CreateUpdateMenu.class),
                examples = {
                        @ExampleObject(
                                name = "예약형 서비스 (슬롯 자동 생성)",
                                value = """
                        {
                            "businessType": "BD003",
                            "categoryName": "헤어",
                            "serviceName": "헤어 컷",
                            "price": 30000,
                            "description": "기본 헤어 컷 서비스",
                            "orderType": "RESERVATION_BASED",
                            "durationMinutes": 60,
                            "autoGenerateSlots": true,
                            "slotSettings": {
                            "startDate": "2025-01-10",
                            "endDate": "2025-01-31",
                            "slotIntervalMinutes": 30,
                            "specificTimeRanges": [
                                {
                                "startTime": "09:00",
                                "endTime": "18:00"
                                }
                            ]
                            }
                        }
                        """
                        ),
                        @ExampleObject(
                                name = "현장 주문형 서비스",
                                value = """
                        {
                            "businessType": "BD000",
                            "categoryName": "메인 메뉴",
                            "serviceName": "김치찌개",
                            "price": 8000,
                            "description": "얼큰한 김치찌개",
                            "orderType": "ONDEMAND_BASED"
                        }
                        """
                        )
                }
        )
)
public @interface CreateMenuRequestBody {
}