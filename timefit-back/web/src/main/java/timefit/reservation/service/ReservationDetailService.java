package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.common.ResponseData;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.factory.ReservationResponseFactory;
import timefit.reservation.service.util.ReservationValidationUtil;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 예약 상세 조회 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationDetailService {

    private final ReservationResponseFactory reservationResponseFactory;
    private final ReservationValidationUtil validationUtil;

    /**
     * 예약 상세 조회 (Public 진입점)
     */
    public ResponseData<ReservationResponseDto.ReservationDetailWithHistory> getReservationDetail(
            UUID reservationId, UUID customerId) {

        log.info("예약 상세 조회 시작: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 예약 존재 및 소유권 확인
        Reservation reservation = validationUtil.validateReservationOwnership(reservationId, customerId);

        // 2. 예약 수정/취소 가능성 체크
        boolean canModify = this.checkCanModify(reservation);
        boolean canCancel = this.checkCanCancel(reservation);
        LocalDateTime cancelDeadline = this.calculateCancelDeadline(reservation);

        // 3. 응답 생성
        ReservationResponseDto.ReservationDetailWithHistory response =
                reservationResponseFactory.createReservationDetailWithHistoryResponse(
                        reservation, canModify, canCancel, cancelDeadline);

        log.info("예약 상세 조회 완료: reservationId={}, status={}", reservationId, reservation.getStatus());

        return ResponseData.of(response);
    }

    /**
     * 예약 수정 가능 여부 체크 (Private)
     */
    private boolean checkCanModify(Reservation reservation) {
        return validationUtil.checkCanModify(reservation);
    }

    /**
     * 예약 취소 가능 여부 체크 (Private)
     */
    private boolean checkCanCancel(Reservation reservation) {
        return validationUtil.checkCanCancel(reservation);
    }

    /**
     * 취소 데드라인 계산 (Private)
     */
    private LocalDateTime calculateCancelDeadline(Reservation reservation) {
        return validationUtil.calculateCancelDeadline(reservation);
    }
}