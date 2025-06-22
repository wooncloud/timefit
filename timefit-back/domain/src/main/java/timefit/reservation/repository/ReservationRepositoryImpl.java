package timefit.reservation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import timefit.reservation.entity.QReservation;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReservation reservation = QReservation.reservation;

    @Override
    public List<Reservation> findReservationsByCustomerOrderByDate(UUID customerId) {
        return queryFactory
                .selectFrom(reservation)
                .where(reservation.customer.id.eq(customerId))
                .orderBy(
                        reservation.reservationDate.desc(),
                        reservation.reservationTime.desc()
                )
                .fetch();
    }

    @Override
    public List<Reservation> findReservationsByBusinessOrderByDate(UUID businessId) {
        return queryFactory
                .selectFrom(reservation)
                .where(reservation.business.id.eq(businessId))
                .orderBy(
                        reservation.reservationDate.desc(),
                        reservation.reservationTime.desc()
                )
                .fetch();
    }

    @Override
    public List<Reservation> findReservationsByBusinessAndDate(UUID businessId, LocalDate reservationDate) {
        return queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.business.id.eq(businessId)
                                .and(reservation.reservationDate.eq(reservationDate))
                )
                .orderBy(reservation.reservationTime.asc())
                .fetch();
    }

    @Override
    public List<Reservation> findReservationsByBusinessAndStatus(UUID businessId, ReservationStatus status) {
        return queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.business.id.eq(businessId)
                                .and(reservation.status.eq(status))
                )
                .orderBy(
                        reservation.reservationDate.desc(),
                        reservation.reservationTime.desc()
                )
                .fetch();
    }

    @Override
    public List<Reservation> findReservationsByCustomerAndStatus(UUID customerId, ReservationStatus status) {
        return queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.customer.id.eq(customerId)
                                .and(reservation.status.eq(status))
                )
                .orderBy(
                        reservation.reservationDate.desc(),
                        reservation.reservationTime.desc()
                )
                .fetch();
    }

    @Override
    public List<Reservation> findReservationsByBusinessAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.business.id.eq(businessId)
                                .and(reservation.reservationDate.between(startDate, endDate))
                )
                .orderBy(
                        reservation.reservationDate.asc(),
                        reservation.reservationTime.asc()
                )
                .fetch();
    }

    @Override
    public List<Reservation> findReservationsByBusinessAndService(UUID businessId, UUID serviceId) {
        return queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.business.id.eq(businessId)
                                .and(reservation.service.id.eq(serviceId))
                )
                .orderBy(
                        reservation.reservationDate.desc(),
                        reservation.reservationTime.desc()
                )
                .fetch();
    }

    @Override
    public List<Reservation> findReservationsBySlotAndActiveStatuses(UUID slotId) {
        return queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.slot.id.eq(slotId)
                                .and(reservation.status.in(
                                        ReservationStatus.PENDING,
                                        ReservationStatus.CONFIRMED
                                ))
                )
                .fetch();
    }

    @Override
    public int countActiveReservationsBySlot(UUID slotId) {
        return Math.toIntExact(queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(
                        reservation.slot.id.eq(slotId)
                                .and(reservation.status.notIn(
                                        ReservationStatus.CANCELLED,
                                        ReservationStatus.NO_SHOW
                                ))
                )
                .fetchOne());
    }

    @Override
    public List<Reservation> findTodayReservationsByBusiness(UUID businessId, LocalDate today, ReservationStatus status) {
        return queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.business.id.eq(businessId)
                                .and(reservation.reservationDate.eq(today))
                                .and(reservation.status.eq(status))
                )
                .orderBy(reservation.reservationTime.asc())
                .fetch();
    }
}