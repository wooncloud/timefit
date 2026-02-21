package timefit.reservation.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import timefit.booking.entity.BookingSlot;
import timefit.common.entity.DayOfWeek;
import timefit.reservation.entity.QReservation;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static timefit.business.entity.QBusiness.business;
import static timefit.menu.entity.QMenu.menu;
import static timefit.business.entity.QBusinessCategory.businessCategory;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QReservation reservation = QReservation.reservation;

    /**
     * 고객 예약 조회 (필터링, 페이징)
     * N+1 방지: Business, Menu, BusinessCategory fetch join
     */
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
                .join(reservation.business, business).fetchJoin()
                .join(reservation.menu, menu).fetchJoin()
                .join(menu.businessCategory, businessCategory).fetchJoin()
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
     * 업체 예약 조회 (필터링, 페이징)
     * N+1 방지: Business, Menu, BusinessCategory fetch join
     */
    @Override
    public Page<Reservation> findBusinessReservationsWithFilters(
            UUID businessId, ReservationStatus status, String customerName,
            LocalDate startDate, LocalDate endDate, Pageable pageable) {

        // 쿼리 실행 - N+1 방지를 위한 fetch join
        List<Reservation> reservations = queryFactory
                .selectFrom(reservation)
                .join(reservation.business, business).fetchJoin()
                .join(reservation.menu, menu).fetchJoin()
                .join(menu.businessCategory, businessCategory).fetchJoin()
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

        // 1. DB에서 미래 예약 전체 조회 (요일 필터 제외)
        List<Reservation> allFutureReservations = queryFactory
                .selectFrom(reservation)
                .where(
                        reservation.business.id.eq(businessId),
                        reservation.reservationDate.goe(currentDate),
                        reservation.status.in(
                                ReservationStatus.PENDING,
                                ReservationStatus.CONFIRMED
                        )
                )
                .orderBy(
                        reservation.reservationDate.asc(),
                        reservation.reservationTime.asc()
                )
                .fetch();

        // 2. Java에서 요일 필터링
        // Java DayOfWeek: MONDAY=1 ~ SUNDAY=7
        // DayOfWeek enum: SUNDAY=0, MONDAY=1 ~ SATURDAY=6
        return allFutureReservations.stream()
                .filter(r -> {
                    java.time.DayOfWeek javaDayOfWeek = r.getReservationDate().getDayOfWeek();
                    // Java DayOfWeek를 0-6 범위로 변환 (일요일=0)
                    int dayValue = (javaDayOfWeek.getValue() % 7); // SUNDAY=7 -> 0, MONDAY=1 -> 1, ...
                    return dayValue == dayOfWeek.getValue();
                })
                .collect(Collectors.toList());
    }

    /**
     * [Phase 2] 특정 업체의 특정 날짜 활성 예약 조회
     *
     * QueryDSL 구현:
     * - menu 페치 조인으로 N+1 방지
     * - PENDING, CONFIRMED 상태만 조회
     * - reservationTime 오름차순 정렬
     */
    @Override
    public List<Reservation> findActiveReservationsByBusinessAndDate(
            UUID businessId,
            LocalDate date) {

        return queryFactory
                .selectFrom(reservation)
                .join(reservation.menu, menu).fetchJoin()  // menu 페치 조인
                .where(
                        reservation.business.id.eq(businessId),
                        reservation.reservationDate.eq(date),
                        reservation.status.in(
                                ReservationStatus.PENDING,
                                ReservationStatus.CONFIRMED
                        )
                )
                .orderBy(reservation.reservationTime.asc())
                .fetch();
    }

    /**
     * 예약 생성을 위한 BookingSlot 조회 (fetch join)
     * N+1 방지: BookingSlot + Business + Menu를 한 번에 조회
     */
    @Override
    public Optional<BookingSlot> findBookingSlotWithBusinessAndMenu(UUID slotId) {
        timefit.booking.entity.QBookingSlot bookingSlot = timefit.booking.entity.QBookingSlot.bookingSlot;

        timefit.booking.entity.BookingSlot result = queryFactory
                .selectFrom(bookingSlot)
                .join(bookingSlot.business).fetchJoin()
                .join(bookingSlot.menu).fetchJoin()
                .where(bookingSlot.id.eq(slotId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 예약 생성을 위한 Menu 조회 (fetch join)
     * N+1 방지: Menu + Business를 한 번에 조회
     */
    @Override
    public Optional<timefit.menu.entity.Menu> findMenuWithBusiness(UUID menuId) {
        timefit.menu.entity.QMenu menu = timefit.menu.entity.QMenu.menu;

        timefit.menu.entity.Menu result = queryFactory
                .selectFrom(menu)
                .join(menu.business).fetchJoin()
                .where(menu.id.eq(menuId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}