package timefit.business.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessCustomerMemo;
import timefit.business.repository.BusinessCustomerMemoRepository;
import timefit.user.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * 고객 메모 처리 헬퍼
 * - 메모 조회 → update / create 분기 → 저장까지 메모 관련 액션 전담
 * - businessCustomerMemoRepository 단일 의존
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerMemoHelper {

    private final BusinessCustomerMemoRepository businessCustomerMemoRepository;

    /**
     * 고객 메모 upsert
     * - DB에서 직접 조회해 분기 판단 (조회 결과가 이 메서드에 종속된 행동이므로 내부 처리)
     */
    public void upsert(UUID businessId, Business business, User customer, String memo) {

        Optional<BusinessCustomerMemo> memoInDb =
                businessCustomerMemoRepository.findByBusinessIdAndCustomerId(
                        businessId, customer.getId()
                );

        if (memoInDb.isPresent()) {
            update(memoInDb.get(), memo);
        } else {
            create(business, customer, memo);
        }
    }

    // ----------------------------------------

    /**
     * 기존 메모 수정
     * - dirty checking으로 자동 flush
     */
    private void update(BusinessCustomerMemo memoInDb, String memo) {
        memoInDb.updateMemo(memo);
        log.debug("고객 메모 수정: memoId={}", memoInDb.getId());
    }

    /**
     * 신규 메모 생성 및 저장
     */
    private void create(Business business, User customer, String memo) {
        BusinessCustomerMemo newMemo = BusinessCustomerMemo.create(business, customer, memo);
        businessCustomerMemoRepository.save(newMemo);
        log.debug("고객 메모 생성: businessId={}, customerId={}", business.getId(), customer.getId());
    }
}