package timefit.booking.repository;

import timefit.booking.entity.BookingSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingSlotQueryRepository {


    // 업체의 특정 기간 슬롯 조회
    List<BookingSlot> findByBusinessIdAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate);

    // 업체의 오늘 이후 활성 슬롯 조회
    List<BookingSlot> findUpcomingActiveSlotsByBusinessId(UUID businessId);

    // 슬롯의 활성 예약 수 계산
    Integer countActiveReservationsBySlot(UUID slotId);

    /**
     * 예약이 있는 슬롯 ID 목록 조회 (벌크 쿼리)
     * - 상태 무관, 하나라도 예약이 있으면 포함
     * - 성능 최적화: N+1 문제 해결
     * - 단일 쿼리로 모든 슬롯의 예약 존재 여부 체크
     *
     * @param slotIds 체크할 슬롯 ID 목록
     * @return 예약이 있는 슬롯 ID 목록
     */
    List<UUID> findSlotIdsWithAnyReservations(List<UUID> slotIds);
}