package timefit.businesscategory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.repository.BusinessCategoryRepository;
import timefit.businesscategory.dto.BusinessCategoryResponseDto;
import timefit.businesscategory.service.validator.BusinessCategoryValidator;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessCategoryQueryService {

    private final BusinessCategoryRepository businessCategoryRepository;
    private final BusinessCategoryValidator businessCategoryValidator;

    /**
     * 업체의 모든 카테고리 목록 조회 (활성/비활성 포함)
     *
     * @param businessId 업체 ID
     * @return 카테고리 목록 응답 DTO
     */
    public BusinessCategoryResponseDto.CategoryList getCategoryList(UUID businessId) {
        log.info("카테고리 목록 조회: businessId={}", businessId);

        List<BusinessCategory> categories = businessCategoryRepository
                .findByBusinessIdOrderByBusinessTypeAscCategoryNameAsc(businessId);

        return BusinessCategoryResponseDto.CategoryList.of(categories);
    }

    /**
     * 특정 업종의 카테고리 목록 조회
     *
     * @param businessId 업체 ID
     * @param businessType 업종 코드
     * @return 카테고리 목록 응답 DTO
     */
    public BusinessCategoryResponseDto.CategoryList getCategoryListByType(
            UUID businessId,
            BusinessTypeCode businessType) {

        log.info("업종별 카테고리 목록 조회: businessId={}, businessType={}",
                businessId, businessType);

        List<BusinessCategory> categories = businessCategoryRepository
                .findByBusinessIdAndBusinessTypeAndIsActiveTrueOrderByCategoryNameAsc(
                        businessId, businessType);

        return BusinessCategoryResponseDto.CategoryList.of(categories);
    }

    /**
     * 카테고리 상세 조회
     *
     * @param businessId 업체 ID
     * @param categoryId 카테고리 ID
     * @return 카테고리 상세 응답 DTO
     */
    public BusinessCategoryResponseDto.Category getCategory(UUID businessId, UUID categoryId) {
        log.info("카테고리 상세 조회: businessId={}, categoryId={}", businessId, categoryId);

        // 조회 및 검증
        BusinessCategory category = businessCategoryValidator.validateExists(categoryId);
        businessCategoryValidator.validateCategoryBelongsToBusiness(category, businessId);

        return BusinessCategoryResponseDto.Category.of(category);
    }
}