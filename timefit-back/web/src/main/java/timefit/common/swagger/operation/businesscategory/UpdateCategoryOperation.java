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
        summary = "카테고리 수정 (업체용)",
        description = """
                카테고리 정보를 수정합니다.
                
                1. Path Parameter
                   - businessId: 업체 ID (UUID)
                   - categoryId: 카테고리 ID (UUID)
                
                2. Request Body (변경할 필드만 입력)
                   - categoryName: 카테고리명 (선택, 2~20자)
                   - categoryNotice: 카테고리 공지사항 (선택, 1000자 이내)
                   - isActive: 활성 상태 (선택)
                
                3. 검증 규칙
                   - 카테고리명 변경 시 2~20자 이내
                   - 업체 내 동일 업종에서 중복 불가
                   - 공지사항은 1000자 이내
                
                4. 권한
                   - OWNER, MANAGER
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessCategoryResponseDto.Category.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                        입력 검증 오류:
                        
                        1. 카테고리명은 2~20자여야 합니다.
                        2. 카테고리 공지사항은 1000자 이내여야 합니다.
                        
                        INVALID_CATEGORY_NAME_FORMAT - 카테고리명 형식 오류
                        
                        CATEGORY_NAME_DUPLICATE - 중복된 카테고리명
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
        example = "30000000-0000-0000-0000-000000000001"
)
@Parameter(
        name = "categoryId",
        description = "카테고리 ID",
        required = true,
        example = "10000000-0000-0000-0000-000000000001"
)
public @interface UpdateCategoryOperation {
}