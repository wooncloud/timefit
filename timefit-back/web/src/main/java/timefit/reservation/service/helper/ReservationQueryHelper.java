package timefit.reservation.service.helper;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.CustomerSortType;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationQueryRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
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

    public Map<ReservationStatus, Long> loadBusinessReservationStats(
            UUID businessId, ReservationStatus status, String customerName,
            LocalDate startDate, LocalDate endDate) {
        log.debug("업체 예약 통계 조회: businessId={}, status={}, customerName={}, dateRange={}~{}",
                businessId, status, customerName, startDate, endDate);
        return reservationQueryRepository.countByBusinessIdGroupByStatus(
                businessId, status, customerName, startDate, endDate);
    }



    // ================================================================
    // 고객 관리 — Tuple → DTO 변환 포함
    // ================================================================

    /**
     * 업체 고객 목록 조회 (검색, 정렬, 페이지네이션)
     * Repository에서 Tuple로 받아 CustomerListItem으로 변환
     */
    public Page<ReservationResponseDto.CustomerListItem> loadCustomerList(
            UUID businessId, String keyword, CustomerSortType sortBy, int page, int size) {

        log.debug("고객 목록 조회: businessId={}, keyword={}, sortBy={}, page={}",
                businessId, keyword, sortBy, page);

        Page<Tuple> tuples = reservationQueryRepository.findCustomerListByBusinessId(
                businessId, keyword, sortBy, PageRequest.of(page, size));

        List<ReservationResponseDto.CustomerListItem> items = tuples.getContent().stream()
                .map(t -> ReservationResponseDto.CustomerListItem.of(
                        t.get(0, UUID.class),       // customerId
                        t.get(1, String.class),     // customerName
                        t.get(2, String.class),     // customerPhone
                        t.get(3, Long.class),       // totalVisits
                        t.get(4, LocalDate.class),  // lastVisitDate
                        t.get(5, LocalDate.class),  // firstVisitDate
                        t.get(6, String.class)      // memo (nullable)
                ))
                .toList();

        return new PageImpl<>(items, tuples.getPageable(), tuples.getTotalElements());
    }

    /**
     * 고객 상세 요약 조회
     * Repository에서 Tuple로 받아 CustomerDetailSummary로 변환
     * 존재하지 않으면 예외 발생 (해당 업체 COMPLETED 예약 없는 고객)
     */
    public ReservationResponseDto.CustomerDetailSummary loadCustomerSummary(
            UUID businessId, UUID customerId) {

        log.debug("고객 상세 조회: businessId={}, customerId={}", businessId, customerId);

        Tuple t = reservationQueryRepository.findCustomerSummary(businessId, customerId)
                .orElseThrow(() -> {
                    log.warn("고객 없음: businessId={}, customerId={}", businessId, customerId);
                    return new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
                });

        return ReservationResponseDto.CustomerDetailSummary.of(
                t.get(0, UUID.class),       // customerId
                t.get(1, String.class),     // customerName
                t.get(2, String.class),     // customerPhone
                t.get(3, String.class),     // customerEmail
                t.get(4, Long.class),       // totalVisits
                t.get(5, LocalDate.class),  // lastVisitDate
                t.get(6, LocalDate.class),  // firstVisitDate
                t.get(7, String.class)      // memo (nullable)
        );
    }

    /**
     * 고객 최근 예약 이력 조회 (COMPLETED, 최신 10건)
     * Repository에서 Tuple로 받아 ReservationHistoryItem으로 변환
     */
    public List<ReservationResponseDto.ReservationHistoryItem> loadRecentReservations(
            UUID businessId, UUID customerId) {

        log.debug("고객 예약 이력 조회: businessId={}, customerId={}", businessId, customerId);

        return reservationQueryRepository.findRecentReservationsByCustomer(businessId, customerId)
                .stream()
                .map(t -> ReservationResponseDto.ReservationHistoryItem.of(
                        t.get(0, UUID.class),              // reservationId
                        t.get(1, LocalDate.class),         // reservationDate
                        t.get(2, LocalTime.class),         // reservationTime
                        t.get(3, String.class),            // menuServiceName
                        t.get(4, ReservationStatus.class), // status
                        t.get(5, Integer.class)            // reservationPrice
                ))
                .toList();
    }
}