package timefit.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import timefit.business.entity.BusinessCustomerMemo;

import java.util.Optional;
import java.util.UUID;

public interface BusinessCustomerMemoRepository extends JpaRepository<BusinessCustomerMemo, UUID> {

    // 메모 조회 (업체 + 고객 조합)
    Optional<BusinessCustomerMemo> findByBusinessIdAndCustomerId(UUID businessId, UUID customerId);

    // 메모 존재 여부 확인
    boolean existsByBusinessIdAndCustomerId(UUID businessId, UUID customerId);
}