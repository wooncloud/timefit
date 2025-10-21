package timefit.booking.repository;

import timefit.booking.entity.BookingSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingSlotQueryRepository {

    // 업체의 특정 날짜 활성 슬롯들만
    List<BookingSlot> findAvailableSlotsByBusinessAndDate(UUID businessId, LocalDate slotDate);

    // 업체의 특정 기간 슬롯 조회
    List<BookingSlot> findByBusinessIdAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate);

    // 업체의 오늘 이후 활성 슬롯 조회
    List<BookingSlot> findUpcomingActiveSlotsByBusinessId(UUID businessId);

    // 업체의 특정 날짜 슬롯 개수
    Long countByBusinessIdAndSlotDate(UUID businessId, LocalDate slotDate);

    // 업체의 특정 날짜 슬롯과 예약 현황 함께 조회
    List<BookingSlot> findSlotsWithBookingCountByBusinessAndDate(UUID businessId, LocalDate date);

    // 슬롯의 활성 예약 수 계산
    Integer countActiveReservationsBySlot(UUID slotId);
}