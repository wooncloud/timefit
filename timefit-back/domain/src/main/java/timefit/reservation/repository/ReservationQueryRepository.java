package timefit.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// 복잡한 검색 조건이나 동적 쿼리, 페이징 처리만 QueryDSL로 구현
public interface ReservationQueryRepository {

    // 업체의 특정 날짜 예약들 (시간순)
    List<Reservation> findByBusinessIdAndReservationDate(UUID businessId, LocalDate reservationDate);

    // 업체의 특정 기간 예약들
    List<Reservation> findByBusinessIdAndDateRange(UUID businessId, LocalDate startDate, LocalDate endDate);

    // 업체의 오늘 예약들 (특정 상태)
    List<Reservation> findTodayReservationsByBusinessAndStatus(UUID businessId, LocalDate today, ReservationStatus status);

    // 슬롯의 활성 예약 수 계산 (취소/노쇼 제외)
    Long countActiveReservationsBySlot(UUID slotId);

    // 고객 예약 조회 (필터링, 페이징)
    Page<Reservation> findMyReservationsWithFilters(UUID customerId, ReservationStatus status,
                                                    LocalDate startDate, LocalDate endDate, UUID businessId,
                                                    Pageable pageable);

    // 업체 예약 조회 (필터링, 페이징)
    Page<Reservation> findBusinessReservationsWithFilters(UUID businessId, ReservationStatus status,
                                                          LocalDate startDate, LocalDate endDate,
                                                          Pageable pageable);
}