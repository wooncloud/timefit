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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.business.entity.BusinessTypeCode;
import timefit.businesscategory.dto.BusinessCategoryRequestDto;
import timefit.businesscategory.dto.BusinessCategoryResponseDto;
import timefit.businesscategory.service.CategoryFacadeService;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;

import java.util.UUID;

@Tag(name = "카테고리", description = "업체 카테고리 관리 API")
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
                    업체의 활성 카테고리 목록을 조회합니다.
                    
                    1. Path Parameter
                        - businessId: 업체 ID (UUID)
                    
                    2. Query Parameter
                        - businessType: 업종 코드 (선택, 미입력 시 전체 조회)
                    
                    3. 응답
                        - 성공: categories 배열, totalCount
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
                                                    "businessType": "BD003",
                                                    "categoryName": "헤어 컷",
                                                    "categoryNotice": "예약 시 주의사항",
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
                    description = "NOT_FOUND - 업체를 찾을 수 없음",
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
                    example = "BD003",
                    schema = @Schema(
                            allowableValues = {
                                    "BD000 (음식점업)", "BD001 (숙박업)", "BD002 (소매/유통업)",
                                    "BD003 (미용/뷰티업)", "BD004 (의료업)", "BD005 (피트니스/스포츠업)",
                                    "BD006 (교육/문화업)", "BD007 (전문서비스업)", "BD008 (생활서비스업)",
                                    "BD009 (제조/생산업)"
                            }
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
                    description = "FORBIDDEN - 다른 업체의 카테고리에 접근 시도",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "NOT_FOUND - 카테고리를 찾을 수 없음",
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
            summary = "카테고리 생성",
            description = """
                    업체에 새로운 카테고리를 생성합니다.
                    
                    1. Path Parameter
                        - businessId: 업체 ID (UUID)
                    
                    2. Request Body 필수값
                        - businessType: 업종 코드
                        - categoryName: 카테고리명
                    
                    3. Request Body 선택값
                        - categoryNotice: 카테고리 공지사항
                    
                    4. 제약사항
                        - businessType: BD000~BD009 중 하나
                        - categoryName: 2~20자, 한글/영문/숫자/공백만 허용
                        - categoryNotice: 최대 1000자
                        - 같은 업체, 같은 업종 내에서 카테고리명 중복 불가
                    
                    5. 권한
                        - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "생성 성공",
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
                                                "businessType": "BD003",
                                                "categoryName": "헤어 컷",
                                                "categoryNotice": null,
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
                            3. categoryName 최소 길이는 2 입니다.
                            4. categoryName 최대 길이는 20 입니다.
                            5. categoryName 에 포함될 수 없는 문자가 존재합니다. (특수문자, 이모지 불가)
                            6. categoryNotice 최대 길이는 1000 입니다.
                            
                            INVALID_CATEGORY_NAME_FORMAT - 카테고리명 형식 오류
                            
                            CATEGORY_NAME_DUPLICATE - 중복된 카테고리명
                            - 같은 업체의 같은 업종 내에 동일한 카테고리명이 이미 존재합니다.
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class),
                            examples = @ExampleObject(
                                    name = "Validation Error 예시",
                                    value = """
                                            {
                                                "success": false,
                                                "code": 400,
                                                "message": "error",
                                                "error": {
                                                "code": "VALIDATION_ERROR",
                                                "message": "categoryName 은(는) 필수 값입니다."
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            FORBIDDEN - 권한 없음
                            - OWNER 또는 MANAGER 권한이 필요합니다.
                            """,
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
                                                "businessType": "BD003",
                                                "categoryName": "헤어 컷",
                                                "categoryNotice": "예약 시 주의사항을 확인해주세요."
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody BusinessCategoryRequestDto.CreateCategory request,

            @Parameter(hidden = true) @CurrentUserId UUID currentUserId) {

        log.info("카테고리 생성: businessId={}, categoryName={}", businessId, request.categoryName());

        BusinessCategoryResponseDto.Category response =
                categoryFacadeService.createCategory(businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
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
            summary = "카테고리 수정",
            description = """
                    카테고리 정보를 부분 수정합니다.
                    
                    1. Path Parameter
                        - businessId: 업체 ID (UUID)
                        - categoryId: 카테고리 ID (UUID)
                    
                    2. Request Body (모두 선택값)
                        - categoryName: 카테고리명 (변경 시에만 입력)
                        - categoryNotice: 카테고리 공지사항 (변경 시에만 입력)
                        - isActive: 활성화 상태 (변경 시에만 입력)
                    
                    3. 제약사항
                        - categoryName: 2~20자, 한글/영문/숫자/공백만 허용
                        - categoryNotice: 최대 1000자
                        - 같은 업체, 같은 업종 내에서 카테고리명 중복 불가 (자신 제외)
                    
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
                            VALIDATION_ERROR - 요청 형식 오류
                            1. categoryName 최소 길이는 2 입니다.
                            2. categoryName 최대 길이는 20 입니다.
                            3. categoryName 에 포함될 수 없는 문자가 존재합니다.
                            4. categoryNotice 최대 길이는 1000 입니다.
                            
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
                    description = "FORBIDDEN - 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "NOT_FOUND - 카테고리를 찾을 수 없음",
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
                                                        "categoryNotice": "새로운 공지사항",
                                                        "isActive": true
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody BusinessCategoryRequestDto.UpdateCategory request,

            @Parameter(hidden = true) @CurrentUserId UUID currentUserId) {

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
            summary = "카테고리 삭제",
            description = """
                    카테고리를 비활성화합니다 (논리 삭제).
                    
                    1. Path Parameter
                        - businessId: 업체 ID (UUID)
                        - categoryId: 카테고리 ID (UUID)
                    
                    2. 제약사항
                        - 활성 메뉴가 있는 카테고리는 삭제할 수 없습니다.
                        - 먼저 해당 카테고리의 메뉴를 삭제하거나 비활성화해야 합니다.
                    
                    3. 권한
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
                    description = "FORBIDDEN - 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "NOT_FOUND - 카테고리를 찾을 수 없음",
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

            @Parameter(hidden = true) @CurrentUserId UUID currentUserId) {

        log.info("카테고리 삭제: businessId={}, categoryId={}", businessId, categoryId);

        categoryFacadeService.deleteCategory(businessId, categoryId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(null));
    }
}