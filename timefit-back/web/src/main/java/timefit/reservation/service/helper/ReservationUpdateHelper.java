package timefit.reservation.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.service.validator.ReservationValidator;

/**
 * Reservation 수정 로직 전담 클래스
 *
 * 역할:
 * - 예약 수정 가능 여부 검증
 * - 예약 정보 업데이트
 *
 * Menu 패턴 준수:
 * - 단일 책임: "예약 수정"만 담당
 * - 검증은 Validator에 위임
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationUpdateHelper {

    private final ReservationValidator validator;

    /**
     * 예약 정보 수정
     *
     * @param reservation 수정할 예약 엔티티
     * @param request 수정 요청
     */
    public void update(Reservation reservation, ReservationRequestDto.UpdateReservation request) {
        log.debug("예약 수정 시작: reservationId={}", reservation.getId());

        // 1. 수정 가능 여부 확인
        if (!reservation.isCancellable()) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_MODIFIABLE);
        }

        // 2. 날짜/시간 수정
        if (request.reservationDate() != null && request.reservationTime() != null) {
            validator.validateNotPastDate(request.reservationDate());
            reservation.updateReservationDateTime(request.reservationDate(), request.reservationTime());
        }

        // 3. 고객 정보 수정
        if (request.customerName() != null || request.customerPhone() != null) {
            reservation.updateCustomerInfo(request.customerName(), request.customerPhone());
        }

        // 4. 메모 수정
        if (request.notes() != null) {
            reservation.updateNotes(request.notes());
        }

        log.debug("예약 수정 완료: reservationId={}", reservation.getId());
    }
}