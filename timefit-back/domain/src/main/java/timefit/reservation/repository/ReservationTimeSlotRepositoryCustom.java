package timefit.reservation.repository;
import timefit.reservation.entity.ReservationTimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationTimeSlotRepositoryCustom {

    List<ReservationTimeSlot> findByBusinessIdAndSlotDateOrderByStartTime(UUID businessId, LocalDate slotDate);

    List<ReservationTimeSlot> findAvailableSlotsByBusinessAndDate(UUID businessId, LocalDate slotDate);

    List<ReservationTimeSlot> findSlotsByBusinessAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate);

    List<ReservationTimeSlot> findAvailableSlotsByBusinessAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate);

    // 슬롯의 활성 예약 수 계산
    Integer countActiveReservationsBySlot(UUID slotId);

    // 업체의 특정 날짜 슬롯과 예약 현황 함께 조회
    List<ReservationTimeSlot> findSlotsWithBookingCountByBusinessAndDate(UUID businessId, LocalDate date);
}