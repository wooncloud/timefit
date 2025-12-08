package timefit.common.swagger.operation.businesscategory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
        summary = "카테고리 목록 조회",
        description = """
                업체의 카테고리 목록을 조회합니다.
                
                1. Path Parameter
                   - businessId: 업체 ID (UUID)
                
                2. Query Parameter (선택)
                   - businessType: 업종 코드 (미입력 시 전체 조회)
                
                3. 응답
                   - categories: 카테고리 배열
                   - totalCount: 전체 카테고리 수
                   - 정렬: businessType 오름차순 → categoryName 오름차순
                
                4. 권한
                   - 인증된 사용자 (업체 소속 확인)
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessCategoryResponseDto.CategoryList.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "success": true,
                                          "data": {
                                            "categories": [
                                              {
                                                "categoryId": "550e8400-e29b-41d4-a716-446655440000",
                                                "businessId": "550e8400-e29b-41d4-a716-446655440001",
                                                "businessType": "BD008",
                                                "categoryName": "헤어 컷",
                                                "categoryNotice": "예약 시 주의사항을 확인해주세요.",
                                                "isActive": true,
                                                "createdAt": "2025-11-23T10:00:00",
                                                "updatedAt": "2025-11-23T10:00:00"
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
@Parameter(
        name = "businessId",
        description = "업체 ID",
        required = true,
        example = "550e8400-e29b-41d4-a716-446655440001"
)
@Parameter(
        name = "businessType",
        description = "업종 코드 (선택)",
        example = "BD008",
        schema = @Schema(
                allowableValues = {"BD000", "BD001", "BD002", "BD003", "BD004", "BD005", "BD006", "BD007", "BD008", "BD009", "BD010", "BD011", "BD012", "BD013"}
        )
)
public @interface GetCategoryListOperation {
}