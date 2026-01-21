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
        summary = "메뉴 상세 조회",
        description = """
            업체의 특정 메뉴를 상세 조회합니다.
            
            1. Path Parameter
                - businessId: 업체 ID (UUID)
                - menuId: 메뉴 ID (UUID)
            
            2. 권한
                - 불필요 (공개 API)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MenuResponseDto.Menu.class),
                        examples = @ExampleObject(
                                value = """
                        {
                            "success": true,
                            "data": {
                            "menuId": "10000000-0000-0000-0000-000000000001",
                            "businessId": "30000000-0000-0000-0000-000000000001",
                            "serviceName": "헤어 컷",
                            "businessCategoryId": "60000000-0000-0000-0000-000000000001",
                            "businessType": "BD003",
                            "categoryName": "헤어",
                            "price": 30000,
                            "description": "기본 헤어 컷 서비스",
                            "orderType": "RESERVATION_BASED",
                            "durationMinutes": 60,
                            "imageUrl": "https://example.com/image.jpg",
                            "isActive": true,
                            "createdAt": "2025-11-23T10:00:00",
                            "updatedAt": "2025-11-23T15:30:00"
                            }
                        }
                        """
                        )
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
                description = "MENU_ACCESS_DENIED - 해당 업체의 메뉴가 아님",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface GetMenuOperation {
}