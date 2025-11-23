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
     * 특정 업체의 활성 카테고리 목록 조회 (정렬: businessType → categoryName)
     */
    List<BusinessCategory> findByBusinessIdAndIsActiveTrueOrderByBusinessTypeAscCategoryNameAsc(
            UUID businessId
    );

    /**
     * 특정 업체의 모든 카테고리 목록 조회 (활성/비활성 포함)
     */
    List<BusinessCategory> findByBusinessIdOrderByBusinessTypeAscCategoryNameAsc(
            UUID businessId
    );

    /**
     * 특정 업체의 특정 업종 카테고리 목록 조회
     */
    List<BusinessCategory> findByBusinessIdAndBusinessTypeAndIsActiveTrueOrderByCategoryNameAsc(
            UUID businessId,
            BusinessTypeCode businessType
    );

    /**
     * 중복 검증용 (대소문자 구분)
     */
    boolean existsByBusinessIdAndBusinessTypeAndCategoryNameAndIsActiveTrue(
            UUID businessId,
            BusinessTypeCode businessType,
            String categoryName
    );

    /**
     * 카테고리 조회 (대소문자 구분)
     */
    Optional<BusinessCategory> findByBusinessIdAndBusinessTypeAndCategoryNameAndIsActiveTrue(
            UUID businessId,
            BusinessTypeCode businessType,
            String categoryName
    );

    /**
     * 카테고리 조회 (대소문자 무시)
     */
    Optional<BusinessCategory> findByBusinessIdAndBusinessTypeAndCategoryNameIgnoreCaseAndIsActiveTrue(
            UUID businessId,
            BusinessTypeCode businessType,
            String categoryName
    );



    /**
     * 특정 카테고리에 연결된 활성 메뉴 수 조회
     * (Menu 엔티티와의 관계 확인용)
     */
    // countByIdAndMenus_IsActiveTrue 대신 MenuRepository 에서 조회
}