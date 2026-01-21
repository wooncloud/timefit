package timefit.common.swagger.operation.businesscategory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.businesscategory.dto.BusinessCategoryResponseDto;
import timefit.common.ResponseData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "카테고리 상세 조회",
        description = """
                특정 카테고리의 상세 정보를 조회합니다.
                
                1. Path Parameter
                   - businessId: 업체 ID (UUID)
                   - categoryId: 카테고리 ID (UUID)
                
                2. 권한
                   - 인증된 사용자 (업체 소속 확인)
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessCategoryResponseDto.Category.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "BUSINESS_ACCESS_DENIED - 다른 업체의 카테고리에 접근 시도",
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
        example = "30000000-0000-0000-0000-000000000001"
)
@Parameter(
        name = "categoryId",
        description = "카테고리 ID",
        required = true,
        example = "10000000-0000-0000-0000-000000000001"
)
public @interface GetCategoryDetailOperation {
}