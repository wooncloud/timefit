package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Reservation Facade Service
 *
 * 역할:
 * - Command/Query Service에 단순 위임
 * - 트랜잭션 경계 설정
 * - Controller와 Service 계층 사이의 단일 진입점
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationCommandService commandService;
    private final ReservationQueryService queryService;

    // ========== 예약 생성 ==========

    /**
     * 슬롯 기반 예약 생성 (RESERVATION_BASED)
     */
    @Transactional
    public ReservationResponseDto.ReservationDetail createReservationBased(
            ReservationRequestDto.CreateReservation request, UUID customerId) {
        return commandService.createReservationBased(request, customerId);
    }

    /**
     * 즉시 주문 예약 생성 (ONDEMAND_BASED)
     */
    @Transactional
    public ReservationResponseDto.ReservationDetail createOnDemandBased(
            ReservationRequestDto.CreateReservation request, UUID customerId) {
        return commandService.createOnDemandBased(request, customerId);
    }

    // ========== 예약 수정/취소 (고객) ==========

    /**
     * 예약 수정
     */
    @Transactional
    public ReservationResponseDto.ReservationDetailWithHistory updateReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.UpdateReservation request) {
        return commandService.updateReservation(reservationId, customerId, request);
    }

    /**
     * 예약 취소
     */
    @Transactional
    public ReservationResponseDto.ReservationCancelResult cancelReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.CancelReservation request) {
        return commandService.cancelReservation(reservationId, customerId, request);
    }

    // ========== 예약 조회 (고객) ==========

    /**
     * 내 예약 목록 조회
     */
    public ReservationResponseDto.ReservationListResult getMyReservations(
            UUID customerId, String status, String startDate, String endDate,
            UUID businessId, int page, int size) {
        return queryService.getMyReservations(customerId, status, startDate, endDate, businessId, page, size);
    }

    /**
     * 예약 상세 조회
     */
    public ReservationResponseDto.ReservationDetailWithHistory getReservationDetail(
            UUID reservationId, UUID customerId) {
        return queryService.getReservationDetail(reservationId, customerId);
    }

    // ========== 예약 관리 (업체) ==========

    /**
     * 업체 예약 목록 조회
     */
    public ReservationResponseDto.BusinessReservationListResult getBusinessReservations(
            UUID businessId, UUID currentUserId, String status,
            LocalDate startDate, LocalDate endDate, int page, int size) {
        return queryService.getBusinessReservations(businessId, currentUserId, status, startDate, endDate, page, size);
    }

    /**
     * 예약 승인
     */
    @Transactional
    public ReservationResponseDto.ReservationStatusChangeResult approveReservation(
            UUID businessId, UUID reservationId, UUID currentUserId) {
        return commandService.approveReservation(businessId, reservationId, currentUserId);
    }

    /**
     * 예약 거절
     */
    @Transactional
    public ReservationResponseDto.ReservationStatusChangeResult rejectReservation(
            UUID businessId, UUID reservationId, UUID currentUserId, String reason) {
        return commandService.rejectReservation(businessId, reservationId, currentUserId, reason);
    }

    /**
     * 예약 완료 처리
     */
    @Transactional
    public ReservationResponseDto.ReservationCompletionResult completeReservation(
            UUID businessId, UUID reservationId, UUID currentUserId, String notes) {
        return commandService.completeReservation(businessId, reservationId, currentUserId, notes);
    }

    /**
     * 노쇼 처리
     */
    @Transactional
    public ReservationResponseDto.ReservationCompletionResult markAsNoShow(
            UUID businessId, UUID reservationId, UUID currentUserId, String notes) {
        return commandService.markAsNoShow(businessId, reservationId, currentUserId, notes);
    }
}