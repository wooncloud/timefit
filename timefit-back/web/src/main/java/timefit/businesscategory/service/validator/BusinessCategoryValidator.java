package timefit.businesscategory.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.repository.BusinessCategoryRepository;
import timefit.exception.businesscategory.BusinessCategoryErrorCode;
import timefit.exception.businesscategory.BusinessCategoryException;
import timefit.menu.repository.MenuRepository;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * BusinessCategory 검증 전담 클래스
 * - BusinessCategory 존재 여부 검증
 * - 권한 검증 (업체 소속 확인)
 * - 삭제 가능 여부 검증
 * - categoryCode와 businessType 일치 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessCategoryValidator {

    private final BusinessCategoryRepository businessCategoryRepository;
    private final MenuRepository menuRepository;

    /**
     * 카테고리명 형식 검증
     * - 한글, 영문, 숫자, 공백만 허용
     * - 2~20자
     * - 특수문자, 이모지 불허
     */
    private static final Pattern CATEGORY_NAME_PATTERN =
            Pattern.compile("^[가-힣a-zA-Z0-9\\s]{2,20}$");

    /**
     * 카테고리 존재 확인
     *
     * @param categoryId 카테고리 ID
     * @return 조회된 BusinessCategory
     * @throws BusinessCategoryException 존재하지 않을 경우
     */
    public BusinessCategory validateExists(UUID categoryId) {
        return businessCategoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("카테고리를 찾을 수 없음: categoryId={}", categoryId);
                    return new BusinessCategoryException(
                            BusinessCategoryErrorCode.CATEGORY_NOT_FOUND
                    );
                });
    }

    /**
     * 카테고리가 특정 업체에 속하는지 확인
     *
     * @param category 검증할 카테고리
     * @param businessId 업체 ID
     * @throws BusinessCategoryException 업체에 속하지 않을 경우
     */
    public void validateCategoryBelongsToBusiness(BusinessCategory category, UUID businessId) {
        if (!category.belongsToBusiness(businessId)) {
            log.warn("카테고리가 해당 업체에 속하지 않음: categoryId={}, businessId={}",
                    category.getId(), businessId);
            throw new BusinessCategoryException(
                    BusinessCategoryErrorCode.CATEGORY_ACCESS_DENIED);
        }
    }

    /**
     * 카테고리명 형식 검증
     *
     * @param categoryName 검증할 카테고리명
     * @throws BusinessCategoryException 형식이 올바르지 않을 경우
     */
    public void validateCategoryNameFormat(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new BusinessCategoryException(
                    BusinessCategoryErrorCode.INVALID_CATEGORY_NAME
            );
        }

        // 앞뒤 공백 제거 후 검증
        String trimmedName = categoryName.trim();

        if (!CATEGORY_NAME_PATTERN.matcher(trimmedName).matches()) {
            log.warn("잘못된 카테고리명 형식: categoryName={}", categoryName);
            throw new BusinessCategoryException(
                    BusinessCategoryErrorCode.INVALID_CATEGORY_NAME);
        }
    }

    /**
     * 중복 카테고리 검증 (동일 업체 + 업종 + 카테고리명)
     *
     * @param businessId 업체 ID
     * @param businessType 업종 코드
     * @param categoryName 카테고리명
     * @throws BusinessCategoryException 이미 존재하는 카테고리일 경우
     */
    public void validateNoDuplicateCategory(
            UUID businessId,
            BusinessTypeCode businessType,
            String categoryName) {

        String trimmedName = categoryName.trim();

        boolean exists = businessCategoryRepository
                .existsByBusinessIdAndBusinessTypeAndCategoryNameAndIsActiveTrue(
                        businessId,
                        businessType,
                        trimmedName
                );

        if (exists) {
            log.warn("이미 존재하는 카테고리: businessId={}, businessType={}, categoryName={}",
                    businessId, businessType, trimmedName);
            throw new BusinessCategoryException(
                    BusinessCategoryErrorCode.DUPLICATE_CATEGORY);
        }
    }

    /**
     * 카테고리 수정 시 중복 검증 (자기 자신 제외)
     *
     * @param categoryId 수정할 카테고리 ID
     * @param businessId 업체 ID
     * @param businessType 업종 코드
     * @param newCategoryName 새로운 카테고리명
     */
    public void validateNoDuplicateCategoryForUpdate(
            UUID categoryId,
            UUID businessId,
            BusinessTypeCode businessType,
            String newCategoryName) {

        String trimmedName = newCategoryName.trim();

        businessCategoryRepository
                .findByBusinessIdAndBusinessTypeAndCategoryNameAndIsActiveTrue(
                        businessId,
                        businessType,
                        trimmedName
                )
                .ifPresent(existingCategory -> {
                    // 자기 자신이 아닌 경우에만 중복으로 판단
                    if (!existingCategory.getId().equals(categoryId)) {
                        log.warn("카테고리명 중복: categoryId={}, newName={}", categoryId, trimmedName);
                        throw new BusinessCategoryException(
                                BusinessCategoryErrorCode.DUPLICATE_CATEGORY);
                    }
                });
    }

    /**
     * 카테고리 삭제 가능 여부 검증 (활성 메뉴 확인)
     *
     * @param categoryId 검증할 카테고리 ID
     * @throws BusinessCategoryException 활성 메뉴가 존재할 경우
     */
    public void validateCategoryDeletable(UUID categoryId) {
        long activeMenuCount = menuRepository.countByBusinessCategoryIdAndIsActiveTrue(categoryId);

        if (activeMenuCount > 0) {
            log.warn("활성 메뉴가 있어 카테고리 삭제 불가: categoryId={}, activeMenuCount={}",
                    categoryId, activeMenuCount);
            throw new BusinessCategoryException(
                    BusinessCategoryErrorCode.CATEGORY_HAS_ACTIVE_MENUS);
        }
    }

    /**
     * BusinessCategory 조회 및 검증 (통합 메서드)
     *
     * @param businessId 업체 ID
     * @param businessType 업종 코드
     * @param categoryName 카테고리명
     * @return 조회된 BusinessCategory
     */
    public BusinessCategory validateAndGetBusinessCategory(
            UUID businessId,
            BusinessTypeCode businessType,
            String categoryName) {

        String trimmedName = categoryName.trim();

        return businessCategoryRepository
                .findByBusinessIdAndBusinessTypeAndCategoryNameAndIsActiveTrue(
                        businessId,
                        businessType,
                        trimmedName
                )
                .orElseThrow(() -> {
                    log.warn("카테고리를 찾을 수 없음: businessId={}, businessType={}, categoryName={}",
                            businessId, businessType, trimmedName);
                    return new BusinessCategoryException(
                            BusinessCategoryErrorCode.CATEGORY_NOT_FOUND);
                });
    }
}