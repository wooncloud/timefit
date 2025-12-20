package timefit.common.swagger.operation.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "메뉴 삭제",
        description = """
            메뉴를 비활성화합니다 (논리 삭제).
            
            1. Path Parameter
                - businessId: 업체 ID (UUID)
                - menuId: 메뉴 ID (UUID)
            
            2. 동작
                - 메뉴를 비활성 상태로 전환 (isActive = false)
                - 실제 데이터는 삭제되지 않음
            
            3. 제약사항
                - 미래 활성 예약이 없어야 함
            
            4. 권한
                - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "204",
                description = "삭제 성공"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "CANNOT_DEACTIVATE_MENU_WITH_RESERVATIONS - 미래 예약이 있어 삭제 불가",
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
public @interface DeleteMenuOperation {
}