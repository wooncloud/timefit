package timefit.reservation.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * 예약 검증 유틸리티
 */
@Component
@RequiredArgsConstructor
public class ReservationValidationUtil {

    private final ReservationRepository reservationRepository;

    // 예약 소유권 검증
    public Reservation validateReservationOwnership(UUID reservationId, UUID customerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getCustomer().getId().equals(customerId)) {
            throw new ReservationException(ReservationErrorCode.NOT_RESERVATION_OWNER);
        }

        return reservation;
    }

    // 예약 수정 가능 여부 체크
    public boolean checkCanModify(Reservation reservation) {
        // 1. 상태 체크
        if (reservation.getStatus() != ReservationStatus.PENDING &&
                reservation.getStatus() != ReservationStatus.CONFIRMED) {
            return false;
        }

        // 2. 데드라인 체크 (24시간 전)
        LocalDateTime deadline = reservation.getReservationDate()
                .atTime(reservation.getReservationTime())
                .minusHours(24);

        return LocalDateTime.now().isBefore(deadline);
    }

    // 예약 취소 가능 여부 체크
    public boolean checkCanCancel(Reservation reservation) {
        // 1. 상태 체크
        if (reservation.getStatus() == ReservationStatus.CANCELLED ||
                reservation.getStatus() == ReservationStatus.COMPLETED ||
                reservation.getStatus() == ReservationStatus.NO_SHOW) {
            return false;
        }

        // 2. 데드라인 체크 (24시간 전)
        LocalDateTime deadline = reservation.getReservationDate()
                .atTime(reservation.getReservationTime())
                .minusHours(24);

        return LocalDateTime.now().isBefore(deadline);
    }

    // 취소 데드라인 계산
    public LocalDateTime calculateCancelDeadline(Reservation reservation) {
        return reservation.getReservationDate()
                .atTime(reservation.getReservationTime())
                .minusHours(24);
    }

    // 페이징 파라미터 검증
    public void validatePagingParameters(int page, int size) {
        if (page < 0) {
            throw new ReservationException(ReservationErrorCode.INVALID_PAGE_NUMBER);
        }
        if (size <= 0 || size > 100) {
            throw new ReservationException(ReservationErrorCode.INVALID_PAGE_SIZE);
        }
    }

    // 날짜 문자열 파싱
    public LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new ReservationException(ReservationErrorCode.INVALID_DATE_FORMAT);
        }
    }

    // 상태 문자열 파싱
    public ReservationStatus parseStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return null;
        }
        try {
            return ReservationStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ReservationException(ReservationErrorCode.INVALID_STATUS);
        }
    }
}