package timefit.reservation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import timefit.reservation.entity.QReservationTimeSlot;
import timefit.reservation.entity.ReservationTimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ReservationTimeSlotRepositoryImpl implements ReservationTimeSlotRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReservationTimeSlot reservationTimeSlot = QReservationTimeSlot.reservationTimeSlot;

    @Override
    public List<ReservationTimeSlot> findByBusinessIdAndSlotDateOrderByStartTime(UUID businessId, LocalDate slotDate) {
        return queryFactory
                .selectFrom(reservationTimeSlot)
                .where(
                        reservationTimeSlot.business.id.eq(businessId)
                                .and(reservationTimeSlot.slotDate.eq(slotDate))
                )
                .orderBy(reservationTimeSlot.startTime.asc())
                .fetch();
    }

    @Override
    public List<ReservationTimeSlot> findAvailableSlotsByBusinessAndDate(UUID businessId, LocalDate slotDate) {
        return queryFactory
                .selectFrom(reservationTimeSlot)
                .where(
                        reservationTimeSlot.business.id.eq(businessId)
                                .and(reservationTimeSlot.slotDate.eq(slotDate))
                                .and(reservationTimeSlot.isAvailable.eq(true))
                )
                .orderBy(reservationTimeSlot.startTime.asc())
                .fetch();
    }

    @Override
    public List<ReservationTimeSlot> findSlotsByBusinessAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(reservationTimeSlot)
                .where(
                        reservationTimeSlot.business.id.eq(businessId)
                                .and(reservationTimeSlot.slotDate.between(startDate, endDate))
                )
                .orderBy(
                        reservationTimeSlot.slotDate.asc(),
                        reservationTimeSlot.startTime.asc()
                )
                .fetch();
    }

    @Override
    public List<ReservationTimeSlot> findAvailableSlotsByBusinessAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(reservationTimeSlot)
                .where(
                        reservationTimeSlot.business.id.eq(businessId)
                                .and(reservationTimeSlot.slotDate.between(startDate, endDate))
                                .and(reservationTimeSlot.isAvailable.eq(true))
                )
                .orderBy(
                        reservationTimeSlot.slotDate.asc(),
                        reservationTimeSlot.startTime.asc()
                )
                .fetch();
    }
}