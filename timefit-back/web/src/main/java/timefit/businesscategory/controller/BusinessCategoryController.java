package timefit.businesscategory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.business.entity.BusinessTypeCode;
import timefit.businesscategory.dto.BusinessCategoryRequest;
import timefit.businesscategory.dto.BusinessCategoryResponse;
import timefit.businesscategory.dto.CategoryListResponse;
import timefit.businesscategory.service.BusinessCategoryCommandService;
import timefit.businesscategory.service.BusinessCategoryQueryService;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;

import java.util.UUID;

/**
 * BusinessCategory 관리 API
 * 담당 기능:
 * - 카테고리 CRUD
 * - 업종별 카테고리 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}")
@RequiredArgsConstructor
public class BusinessCategoryController {

    private final BusinessCategoryCommandService businessCategoryCommandService;
    private final BusinessCategoryQueryService businessCategoryQueryService;

    /**
     * 카테고리 목록 조회 (복수형)
     * GET /api/business/{businessId}/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ResponseData<CategoryListResponse>> getCategoryList(
            @PathVariable UUID businessId,
            @RequestParam(required = false) BusinessTypeCode businessType) {

        log.info("카테고리 목록 조회 요청: businessId={}, businessType={}", businessId, businessType);

        CategoryListResponse response = (businessType != null)
                ? businessCategoryQueryService.getCategoryListByType(businessId, businessType)
                : businessCategoryQueryService.getCategoryList(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 카테고리 상세 조회 (단수형)
     * GET /api/business/{businessId}/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<BusinessCategoryResponse>> getCategory(
            @PathVariable UUID businessId,
            @PathVariable UUID categoryId) {

        log.info("카테고리 상세 조회 요청: businessId={}, categoryId={}", businessId, categoryId);

        BusinessCategoryResponse response = businessCategoryQueryService.getCategory(businessId, categoryId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 카테고리 생성 (단수형)
     * POST /api/business/{businessId}/category
     */
    @PostMapping("/category")
    public ResponseEntity<ResponseData<BusinessCategoryResponse>> createCategory(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessCategoryRequest.CreateCategory request,
            @CurrentUserId UUID currentUserId) {

        log.info("카테고리 생성 요청: businessId={}, categoryName={}, userId={}",
                businessId, request.getCategoryName(), currentUserId);

        BusinessCategoryResponse response = businessCategoryCommandService.createCategory(
                businessId,
                request,
                currentUserId
        );

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 카테고리 수정 (단수형)
     * PATCH /api/business/{businessId}/category/{categoryId}
     */
    @PatchMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<BusinessCategoryResponse>> updateCategory(
            @PathVariable UUID businessId,
            @PathVariable UUID categoryId,
            @Valid @RequestBody BusinessCategoryRequest.UpdateCategory request,
            @CurrentUserId UUID currentUserId) {

        log.info("카테고리 수정 요청: businessId={}, categoryId={}, userId={}",
                businessId, categoryId, currentUserId);

        BusinessCategoryResponse response = businessCategoryCommandService.updateCategory(
                businessId,
                categoryId,
                request,
                currentUserId
        );

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 카테고리 삭제 (논리 삭제, 단수형)
     * DELETE /api/business/{businessId}/category/{categoryId}
     */
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<ResponseData<Void>> deleteCategory(
            @PathVariable UUID businessId,
            @PathVariable UUID categoryId,
            @CurrentUserId UUID currentUserId) {

        log.info("카테고리 삭제 요청: businessId={}, categoryId={}, userId={}",
                businessId, categoryId, currentUserId);

        businessCategoryCommandService.deleteCategory(businessId, categoryId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(null));
    }
}