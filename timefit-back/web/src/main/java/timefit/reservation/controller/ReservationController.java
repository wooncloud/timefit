package timefit.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.service.ReservationService;

import java.time.LocalDate;
import java.util.UUID;

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
     * - bookingSlotId가 있으면 RESERVATION_BASED
     * - bookingSlotId가 없고 reservationDate/Time이 있으면 ONDEMAND_BASED
     */
    @PostMapping("/api/reservation")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservation>> createReservation(
            @Valid @RequestBody ReservationRequestDto.CreateReservation request,
            @CurrentUserId UUID customerId) {

        log.info("예약 생성 요청: customerId={}, businessId={}, menuId={}",
                customerId, request.businessId(), request.menuId());

        ReservationResponseDto.CustomerReservation response;

        // 예약 타입 판별 로직 (DTO에서 제거됨)
        if (request.bookingSlotId() != null) {
            // RESERVATION_BASED
            response = reservationService.createReservationBased(request, customerId);
        } else if (request.reservationDate() != null && request.reservationTime() != null) {
            // ONDEMAND_BASED
            response = reservationService.createOnDemandBased(request, customerId);
        } else {
            throw new IllegalArgumentException("유효하지 않은 예약 타입입니다");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    // 내 예약 목록 조회
    @GetMapping("/api/reservations")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservationList>> getMyReservations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) UUID businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUserId UUID customerId) {

        log.info("내 예약 목록 조회 요청: customerId={}, status={}, page={}",
                customerId, status, page);

        ReservationResponseDto.CustomerReservationList response = reservationService.getMyReservations(
                customerId, status, startDate, endDate, businessId, page, size);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 예약 상세 조회
    @GetMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservation>> getReservationDetail(
            @PathVariable UUID reservationId,
            @CurrentUserId UUID customerId) {

        log.info("예약 상세 조회 요청: reservationId={}, customerId={}",
                reservationId, customerId);

        ReservationResponseDto.CustomerReservation response = reservationService.getReservationDetail(
                reservationId, customerId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 예약 수정
    @PutMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservation>> updateReservation(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationRequestDto.UpdateReservation request,
            @CurrentUserId UUID customerId) {

        log.info("예약 수정 요청: reservationId={}, customerId={}",
                reservationId, customerId);

        ReservationResponseDto.CustomerReservation response = reservationService.updateReservation(
                reservationId, customerId, request);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 예약 취소
    @DeleteMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> cancelReservation(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationRequestDto.CancelReservation request,
            @CurrentUserId UUID customerId) {

        log.info("예약 취소 요청: reservationId={}, customerId={}",
                reservationId, customerId);

        ReservationResponseDto.ReservationActionResult response = reservationService.cancelReservation(
                reservationId, customerId, request);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // ========================================
    // 업체용 API: /api/business/{businessId}/reservations
    // ========================================

    // 업체 예약 목록 조회
    @GetMapping("/api/business/{businessId}/reservations")
    public ResponseEntity<ResponseData<ReservationResponseDto.BusinessReservationList>> getBusinessReservations(
            @PathVariable UUID businessId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 예약 목록 조회 요청: businessId={}, userId={}, status={}",
                businessId, currentUserId, status);

        ReservationResponseDto.BusinessReservationList response = reservationService.getBusinessReservations(
                businessId, currentUserId, status, startDate, endDate, page, size);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 업체용 예약 상세 조회
    @GetMapping("/api/business/{businessId}/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.BusinessReservation>> getBusinessReservationDetail(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 예약 상세 조회 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.BusinessReservation response = reservationService
                .getBusinessReservationDetail(businessId, reservationId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 예약 승인
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/approve")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> approveReservation(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @CurrentUserId UUID currentUserId) {

        log.info("예약 승인 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.approveReservation(
                businessId, reservationId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 예약 거절
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/reject")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> rejectReservation(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @RequestBody(required = false) String reason,
            @CurrentUserId UUID currentUserId) {

        log.info("예약 거절 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.rejectReservation(
                businessId, reservationId, currentUserId, reason);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 예약 완료 처리
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/complete")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> completeReservation(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @RequestBody(required = false) String notes,
            @CurrentUserId UUID currentUserId) {

        log.info("예약 완료 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.completeReservation(
                businessId, reservationId, currentUserId, notes);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 노쇼 처리
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/no-show")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> markAsNoShow(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @RequestBody(required = false) String notes,
            @CurrentUserId UUID currentUserId) {

        log.info("노쇼 처리 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.markAsNoShow(
                businessId, reservationId, currentUserId, notes);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}