package timefit.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import timefit.business.entity.Business;

import java.util.List;
import java.util.UUID;

public interface BusinessRepositoryCustom {

    /**
     * 키워드 통합 검색 (업체명, 업종, 주소)
     */
    Page<Business> findByKeyword(String keyword, Pageable pageable);

    /**
     * 업체명으로 검색 (페이징)
     */
    Page<Business> findByBusinessNameContaining(String businessName, Pageable pageable);

    /**
     * 업종별 조회 (페이징)
     */
    Page<Business> findByBusinessType(String businessType, Pageable pageable);

    /**
     * 지역별 검색 (페이징)
     */
    Page<Business> findByRegion(String region, Pageable pageable);

    /**
     * 복합 검색 - 업체명과 업종
     */
    Page<Business> findByBusinessNameAndType(String businessName, String businessType, Pageable pageable);

    /**
     * 복합 검색 - 업종과 지역
     */
    Page<Business> findByBusinessTypeAndRegion(String businessType, String region, Pageable pageable);

    /**
     * 통합 검색 - 모든 조건 (DTO 의존성 제거)
     */
    Page<Business> searchBusinesses(String keyword, String businessType, String region, Pageable pageable);

    /**
     * 업체 통계 조회
     */
    long countByBusinessType(String businessType);
    long countByRegion(String region);

    /**
     * 추천 업체 조회 (활성화된 업체 중 랜덤 또는 인기순)
     */
    List<Business> findRecommendedBusinesses(int limit);

    /**
     * 특정 사용자가 속한 업체들 조회 (권한 테이블 조인)
     */
    List<Business> findBusinessesByUserId(UUID userId);
}