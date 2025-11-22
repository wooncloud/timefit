package timefit.businesscategory.controller;

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
    @GetMapping("/categories")
    public ResponseEntity<ResponseData<BusinessCategoryResponseDto.CategoryList>> getCategoryList(
            @PathVariable UUID businessId,
            @RequestParam(required = false) BusinessTypeCode businessType) {

        log.info("카테고리 목록 조회 요청: businessId={}, businessType={}", businessId, businessType);

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
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<BusinessCategoryResponseDto.Category>> getCategory(
            @PathVariable UUID businessId,
            @PathVariable UUID categoryId) {

        log.info("카테고리 상세 조회 요청: businessId={}, categoryId={}", businessId, categoryId);

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
    @PostMapping("/category")
    public ResponseEntity<ResponseData<BusinessCategoryResponseDto.Category>> createCategory(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessCategoryRequestDto.CreateCategory request,
            @CurrentUserId UUID currentUserId) {

        log.info("카테고리 생성 요청: businessId={}, categoryName={}, userId={}",
                businessId, request.categoryName(), currentUserId);

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
    @PatchMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<BusinessCategoryResponseDto.Category>> updateCategory(
            @PathVariable UUID businessId,
            @PathVariable UUID categoryId,
            @Valid @RequestBody BusinessCategoryRequestDto.UpdateCategory request,
            @CurrentUserId UUID currentUserId) {

        log.info("카테고리 수정 요청: businessId={}, categoryId={}, userId={}",
                businessId, categoryId, currentUserId);

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
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<Void>> deleteCategory(
            @PathVariable UUID businessId,
            @PathVariable UUID categoryId,
            @CurrentUserId UUID currentUserId) {

        log.info("카테고리 삭제 요청: businessId={}, categoryId={}, userId={}",
                businessId, categoryId, currentUserId);

        categoryFacadeService.deleteCategory(businessId, categoryId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(null));
    }
}