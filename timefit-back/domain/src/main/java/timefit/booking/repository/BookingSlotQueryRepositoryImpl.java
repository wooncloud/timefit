package timefit.booking.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import timefit.booking.entity.BookingSlot;
import timefit.booking.entity.QBookingSlot;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static timefit.reservation.entity.QReservation.reservation;


@Repository
@RequiredArgsConstructor
public class BookingSlotQueryRepositoryImpl implements BookingSlotQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QBookingSlot bookingSlot = QBookingSlot.bookingSlot;

    @Override
    public List<BookingSlot> findAvailableSlotsByBusinessAndDate(UUID businessId, LocalDate slotDate) {
        return queryFactory
                .selectFrom(bookingSlot)
                .where(
                        bookingSlot.business.id.eq(businessId)
                                .and(bookingSlot.slotDate.eq(slotDate))
                                .and(bookingSlot.isAvailable.eq(true))
                )
                .orderBy(bookingSlot.startTime.asc())
                .fetch();
    }

    @Override
    public List<BookingSlot> findByBusinessIdAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(bookingSlot)
                .where(
                        bookingSlot.business.id.eq(businessId)
                                .and(bookingSlot.slotDate.between(startDate, endDate))
                )
                .orderBy(
                        bookingSlot.slotDate.asc(),
                        bookingSlot.startTime.asc()
                )
                .fetch();
    }

    @Override
    public List<BookingSlot> findUpcomingActiveSlotsByBusinessId(UUID businessId) {
        return queryFactory
                .selectFrom(bookingSlot)
                .where(
                        bookingSlot.business.id.eq(businessId)
                                .and(bookingSlot.slotDate.goe(LocalDate.now()))
                                .and(bookingSlot.isAvailable.eq(true))
                )
                .orderBy(
                        bookingSlot.slotDate.asc(),
                        bookingSlot.startTime.asc()
                )
                .fetch();
    }

    @Override
    public Long countByBusinessIdAndSlotDate(UUID businessId, LocalDate slotDate) {
        return queryFactory
                .select(bookingSlot.count())
                .from(bookingSlot)
                .where(
                        bookingSlot.business.id.eq(businessId)
                                .and(bookingSlot.slotDate.eq(slotDate))
                )
                .fetchOne();
    }

    @Override
    public List<BookingSlot> findSlotsWithBookingCountByBusinessAndDate(UUID businessId, LocalDate date) {
        return queryFactory
                .selectFrom(bookingSlot)
                .leftJoin(bookingSlot.business).fetchJoin()
                .where(
                        bookingSlot.business.id.eq(businessId)
                                .and(bookingSlot.slotDate.eq(date))
                )
                .orderBy(bookingSlot.startTime.asc())
                .fetch();
    }

    @Override
    public Integer countActiveReservationsBySlot(UUID slotId) {
        Long count = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(
                        reservation.bookingSlot.id.eq(slotId)
                                .and(reservation.status.notIn(
                                        ReservationStatus.CANCELLED,
                                        ReservationStatus.NO_SHOW
                                ))
                )
                .fetchOne();

        return count != null ? Math.toIntExact(count) : 0;
    }
}