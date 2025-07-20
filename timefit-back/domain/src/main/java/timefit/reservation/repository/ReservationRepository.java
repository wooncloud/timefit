package timefit.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    // 예약 번호 생성용 - 특정 날짜의 예약 개수 조회
    long countByReservationDate(LocalDate reservationDate);

    Long countBySlotIdAndStatusNotIn(UUID slotId, List<ReservationStatus> excludedStatuses);

}