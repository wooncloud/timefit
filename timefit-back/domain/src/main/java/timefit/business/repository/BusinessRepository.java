package timefit.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.Business;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {

    Optional<Business> findById(UUID id);

    // 사업자번호 관련
    boolean existsByBusinessNumber(String businessNumber);

    // 통계용 기본 메서드들
    long count();
}