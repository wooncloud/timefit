package timefit.reservation.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import timefit.common.entity.DayOfWeek;
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
    public Page<Reservation> findBusinessReservationsWithFilters(
            UUID businessId, ReservationStatus status, String customerName,
            LocalDate startDate, LocalDate endDate, Pageable pageable) {


        List<Reservation> reservations = queryFactory
                .selectFrom(reservation)
                .where(
                        businessIdEq(businessId),
                        statusEq(status),
                        customerNameContains(customerName),
                        reservationDateGoe(startDate),
                        reservationDateLoe(endDate)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(
                        reservation.reservationDate.desc(),
                        reservation.reservationTime.desc()
                )
                .fetch();

        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(
                        businessIdEq(businessId),
                        statusEq(status),
                        customerNameContains(customerName),
                        reservationDateGoe(startDate),
                        reservationDateLoe(endDate)
                )
                .fetchOne();

        return new PageImpl<>(reservations, pageable, total != null ? total : 0);
    }

    // -----------  private (BooleanExpression)

    private BooleanExpression businessIdEq(UUID businessId) {
        return businessId != null ? reservation.business.id.eq(businessId) : null;
    }

    private BooleanExpression statusEq(ReservationStatus status) {
        return status != null ? reservation.status.eq(status) : null;
    }

    // 고객명 검색 (대소문자 무시)
    private BooleanExpression customerNameContains(String customerName) {
        return customerName != null ?
                reservation.customerName.containsIgnoreCase(customerName) : null;
    }

    private BooleanExpression reservationDateGoe(LocalDate startDate) {
        return startDate != null ? reservation.reservationDate.goe(startDate) : null;
    }

    private BooleanExpression reservationDateLoe(LocalDate endDate) {
        return endDate != null ? reservation.reservationDate.loe(endDate) : null;
    }

    // 정렬 조건 변환
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

    @Override
    public List<Reservation> findFutureReservationsByBusinessAndDayOfWeek(
            UUID businessId,
            DayOfWeek dayOfWeek,
            LocalDate currentDate) {

        return queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.business.id.eq(businessId),
                        reservation.reservationDate.goe(currentDate),
                        reservation.status.in(
                                ReservationStatus.PENDING,
                                ReservationStatus.CONFIRMED
                        ),
                        // 요일 필터 (PostgreSQL EXTRACT 함수 사용)
                        Expressions.numberTemplate(
                                Integer.class,
                                "EXTRACT(DOW FROM {0})",
                                reservation.reservationDate
                        ).eq(dayOfWeek.getValue())
                )
                .orderBy(
                        reservation.reservationDate.asc(),
                        reservation.reservationTime.asc()
                )
                .fetch();
    }
}