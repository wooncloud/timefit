package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.common.ResponseData;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationCreateService createService;
    private final ReservationQueryService queryService;
    private final ReservationDetailService detailService;

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
}