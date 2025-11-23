package timefit.businesscategory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.business.entity.BusinessTypeCode;
import timefit.businesscategory.dto.BusinessCategoryRequestDto;
import timefit.businesscategory.dto.BusinessCategoryResponseDto;
import timefit.businesscategory.service.CategoryFacadeService;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;

import java.util.UUID;

@Tag(name = "04. 메뉴 카테고리", description = "메뉴를 분류하기 위한 카테고리 관리 API (업종별 중분류)")
@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}")
@RequiredArgsConstructor
public class BusinessCategoryController {

    private final CategoryFacadeService categoryFacadeService;

    /**
     * 카테고리 목록 조회 (복수형)
     * GET /api/business/{businessId}/categories?businessType={type}
     *
     * @param businessId 업체 ID
     * @param businessType 업종 코드 (Optional - 없으면 전체 조회)
     * @return 카테고리 목록
     */
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
    @GetMapping("/categories")
    public ResponseEntity<ResponseData<BusinessCategoryResponseDto.CategoryList>> getCategoryList(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,

            @Parameter(
                    description = "업종 코드 (선택)",
                    example = "BD008",
                    schema = @Schema(
                            allowableValues = {"BD000", "BD001", "BD002", "BD003", "BD004", "BD005", "BD006", "BD007", "BD008", "BD009", "BD010", "BD011", "BD012", "BD013"}
                    )
            )
            @RequestParam(required = false) BusinessTypeCode businessType) {

        log.info("카테고리 목록 조회: businessId={}, businessType={}", businessId, businessType);

        BusinessCategoryResponseDto.CategoryList response = (businessType != null)
                ? categoryFacadeService.getCategoryListByType(businessId, businessType)
                : categoryFacadeService.getCategoryList(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 카테고리 상세 조회 (단수형)
     * GET /api/business/{businessId}/category/{categoryId}
     *
     * @param businessId 업체 ID
     * @param categoryId 카테고리 ID
     * @return 카테고리 상세 정보
     */
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
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<BusinessCategoryResponseDto.Category>> getCategory(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID categoryId) {

        log.info("카테고리 상세 조회: businessId={}, categoryId={}", businessId, categoryId);

        BusinessCategoryResponseDto.Category response =
                categoryFacadeService.getCategory(businessId, categoryId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 카테고리 생성 (단수형)
     * POST /api/business/{businessId}/category
     *
     * @param businessId 업체 ID
     * @param request 생성 요청 DTO
     * @param currentUserId 현재 사용자 ID
     * @return 생성된 카테고리 정보
     */
    @Operation(
            summary = "카테고리 생성 (업체용)",
            description = """
                    새로운 카테고리를 생성합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. Request Body 필수값
                       - businessType: 업종 코드 (BD000 ~ BD013)
                       - categoryName: 카테고리명 (2-20자, 한글/영문/숫자/공백만 가능)
                    
                    3. Request Body 선택값
                       - categoryNotice: 카테고리 공지사항 (최대 1000자)
                    
                    4. 제약사항
                       - 같은 업체 내에서 동일한 카테고리명 중복 불가
                       - businessType은 해당 업체의 업종 중 하나여야 함
                    
                    5. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "카테고리 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessCategoryResponseDto.Category.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "categoryId": "550e8400-e29b-41d4-a716-446655440000",
                                                "businessId": "550e8400-e29b-41d4-a716-446655440001",
                                                "businessType": "BD008",
                                                "categoryName": "헤어 컷",
                                                "categoryNotice": "예약 시 주의사항을 확인해주세요.",
                                                "isActive": true,
                                                "createdAt": "2025-11-23T10:00:00",
                                                "updatedAt": "2025-11-23T10:00:00"
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
                            3. 카테고리명은 2~20자여야 합니다.
                            4. 카테고리 공지사항은 1000자 이내여야 합니다.
                            
                            INVALID_CATEGORY_NAME_FORMAT - 카테고리명 형식 오류 (한글/영문/숫자/공백만 가능)
                            
                            CATEGORY_NAME_DUPLICATE - 중복된 카테고리명
                            
                            BUSINESS_TYPE_NOT_MATCHED - 업체의 업종이 아님
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "BUSINESS_ACCESS_DENIED - 권한 없음 (OWNER, MANAGER만 가능)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
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
    @PostMapping("/category")
    public ResponseEntity<ResponseData<BusinessCategoryResponseDto.Category>> createCategory(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "카테고리 생성 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessCategoryRequestDto.CreateCategory.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "businessType": "BD008",
                                              "categoryName": "헤어 컷",
                                              "categoryNotice": "예약 시 주의사항을 확인해주세요."
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody BusinessCategoryRequestDto.CreateCategory request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("카테고리 생성: businessId={}, categoryName={}", businessId, request.categoryName());

        BusinessCategoryResponseDto.Category response =
                categoryFacadeService.createCategory(businessId, request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    /**
     * 카테고리 수정 (단수형)
     * PATCH /api/business/{businessId}/category/{categoryId}
     *
     * @param businessId 업체 ID
     * @param categoryId 카테고리 ID
     * @param request 수정 요청 DTO
     * @param currentUserId 현재 사용자 ID
     * @return 수정된 카테고리 정보
     */
    @Operation(
            summary = "카테고리 수정 (업체용)",
            description = """
                    카테고리 정보를 수정합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - categoryId: 카테고리 ID (UUID)
                    
                    2. Request Body (모두 선택)
                       - categoryName: 카테고리명 (2-20자, 한글/영문/숫자/공백만 가능)
                       - categoryNotice: 카테고리 공지사항 (최대 1000자)
                       - isActive: 활성화 상태 (true/false)
                    
                    3. 수정 규칙
                       - null이 아닌 필드만 수정
                       - null 필드는 기존 값 유지
                    
                    4. 제약사항
                       - 카테고리명 변경 시 중복 확인
                    
                    5. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "카테고리 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessCategoryResponseDto.Category.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
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
    @PatchMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<BusinessCategoryResponseDto.Category>> updateCategory(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,

            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID categoryId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "카테고리 수정 요청 (변경할 필드만 입력)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessCategoryRequestDto.UpdateCategory.class),
                            examples = {
                                    @ExampleObject(
                                            name = "카테고리명만 변경",
                                            value = """
                                                    {
                                                      "categoryName": "헤어 펌"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "비활성화",
                                            value = """
                                                    {
                                                      "isActive": false
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "여러 필드 동시 변경",
                                            value = """
                                                    {
                                                      "categoryName": "스타일링",
                                                      "categoryNotice": "새로운 공지사항입니다.",
                                                      "isActive": true
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody BusinessCategoryRequestDto.UpdateCategory request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("카테고리 수정: businessId={}, categoryId={}", businessId, categoryId);

        BusinessCategoryResponseDto.Category response =
                categoryFacadeService.updateCategory(businessId, categoryId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 카테고리 삭제 (논리 삭제, 단수형)
     * DELETE /api/business/{businessId}/category/{categoryId}
     *
     * @param businessId 업체 ID
     * @param categoryId 카테고리 ID
     * @param currentUserId 현재 사용자 ID
     * @return 성공 응답
     */
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
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<Void>> deleteCategory(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,

            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID categoryId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("카테고리 삭제: businessId={}, categoryId={}", businessId, categoryId);

        categoryFacadeService.deleteCategory(businessId, categoryId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(null));
    }
}