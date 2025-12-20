package timefit.common.swagger.operation.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.menu.dto.MenuResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "메뉴 수정",
        description = """
            메뉴 정보를 수정합니다.
            
            1. Path Parameter
                - businessId: 업체 ID (UUID)
                - menuId: 메뉴 ID (UUID)
            
            2. Request Body (모두 선택)
                - businessType: 업종 코드
                - categoryName: 카테고리명
                - serviceName: 서비스명
                - price: 가격
                - description: 서비스 설명
                - orderType: 서비스 유형
                - durationMinutes: 소요 시간
                - imageUrl: 이미지 URL
                - autoGenerateSlots: 슬롯 자동 생성 여부
                - slotSettings: 슬롯 생성 설정
            
            3. 수정 규칙
                - null이 아닌 필드만 수정
                - null 필드는 기존 값 유지
            
            4. 제약사항
                - orderType이 RESERVATION_BASED일 때 durationMinutes 필수
                - autoGenerateSlots가 true일 때 slotSettings 필수
                - 업체에 존재하는 카테고리만 사용 가능
            
            5. 권한
                - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MenuResponseDto.Menu.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                
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
                responseCode = "404",
                description = "MENU_NOT_FOUND - 메뉴를 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = """
                BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음
                
                MENU_ACCESS_DENIED - 해당 업체의 메뉴가 아님
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface UpdateMenuOperation {
}