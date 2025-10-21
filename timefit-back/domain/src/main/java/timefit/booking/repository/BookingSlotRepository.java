package timefit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.booking.entity.BookingSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface BookingSlotRepository extends JpaRepository<BookingSlot, UUID> {

    boolean existsByBusinessIdAndSlotDateAndStartTime(UUID businessId, LocalDate slotDate, LocalTime startTime);

    Optional<BookingSlot> findByBusinessIdAndSlotDateAndStartTimeAndEndTime(
            UUID businessId, LocalDate slotDate, LocalTime startTime, LocalTime endTime);

    List<BookingSlot> findByBusinessIdAndSlotDateOrderByStartTimeAsc(UUID businessId, LocalDate slotDate);

    List<BookingSlot> findByBusinessIdAndMenuId(UUID businessId, UUID menuId);

    List<BookingSlot> findBySlotDateBefore(LocalDate date);
}