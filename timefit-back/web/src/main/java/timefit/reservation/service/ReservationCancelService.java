package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.common.ResponseData;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.entity.ReservationTimeSlot;
import timefit.reservation.repository.ReservationRepository;
import timefit.reservation.repository.ReservationTimeSlotRepository;
import timefit.reservation.repository.ReservationRepositoryCustom;
import timefit.reservation.service.util.ReservationValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 예약 취소 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationCancelService {

    private final ReservationRepository reservationRepository;
    private final ReservationRepositoryCustom reservationRepositoryCustom;
    private final ReservationTimeSlotRepository reservationTimeSlotRepository;
    private final ReservationValidationUtil validationUtil;

    /**
     * 예약 취소
     */
    @Transactional
    public ResponseData<ReservationResponseDto.ReservationCancelResult> cancelReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.CancelReservation request) {

        log.info("예약 취소 시작: reservationId={}, customerId={}, reason={}",
                reservationId, customerId, request.getReason());

        // 1. 예약 존재 및 소유권 확인
        Reservation reservation = validationUtil.validateReservationOwnership(reservationId, customerId);

        // 2. 취소 가능 여부 검증
        this.validateReservationCancellable(reservation);

        // 3. 취소 처리
        ReservationStatus previousStatus = reservation.getStatus();
        LocalDateTime cancelledAt = LocalDateTime.now();

        reservation.cancelReservation(request.getReason());

        // 4. 예약 저장
        Reservation cancelledReservation = reservationRepository.save(reservation);

        // 5. 슬롯 재개방 처리 (다른 예약자가 예약할 수 있도록)
        this.reopenSlotIfNeeded(cancelledReservation.getSlot());

        // 6. 응답 생성
        ReservationResponseDto.ReservationCancelResult response =
                ReservationResponseDto.ReservationCancelResult.of(
                        cancelledReservation.getId(),
                        previousStatus,
                        cancelledReservation.getStatus(),
                        request.getReason(),
                        cancelledAt
                );

        log.info("예약 취소 완료: reservationId={}, previousStatus={}, currentStatus={}",
                reservationId, previousStatus, cancelledReservation.getStatus());

        return ResponseData.of(response);
    }

    /**
     * 예약 취소 가능 여부 검증
     */
    private void validateReservationCancellable(Reservation reservation) {
        if (!validationUtil.checkCanCancel(reservation)) {
            // 상태에 따른 구체적인 에러 메시지 제공
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new ReservationException(ReservationErrorCode.RESERVATION_INVALID_STATUS);
            } else if (reservation.getStatus() == ReservationStatus.COMPLETED ||
                    reservation.getStatus() == ReservationStatus.NO_SHOW) {
                throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_CANCELLABLE);
            } else {
                // 데드라인 초과
                throw new ReservationException(ReservationErrorCode.CANCELLATION_DEADLINE_PASSED);
            }
        }
    }

    /**
     * 슬롯 재개방 처리
     * 취소된 예약으로 인해 슬롯에 자리가 생기면 다시 예약 가능 상태로 변경
     */
    private void reopenSlotIfNeeded(ReservationTimeSlot slot) {
        if (slot == null) {
            log.warn("슬롯 정보가 없어 재개방 처리를 건너뜁니다");
            return;
        }

        // 해당 슬롯의 활성 예약 수 확인
        int currentActiveReservations = reservationRepositoryCustom.countActiveReservationsBySlot(slot.getId());

        log.debug("슬롯 재개방 확인: slotId={}, currentActiveReservations={}, capacity={}",
                slot.getId(), currentActiveReservations, slot.getCapacity());

        // 슬롯에 여유가 생기면 재개방
        slot.reopenIfNeeded(currentActiveReservations);

        reservationTimeSlotRepository.save(slot);

        log.info("슬롯 재개방 처리 완료: slotId={}, isAvailable={}",
                slot.getId(), slot.getIsAvailable());
    }
}