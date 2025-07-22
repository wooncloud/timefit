package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.common.ResponseData;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationCreateService createService;
    private final ReservationQueryService queryService;
    private final ReservationDetailService detailService;
    private final ReservationUpdateService updateService;
    private final ReservationCancelService reservationCancelService;
    private final BusinessReservationService businessReservationService;


    /**
     * 예약 신청
     */
    @Transactional
    public ResponseData<ReservationResponseDto.ReservationDetail> createReservation(
            ReservationRequestDto.CreateReservation request, UUID customerId) {
        return createService.createReservation(request, customerId);
    }

    /**
     * 내 예약 목록 조회
     */
    public ResponseData<ReservationResponseDto.ReservationListResult> getMyReservations(
            UUID customerId, String status, String startDate, String endDate, UUID businessId, int page, int size) {
        return queryService.getMyReservations(customerId, status, startDate, endDate, businessId, page, size);
    }

    /**
     * 예약 상세 조회
     */
    public ResponseData<ReservationResponseDto.ReservationDetailWithHistory> getReservationDetail(
            UUID reservationId, UUID customerId) {
        return detailService.getReservationDetail(reservationId, customerId);
    }

    /**
     * 예약 수정
     */
    @Transactional
    public ResponseData<ReservationResponseDto.ReservationDetailWithHistory> updateReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.UpdateReservation request) {
        return updateService.updateReservation(reservationId, customerId, request);
    }

    /**
     * 예약 취소
     */
    public ResponseData<ReservationResponseDto.ReservationCancelResult> cancelReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.CancelReservation request) {

        return reservationCancelService.cancelReservation(reservationId, customerId, request);
    }

    /**
     * 업체의 받은 예약 신청 조회
     */
    public ResponseData<ReservationResponseDto.BusinessReservationListResult> getBusinessReservations(
            UUID businessId, UUID currentUserId, String status, LocalDate date,
            LocalDate startDate, LocalDate endDate, int page, int size) {

        return businessReservationService.getBusinessReservations(
                businessId, currentUserId, status, date, startDate, endDate, page, size);
    }

}