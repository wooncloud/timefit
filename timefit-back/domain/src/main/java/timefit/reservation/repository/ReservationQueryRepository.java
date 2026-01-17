package timefit.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import timefit.booking.entity.BookingSlot;
import timefit.common.entity.DayOfWeek;
import timefit.menu.entity.Menu;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// 복잡한 검색 조건이나 동적 쿼리, 페이징 처리만 QueryDSL로 구현
public interface ReservationQueryRepository {

    // 고객 예약 조회 (필터링, 페이징)
    Page<Reservation> findMyReservationsWithFilters(UUID customerId, ReservationStatus status,
                                                    LocalDate startDate, LocalDate endDate, UUID businessId,
                                                    Pageable pageable);

    // 업체 예약 조회 (필터링, 페이징)
    Page<Reservation> findBusinessReservationsWithFilters(
            UUID businessId, ReservationStatus status, String customerName,
            LocalDate startDate, LocalDate endDate, Pageable pageable
    );

    /**
     * 특정 업체의 특정 요일 미래 예약 조회
     * - 영업시간 변경 전 예약 검증에 사용
     * - 예약 날짜 >= currentDate
     * - 상태: PENDING, CONFIRMED만
     * - 요일 필터링
     *
     * @param businessId 업체 ID
     * @param dayOfWeek 요일 (0=일요일 ~ 6=토요일)
     * @param currentDate 현재 날짜 (이 날짜 이후의 예약만 조회)
     * @return 진행 중인 예약 목록 (날짜/시간 오름차순)
     */
    List<Reservation> findFutureReservationsByBusinessAndDayOfWeek(
            UUID businessId,
            DayOfWeek dayOfWeek,
            LocalDate currentDate
    );

    /**
     * 특정 업체의 특정 날짜 활성 예약 조회
     * 활성 예약 정의:
     * - PENDING (확정 대기) 또는 CONFIRMED (확정됨) 상태
     * - CANCELLED, NO_SHOW, COMPLETED는 제외
     * 사용 예시:
     * - 파마 08:00-10:00 예약 (CONFIRMED)
     * - 헤어컷 08:00 시도 시 → 충돌 체크용 조회
     *
     * @param businessId 업체 ID
     * @param date 예약 날짜
     * @return 활성 예약 리스트 (reservationTime 순서, menu 페치 조인)
     */
    List<Reservation> findActiveReservationsByBusinessAndDate(
            UUID businessId,
            LocalDate date
    );

    /**
     * 예약 생성을 위한 BookingSlot 조회 (fetch join)
     * N+1 방지: BookingSlot + Business + Menu를 한 번에 조회
     *
     * @param slotId BookingSlot ID
     * @return BookingSlot (Business, Menu fetch join)
     */
    Optional<BookingSlot> findBookingSlotWithBusinessAndMenu(UUID slotId);

    /**
     * 예약 생성을 위한 Menu 조회 (fetch join)
     * N+1 방지: Menu + Business를 한 번에 조회
     *
     * @param menuId Menu ID
     * @return Menu (Business fetch join)
     */
    Optional<Menu> findMenuWithBusiness(UUID menuId);
}