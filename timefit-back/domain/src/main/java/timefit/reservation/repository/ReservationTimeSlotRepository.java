package timefit.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.reservation.entity.ReservationTimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationTimeSlotRepository extends JpaRepository<ReservationTimeSlot, UUID>, ReservationTimeSlotRepositoryCustom {

    // 기본 JPA 메서드들
    Optional<ReservationTimeSlot> findByBusinessIdAndSlotDateAndStartTimeAndEndTime(
            UUID businessId, LocalDate slotDate, LocalTime startTime, LocalTime endTime);
}