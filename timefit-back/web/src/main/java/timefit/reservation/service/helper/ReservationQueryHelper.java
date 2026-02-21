package timefit.reservation.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationQueryRepository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Reservation 조회 전담 Helper
 *
 * 역할:
 * - 예약 목록 조회 (고객용, 업체용)
 * - Repository 호출 및 결과 반환
 *
 * 책임:
 * - 조회만 담당 (DTO 변환 제외)
 * - Service는 이 Helper를 통해 데이터 조회
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationQueryHelper {

    private final ReservationQueryRepository reservationQueryRepository;

    /**
     * 내 예약 목록 조회 (고객용)
     *
     * @param customerId 고객 ID
     * @param status 예약 상태 (nullable)
     * @param startDate 시작 날짜 (nullable)
     * @param endDate 종료 날짜 (nullable)
     * @param businessId 업체 ID 필터 (nullable)
     * @param pageable 페이징 정보
     * @return 예약 페이지
     */
    public Page<Reservation> loadMyReservations(
            UUID customerId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate,
            UUID businessId,
            Pageable pageable) {

        log.debug("고객 예약 조회: customerId={}, status={}, dateRange={}~{}, businessId={}, page={}",
                customerId, status, startDate, endDate, businessId, pageable.getPageNumber());

        return reservationQueryRepository.findMyReservationsWithFilters(
                customerId, status, startDate, endDate, businessId, pageable);
    }

    /**
     * 업체 예약 목록 조회 (업체용)
     *
     * @param businessId 업체 ID
     * @param status 예약 상태 (nullable)
     * @param customerName 고객명 검색 (nullable)
     * @param startDate 시작 날짜 (nullable)
     * @param endDate 종료 날짜 (nullable)
     * @param pageable 페이징 정보
     * @return 예약 페이지
     */
    public Page<Reservation> loadBusinessReservations(
            UUID businessId,
            ReservationStatus status,
            String customerName,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        log.debug("업체 예약 조회: businessId={}, status={}, customerName={}, dateRange={}~{}, page={}",
                businessId, status, customerName, startDate, endDate, pageable.getPageNumber());

        return reservationQueryRepository.findBusinessReservationsWithFilters(
                businessId, status, customerName, startDate, endDate, pageable);
    }
}