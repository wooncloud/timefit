package timefit.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;

import java.util.List;
import java.util.UUID;

public interface BusinessRepositoryCustom {

    /**
     * 통합 검색 - 모든 조건
     * @param keyword 업체명 또는 주소 검색어
     * @param businessTypeCode 업종 코드
     * @param region 지역 검색어
     */
    Page<Business> searchBusinesses(
            String keyword, BusinessTypeCode businessTypeCode,
            String region, Pageable pageable);
}