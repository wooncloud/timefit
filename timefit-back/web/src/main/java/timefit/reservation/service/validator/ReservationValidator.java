package timefit.reservation.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.entity.Reservation;
import timefit.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Reservation 검증 전담 클래스
 * 역할:
 * - 예약 존재 여부, 소유권, 취소 가능 여부 등 검증
 * - Entity 메서드 활용 (isCancellable 등)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationRepository reservationRepository;

    private static final int MAX_SEARCH_YEARS = 5;

    /**
     * 예약 존재 여부 검증 및 조회
     *
     * @param reservationId 검증할 예약 ID
     * @return 조회된 Reservation 엔티티
     * @throws ReservationException 예약이 존재하지 않을 경우
     */
    public Reservation validateExists(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("예약 없음: reservationId={}", reservationId);
                    return new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
                });
    }

    /**
     * 예약이 특정 업체에 속하는지 검증
     *
     * @param reservation 검증할 예약 엔티티
     * @param businessId 업체 ID
     * @throws ReservationException 예약이 해당 업체에 속하지 않을 경우
     */
    public void validateBelongsToBusiness(Reservation reservation, UUID businessId) {
        if (!reservation.getBusiness().getId().equals(businessId)) {
            log.warn("예약이 해당 업체에 속하지 않음: reservationId={}, businessId={}",
                    reservation.getId(), businessId);
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }
    }

    /**
     * 예약 소유자인지 검증 (고객 본인 확인)
     *
     * @param reservation 검증할 예약 엔티티
     * @param customerId 고객(사용자) ID
     * @throws ReservationException 예약 소유자가 아닐 경우
     */
    public void validateOwner(Reservation reservation, UUID customerId) {
        if (!reservation.getCustomer().getId().equals(customerId)) {
            log.warn("예약 소유자 아님: reservationId={}, customerId={}",
                    reservation.getId(), customerId);
            throw new ReservationException(ReservationErrorCode.NOT_RESERVATION_OWNER);
        }
    }

    /**
     *
     * 예약 취소 가능 여부 검증
     * Entity의 isCancellable() 메서드 사용
     * @param reservation 검증할 예약 엔티티
     * @throws ReservationException 취소 불가능한 상태일 경우
     */
    public void validateCancellable(Reservation reservation) {
        if (!reservation.isCancellable()) {
            log.warn("취소 불가능한 예약: reservationId={}, status={}",
                    reservation.getId(), reservation.getStatus());
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_CANCELLABLE);
        }
    }

    /**
     * 과거 날짜 예약 방지 검증
     *
     * @param reservationDate 예약 날짜
     * @throws ReservationException 과거 날짜일 경우
     */
    public void validateNotPastDate(LocalDate reservationDate) {
        if (reservationDate.isBefore(LocalDate.now())) {
            log.warn("과거 날짜 예약 시도: {}", reservationDate);
            throw new ReservationException(ReservationErrorCode.RESERVATION_PAST_DATE);
        }
    }

    /**
     * 예약 존재 및 업체 소속 동시 검증
     *
     * @param reservationId 예약 ID
     * @param businessId 업체 ID
     * @return 조회 및 검증된 Reservation 엔티티
     * @throws ReservationException 예약이 존재하지 않거나 업체에 속하지 않을 경우
     */
    public Reservation validateOfBusiness(UUID reservationId, UUID businessId) {
        Reservation reservation = validateExists(reservationId);
        validateBelongsToBusiness(reservation, businessId);
        return reservation;
    }

    /**
     * 예약 소유자 확인 및 취소 가능 여부 동시 검증
     *
     * @param reservationId 예약 ID
     * @param customerId 고객(사용자) ID
     * @return 조회 및 검증된 Reservation 엔티티
     * @throws ReservationException 소유자가 아니거나 취소 불가능한 경우
     */
    public Reservation validateForCancel(UUID reservationId, UUID customerId) {
        Reservation reservation = validateExists(reservationId);
        validateOwner(reservation, customerId);
        validateCancellable(reservation);
        return reservation;
    }

    /**
     * 날짜 범위 검증 (5년 상한선)
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @throws ReservationException 날짜 범위가 5년을 초과하는 경우
     */
    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return; // null인 경우는 Service 에서 디폴트 처리
        }

        // 시작일이 종료일보다 미래인 경우
        if (startDate.isAfter(endDate)) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_DATE_FORMAT,
                    "시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }

        // 5년 상한선 검증
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > (MAX_SEARCH_YEARS * 365)) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_DATE_FORMAT,
                    "검색 가능한 최대 기간은 " + MAX_SEARCH_YEARS + "년입니다.");
        }

        log.debug("날짜 범위 검증 통과: {} ~ {} ({} 일)", startDate, endDate, daysBetween);
    }
}