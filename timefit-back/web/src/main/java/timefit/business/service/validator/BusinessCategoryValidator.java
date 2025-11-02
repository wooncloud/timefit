package timefit.business.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.ServiceCategoryCode;
import timefit.business.repository.BusinessCategoryRepository;
import timefit.exception.businesscategory.BusinessCategoryErrorCode;
import timefit.exception.businesscategory.BusinessCategoryException;

import java.util.UUID;

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

    /**
     * BusinessCategory 존재 여부 검증 및 조회
     * (business, businessType, categoryCode 조합으로)
     *
     * @param businessId 업체 ID
     * @param businessType 업종 코드
     * @param categoryCode 카테고리 코드
     * @return 조회된 BusinessCategory 엔티티
     * @throws BusinessCategoryException 카테고리가 존재하지 않을 경우
     */
    public BusinessCategory validateAndGetBusinessCategory(
            UUID businessId,
            BusinessTypeCode businessType,
            ServiceCategoryCode categoryCode) {

        return businessCategoryRepository
                .findByBusinessIdAndBusinessTypeAndCategoryCode(businessId, businessType, categoryCode)
                .orElseThrow(() -> {

                    log.warn("존재하지 않는 BusinessCategory 조합: businessId={}, businessType={}, categoryCode={}",
                            businessId, businessType, categoryCode);

                    return new BusinessCategoryException(
                            BusinessCategoryErrorCode.CATEGORY_NOT_FOUND,
                            String.format("업체에 %s - %s 카테고리가 존재하지 않습니다",
                                    businessType.getDescription(),
                                    categoryCode.getDisplayName())
                    );
                });
    }


    /**
     * 핵심: categoryCode가 businessType에 속하는지 검증
     *
     * @param businessType 업종 코드
     * @param categoryCode 카테고리 코드
     * @throws BusinessCategoryException 카테고리가 업종에 속하지 않을 경우
     */
    public void validateCategoryCodeBelongsToBusinessType(BusinessTypeCode businessType,
                                                          ServiceCategoryCode categoryCode) {
        if (!categoryCode.belongsTo(businessType)) {

            log.warn("카테고리 코드가 업종에 속하지 않음: businessType={}, categoryCode={}",
                    businessType, categoryCode);

            throw new BusinessCategoryException(
                    BusinessCategoryErrorCode.CATEGORY_CODE_MISMATCH,
                    String.format("카테고리 %s는 업종 %s에 속하지 않습니다",
                            categoryCode.getDisplayName(),
                            businessType.getDescription())
            );
        }
    }


}