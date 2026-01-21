package timefit.booking.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import timefit.booking.entity.BookingSlot;
import timefit.booking.entity.QBookingSlot;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static timefit.business.entity.QBusiness.business;
import static timefit.menu.entity.QMenu.menu;
import static timefit.reservation.entity.QReservation.reservation;


@Repository
@RequiredArgsConstructor
public class BookingSlotQueryRepositoryImpl implements BookingSlotQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QBookingSlot bookingSlot = QBookingSlot.bookingSlot;

    @Override
    public List<BookingSlot> findByBusinessIdAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(bookingSlot)
                .join(bookingSlot.menu, menu).fetchJoin()
                .join(bookingSlot.business, business).fetchJoin()
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
                .join(bookingSlot.menu, menu).fetchJoin()
                .join(bookingSlot.business, business).fetchJoin()
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
    public Integer countActiveReservationsBySlot(UUID slotId) {
        Long count = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(
                        reservation.bookingSlot.id.eq(slotId)
                                .and(reservation.status.in(
                                        ReservationStatus.PENDING,
                                        ReservationStatus.CONFIRMED
                                ))
                )
                .fetchOne();

        return count != null ? Math.toIntExact(count) : 0;
    }

    @Override
    public List<UUID> findSlotIdsWithAnyReservations(List<UUID> slotIds) {
        if (slotIds == null || slotIds.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory
                .select(reservation.bookingSlot.id)
                .from(reservation)
                .where(reservation.bookingSlot.id.in(slotIds))
                .distinct()  // 중복 제거
                .fetch();
    }
}