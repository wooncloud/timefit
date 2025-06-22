package timefit.reservation.repository;

import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationRepositoryCustom {

    List<Reservation> findReservationsByCustomerOrderByDate(UUID customerId);

    List<Reservation> findReservationsByBusinessOrderByDate(UUID businessId);

    List<Reservation> findReservationsByBusinessAndDate(UUID businessId, LocalDate reservationDate);

    List<Reservation> findReservationsByBusinessAndStatus(UUID businessId, ReservationStatus status);

    List<Reservation> findReservationsByCustomerAndStatus(UUID customerId, ReservationStatus status);

    List<Reservation> findReservationsByBusinessAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate);

    List<Reservation> findReservationsByBusinessAndService(UUID businessId, UUID serviceId);

    List<Reservation> findReservationsBySlotAndActiveStatuses(UUID slotId);

    int countActiveReservationsBySlot(UUID slotId);

    List<Reservation> findTodayReservationsByBusiness(UUID businessId, LocalDate today, ReservationStatus status);
}