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
        summary = "메뉴 목록 조회",
        description = """
            업체의 메뉴를 검색 및 필터링하여 조회합니다.
            
            1. Path Parameter
                - businessId: 업체 ID (UUID)
            
            2. Query Parameter (모두 선택)
                - serviceName: 서비스명 검색 (부분 일치, 대소문자 무시)
                - businessCategoryId: 카테고리 ID 필터
                - minPrice: 최소 가격
                - maxPrice: 최대 가격
                - isActive: 활성 상태 (true/false)
            
            3. 권한
                - 불필요 (공개 API)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MenuResponseDto.MenuList.class),
                        examples = @ExampleObject(
                                value = """
                        {
                            "success": true,
                            "data": {
                            "menus": [
                                {
                                "menuId": "10000000-0000-0000-0000-000000000001",
                                "serviceName": "헤어 컷",
                                "price": 30000,
                                "orderType": "RESERVATION_BASED",
                                "isActive": true
                                }
                            ],
                            "totalCount": 1
                            }
                        }
                        """
                        )
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
public @interface GetMenuListOperation {
}