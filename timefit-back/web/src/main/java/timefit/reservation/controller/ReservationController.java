package timefit.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.service.ReservationService;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Reservation Controller
 *
 * API 엔드포인트:
 * - 고객용: /api/reservations
 * - 업체용: /api/businesses/{businessId}/reservations
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // ========================================
    // 고객용 API: /api/reservations
    // ========================================

    /**
     * 예약 생성
     * POST /api/reservation
     */
    @PostMapping("/api/reservation")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<ReservationResponseDto.CustomerReservation> createReservation(
            @Valid @RequestBody ReservationRequestDto.CreateReservation request,
            @CurrentUserId UUID customerId) {

        log.info("예약 생성 요청: customerId={}, businessId={}, menuId={}",
                customerId, request.getBusinessId(), request.getMenuId());

        // 예약 타입에 따라 분기
        ReservationResponseDto.CustomerReservation response;
        if (request.isReservationBased()) {
            response = reservationService.createReservationBased(request, customerId);
        } else if (request.isOnDemandBased()) {
            response = reservationService.createOnDemandBased(request, customerId);
        } else {
            throw new IllegalArgumentException("유효하지 않은 예약 타입입니다");
        }

        return ResponseData.of(response);
    }

    /**
     * 내 예약 목록 조회
     * GET /api/reservations
     */
    @GetMapping("/api/reservations")
    public ResponseData<ReservationResponseDto.CustomerReservationList> getMyReservations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) UUID businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUserId UUID customerId) {

        log.info("내 예약 목록 조회 요청: customerId={}, status={}, page={}",
                customerId, status, page);

        return ResponseData.of(reservationService.getMyReservations(
                customerId, status, startDate, endDate, businessId, page, size));
    }

    /**
     * 예약 상세 조회
     * GET /api/reservation/{reservationId}
     */
    @GetMapping("/api/reservation/{reservationId}")
    public ResponseData<ReservationResponseDto.CustomerReservation> getReservationDetail(
            @PathVariable UUID reservationId,
            @CurrentUserId UUID customerId) {

        log.info("예약 상세 조회 요청: reservationId={}, customerId={}",
                reservationId, customerId);

        return ResponseData.of(reservationService.getReservationDetail(reservationId, customerId));
    }

    /**
     * 예약 수정
     * PUT /api/reservation/{reservationId}
     */
    @PutMapping("/api/reservation/{reservationId}")
    public ResponseData<ReservationResponseDto.CustomerReservation> updateReservation(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationRequestDto.UpdateReservation request,
            @CurrentUserId UUID customerId) {

        log.info("예약 수정 요청: reservationId={}, customerId={}",
                reservationId, customerId);

        return ResponseData.of(reservationService.updateReservation(reservationId, customerId, request));
    }

    /**
     * 예약 취소
     * DELETE /api/reservation/{reservationId}
     */
    @DeleteMapping("/api/reservation/{reservationId}")
    public ResponseData<ReservationResponseDto.ReservationActionResult> cancelReservation(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationRequestDto.CancelReservation request,
            @CurrentUserId UUID customerId) {

        log.info("예약 취소 요청: reservationId={}, customerId={}",
                reservationId, customerId);

        return ResponseData.of(reservationService.cancelReservation(reservationId, customerId, request));
    }

    // ========================================
    // 업체용 API: /api/business/{businessId}/reservations
    // ========================================

    /**
     * 업체 예약 목록 조회
     * GET /api/business/{businessId}/reservations
     */
    @GetMapping("/api/business/{businessId}/reservations")
    public ResponseData<ReservationResponseDto.BusinessReservationList> getBusinessReservations(
            @PathVariable UUID businessId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 예약 목록 조회 요청: businessId={}, userId={}, status={}",
                businessId, currentUserId, status);

        return ResponseData.of(reservationService.getBusinessReservations(
                businessId, currentUserId, status, startDate, endDate, page, size));
    }

    /**
     * 업체용 예약 상세 조회
     * GET /api/business/{businessId}/reservation/{reservationId}
     */
    @GetMapping("/api/business/{businessId}/reservation/{reservationId}")
    public ResponseData<ReservationResponseDto.BusinessReservation> getBusinessReservationDetail(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 예약 상세 조회 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.BusinessReservation response = reservationService
                .getBusinessReservationDetail(businessId, reservationId, currentUserId);

        return ResponseData.of(response);
    }

    /**
     * 예약 승인
     * POST /api/business/{businessId}/reservation/{reservationId}/approve
     */
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/approve")
    public ResponseData<ReservationResponseDto.ReservationActionResult> approveReservation(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @CurrentUserId UUID currentUserId) {

        log.info("예약 승인 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        return ResponseData.of(reservationService.approveReservation(businessId, reservationId, currentUserId));
    }

    /**
     * 예약 거절
     * POST /api/business/{businessId}/reservation/{reservationId}/reject
     */
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/reject")
    public ResponseData<ReservationResponseDto.ReservationActionResult> rejectReservation(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @RequestBody(required = false) String reason,
            @CurrentUserId UUID currentUserId) {

        log.info("예약 거절 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        return ResponseData.of(reservationService.rejectReservation(
                businessId, reservationId, currentUserId, reason));
    }

    /**
     * 예약 완료 처리
     * POST /api/business/{businessId}/reservation/{reservationId}/complete
     */
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/complete")
    public ResponseData<ReservationResponseDto.ReservationActionResult> completeReservation(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @RequestBody(required = false) String notes,
            @CurrentUserId UUID currentUserId) {

        log.info("예약 완료 처리 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        return ResponseData.of(reservationService.completeReservation(
                businessId, reservationId, currentUserId, notes));
    }

    /**
     * 노쇼 처리
     * POST /api/business/{businessId}/reservation/{reservationId}/no-show
     */
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/no-show")
    public ResponseData<ReservationResponseDto.ReservationActionResult> markAsNoShow(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @RequestBody(required = false) String notes,
            @CurrentUserId UUID currentUserId) {

        log.info("노쇼 처리 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        return ResponseData.of(reservationService.markAsNoShow(
                businessId, reservationId, currentUserId, notes));
    }
}