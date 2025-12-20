package timefit.common.swagger.operation.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.menu.dto.MenuResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "메뉴 생성",
        description = """
            업체에 새로운 메뉴를 생성합니다.
            
            1. Path Parameter
                - businessId: 업체 ID (UUID)
            
            2. Request Body 필수값
                - businessType: 업종 코드
                - categoryName: 카테고리명
                - serviceName: 서비스명
                - price: 가격
                - orderType: 서비스 유형
            
            3. Request Body 선택값
                - description: 서비스 설명
                - imageUrl: 이미지 URL
                - durationMinutes: 소요 시간 (RESERVATION_BASED일 때 필수)
                - autoGenerateSlots: 슬롯 자동 생성 여부
                - slotSettings: 슬롯 생성 설정 (autoGenerateSlots=true일 때 필수)
            
            4. 제약사항
                - orderType이 RESERVATION_BASED일 때 durationMinutes 필수
                - autoGenerateSlots가 true일 때 slotSettings 필수
                -  업체에 존재하는 카테고리만 사용 가능
            
            5. 권한
                - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "생성 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MenuResponseDto.Menu.class),
                        examples = @ExampleObject(
                                value = """
                        {
                            "success": true,
                            "data": {
                            "menuId": "550e8400-e29b-41d4-a716-446655440000",
                            "businessId": "550e8400-e29b-41d4-a716-446655440001",
                            "serviceName": "헤어 컷",
                            "price": 30000,
                            "orderType": "RESERVATION_BASED",
                            "durationMinutes": 60,
                            "isActive": true
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
                1. businessType 은(는) 필수 값입니다.
                2. categoryName 은(는) 필수 값입니다.
                3. serviceName 은(는) 필수 값입니다.
                4. price 은(는) 필수 값입니다.
                5. orderType 은(는) 필수 값입니다.
                
                DURATION_REQUIRED_FOR_RESERVATION - 예약형 서비스는 소요 시간 필수
                
                INVALID_SLOT_SETTINGS - 슬롯 자동 생성 시 슬롯 설정 필요
                
                CATEGORY_NOT_FOUND - 카테고리를 찾을 수 없음
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
        )
})
public @interface CreateMenuOperation {
}