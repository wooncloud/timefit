package timefit.common.swagger.operation.businesscategory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "카테고리 삭제 (업체용)",
        description = """
                카테고리를 비활성화합니다 (논리 삭제).
                
                1. Path Parameter
                   - businessId: 업체 ID (UUID)
                   - categoryId: 카테고리 ID (UUID)
                
                2. 삭제 처리
                   - 논리적 삭제 (isActive = false)
                   - 카테고리 데이터는 보존됨
                
                3. 제약사항
                   - 활성 메뉴가 있는 카테고리는 삭제 불가
                   - 먼저 해당 카테고리의 메뉴를 삭제하거나 비활성화해야 함
                
                4. 권한
                   - OWNER, MANAGER
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "삭제 성공 (isActive=false로 변경)",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                        CATEGORY_HAS_ACTIVE_MENUS - 활성 메뉴가 있어 삭제 불가
                        - 해당 카테고리에 활성 메뉴가 존재합니다.
                        - 먼저 메뉴를 삭제하거나 비활성화한 후 다시 시도하세요.
                        """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "BUSINESS_ACCESS_DENIED - 권한 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "CATEGORY_NOT_FOUND - 카테고리를 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
@Parameter(
        name = "businessId",
        description = "업체 ID",
        required = true,
        example = "550e8400-e29b-41d4-a716-446655440001"
)
@Parameter(
        name = "categoryId",
        description = "카테고리 ID",
        required = true,
        example = "550e8400-e29b-41d4-a716-446655440000"
)
public @interface DeleteCategoryOperation {
}