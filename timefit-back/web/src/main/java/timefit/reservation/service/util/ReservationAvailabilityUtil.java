package timefit.reservation.service.util;

import org.springframework.stereotype.Component;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class ReservationAvailabilityUtil {

    // 과거 날짜 예약 방지
    public void validateNotPastDate(LocalDate reservationDate) {
        if (reservationDate.isBefore(LocalDate.now())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_PAST_DATE);
        }
    }

    // 예약 시간 유효성 검증
    public void validateReservationTime(LocalTime reservationTime, Integer durationMinutes) {
        // 15분 단위 시간 검증
        if (reservationTime.getMinute() % 15 != 0) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_SLOT_UNAVAILABLE);
        }

        // 최소 예약 시간 검증
        if (durationMinutes < 15) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_SLOT_UNAVAILABLE);
        }

        // 예약 시간 단위 검증 (15분 단위)
        if (durationMinutes % 15 != 0) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_SLOT_UNAVAILABLE);
        }
    }

    // 요청 인원 수 검증
    public void validateRequestedCapacity(Integer requestedCapacity) {
        if (requestedCapacity == null || requestedCapacity < 1) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_CAPACITY_EXCEEDED);
        }

        if (requestedCapacity > 10) { // 최대 10명까지 제한
            throw new ReservationException(ReservationErrorCode.RESERVATION_CAPACITY_EXCEEDED);
        }
    }
}