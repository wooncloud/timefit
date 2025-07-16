package timefit.reservation.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import timefit.reservation.entity.QReservation;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @Override
    public Page<Reservation> findMyReservationsWithFilters(UUID customerId, ReservationStatus status,
                                                            LocalDate startDate, LocalDate endDate, UUID businessId,
                                                            Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 기본 조건: 내 예약만
        builder.and(reservation.customer.id.eq(customerId));

        // 상태 필터
        if (status != null) {
            builder.and(reservation.status.eq(status));
        }

        // 날짜 범위 필터
        if (startDate != null && endDate != null) {
            builder.and(reservation.reservationDate.between(startDate, endDate));
        } else if (startDate != null) {
            builder.and(reservation.reservationDate.goe(startDate));
        } else if (endDate != null) {
            builder.and(reservation.reservationDate.loe(endDate));
        } else {
            // 기본값: 최근 3개월
            LocalDate defaultStart = LocalDate.now().minusMonths(3);
            builder.and(reservation.reservationDate.goe(defaultStart));
        }

        // 업체 필터
        if (businessId != null) {
            builder.and(reservation.business.id.eq(businessId));
        }

        // 쿼리 실행
        List<Reservation> reservations = queryFactory
                .selectFrom(reservation)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(reservations, pageable, total != null ? total : 0);
    }

    /**
     * 정렬 조건 변환
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (sort.isEmpty()) {
            orders.add(new OrderSpecifier<>(Order.DESC, reservation.reservationDate));
            orders.add(new OrderSpecifier<>(Order.DESC, reservation.reservationTime));
        } else {
            sort.forEach(order -> {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                String property = order.getProperty();

                switch (property) {
                    case "reservationDate":
                        orders.add(new OrderSpecifier<>(direction, reservation.reservationDate));
                        break;
                    case "reservationTime":
                        orders.add(new OrderSpecifier<>(direction, reservation.reservationTime));
                        break;
                    case "status":
                        orders.add(new OrderSpecifier<>(direction, reservation.status));
                        break;
                    case "createdAt":
                        orders.add(new OrderSpecifier<>(direction, reservation.createdAt));
                        break;
                    default:
                        orders.add(new OrderSpecifier<>(Order.DESC, reservation.reservationDate));
                }
            });
        }

        return orders.toArray(new OrderSpecifier[0]);
    }
}