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

    // 고객 예약 조회 (필터링, 페이징)
    Page<Reservation> findMyReservationsWithFilters(UUID customerId, ReservationStatus status,
                                                    LocalDate startDate, LocalDate endDate, UUID businessId,
                                                    Pageable pageable);

    // 업체 예약 조회 (필터링, 페이징)
    Page<Reservation> findBusinessReservationsWithFilters(
            UUID businessId, ReservationStatus status, String customerName,
            LocalDate startDate, LocalDate endDate, Pageable pageable
    );
}