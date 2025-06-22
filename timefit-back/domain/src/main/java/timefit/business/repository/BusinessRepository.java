package timefit.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.Business;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {

    // 사업자번호 관련
    boolean existsByBusinessNumber(String businessNumber);
    Optional<Business> findByBusinessNumber(String businessNumber);

    // 업체명 검색
    List<Business> findByBusinessNameContaining(String businessName);

    // 업종별 조회
    List<Business> findByBusinessType(String businessType);

    // 주소 기반 검색
    List<Business> findByAddressContaining(String address);
}