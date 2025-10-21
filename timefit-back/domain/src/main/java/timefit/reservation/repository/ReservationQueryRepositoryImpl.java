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
import org.springframework.stereotype.Repository;
import timefit.reservation.entity.QReservation;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QReservation reservation = QReservation.reservation;

    @Override
    public List<Reservation> findByBusinessIdAndReservationDate(UUID businessId, LocalDate reservationDate) {
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
    public List<Reservation> findByBusinessIdAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate) {
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
    public List<Reservation> findTodayReservationsByBusinessAndStatus(UUID businessId, LocalDate today, ReservationStatus status) {
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
    public Long countActiveReservationsBySlot(UUID slotId) {
        return queryFactory
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

    @Override
    public Page<Reservation> findBusinessReservationsWithFilters(UUID businessId, ReservationStatus status,
                                                                 LocalDate startDate, LocalDate endDate,
                                                                 Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 필수 조건: 해당 업체의 예약
        builder.and(reservation.business.id.eq(businessId));

        // 상태 필터
        if (status != null) {
            builder.and(reservation.status.eq(status));
        }

        // 날짜 범위 필터
        if (startDate != null) {
            builder.and(reservation.reservationDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(reservation.reservationDate.loe(endDate));
        }

        // 정렬 조건 생성
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "reservationDate":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, reservation.reservationDate));
                        break;
                    case "reservationTime":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, reservation.reservationTime));
                        break;
                    case "createdAt":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, reservation.createdAt));
                        break;
                    case "status":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, reservation.status));
                        break;
                    default:
                        orderSpecifiers.add(new OrderSpecifier<>(direction, reservation.createdAt));
                }
            }
        } else {
            // 기본 정렬: 예약일 내림차순, 예약시간 내림차순
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, reservation.reservationDate));
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, reservation.reservationTime));
        }

        // 쿼리 실행
        List<Reservation> results = queryFactory
                .selectFrom(reservation)
                .leftJoin(reservation.customer).fetchJoin()
                .leftJoin(reservation.business).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
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