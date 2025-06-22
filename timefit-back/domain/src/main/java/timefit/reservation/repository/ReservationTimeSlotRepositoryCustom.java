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
}