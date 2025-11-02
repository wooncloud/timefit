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

    List<Reservation> findByCustomerIdOrderByReservationDateDesc(UUID customerId);

    List<Reservation> findByBusinessIdOrderByReservationDateDesc(UUID businessId);

    List<Reservation> findByCustomerIdAndStatus(UUID customerId, ReservationStatus status);

    List<Reservation> findByBusinessIdAndStatus(UUID businessId, ReservationStatus status);

    List<Reservation> findByBookingSlotIdAndStatusNotIn(UUID bookingSlotId, List<ReservationStatus> excludedStatuses);

    long countByReservationDate(LocalDate reservationDate);
}