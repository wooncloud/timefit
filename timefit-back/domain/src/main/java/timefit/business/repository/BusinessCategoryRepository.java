package timefit.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessCategoryRepository extends JpaRepository<BusinessCategory, UUID> {

    /**
     * 특정 업체의 특정 업종 + 카테고리 코드로 조회
     * - 중복 검증용
     *
     * @param businessId 업체 ID
     * @param businessType 업종 코드
     * @param categoryCode 카테고리 코드
     * @return 카테고리 (Optional)
     */
    Optional<BusinessCategory> findByBusinessIdAndBusinessTypeAndCategoryCode(
            UUID businessId,
            BusinessTypeCode businessType,
            timefit.business.entity.ServiceCategoryCode categoryCode);
}