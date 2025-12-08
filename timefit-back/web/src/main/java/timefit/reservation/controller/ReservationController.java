package timefit.reservation.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.common.swagger.operation.reservation.*;
import timefit.common.swagger.requestbody.reservation.*;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.service.ReservationService;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "07. 예약 관리", description = "예약 관리 API (고객용/업체용)")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // ========================================
    // 고객용 API: /api/reservation
    // ========================================

    @CreateReservationOperation
    @PostMapping("/api/reservation")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservation>> createReservation(
            @CreateReservationRequestBody
            @Valid @RequestBody ReservationRequestDto.CreateReservation request,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("예약 생성 요청: businessId={}, menuId={}, customerId={}",
                request.businessId(), request.menuId(), customerId);

        ReservationResponseDto.CustomerReservation response;

        // 예약 타입 판별 로직 (DTO 에서 제거됨)
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

    @GetMyReservationsOperation
    @GetMapping("/api/reservations")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservationList>> getMyReservations(
            @Parameter(
                    description = "예약 상태 (PENDING/CONFIRMED/REJECTED/CANCELLED/COMPLETED/NO_SHOW)",
                    example = "CONFIRMED"
            )
            @RequestParam(required = false) String status,
            @Parameter(description = "시작 날짜 (YYYY-MM-DD)", example = "2025-11-01")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "종료 날짜 (YYYY-MM-DD)", example = "2025-11-30")
            @RequestParam(required = false) String endDate,
            @Parameter(description = "업체 ID 필터", example = "550e8400-e29b-41d4-a716-446655440001")
            @RequestParam(required = false) UUID businessId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("내 예약 목록 조회 요청: customerId={}, status={}, page={}", customerId, status, page);
        ReservationResponseDto.CustomerReservationList response = reservationService.getMyReservations(
                customerId, status, startDate, endDate, businessId, page, size);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @GetReservationDetailOperation
    @GetMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservation>> getReservationDetail(
            @Parameter(description = "예약 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID reservationId,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("예약 상세 조회 요청: reservationId={}, customerId={}", reservationId, customerId);
        ReservationResponseDto.CustomerReservation response = reservationService.getReservationDetail(
                reservationId, customerId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @UpdateReservationOperation
    @PutMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservation>> updateReservation(
            @Parameter(description = "예약 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID reservationId,
            @UpdateReservationRequestBody
            @Valid @RequestBody ReservationRequestDto.UpdateReservation request,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("예약 수정 요청: reservationId={}, customerId={}", reservationId, customerId);
        ReservationResponseDto.CustomerReservation response = reservationService.updateReservation(
                reservationId, customerId, request);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @CancelReservationOperation
    @DeleteMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> cancelReservation(
            @Parameter(description = "예약 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID reservationId,
            @CancelReservationRequestBody
            @Valid @RequestBody ReservationRequestDto.CancelReservation request,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("예약 취소 요청: reservationId={}, customerId={}", reservationId, customerId);
        ReservationResponseDto.ReservationActionResult response = reservationService.cancelReservation(
                reservationId, customerId, request);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // ========================================
    // 업체용 API: /api/business/{businessId}/reservations
    // ========================================

    @GetBusinessReservationsOperation
    @GetMapping("/api/business/{businessId}/reservations")
    public ResponseEntity<ResponseData<ReservationResponseDto.BusinessReservationList>> getBusinessReservations(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "예약 상태", example = "PENDING")
            @RequestParam(required = false) String status,
            @Parameter(description = "시작 날짜", example = "2025-11-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료 날짜", example = "2025-11-30")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 예약 목록 조회 요청: businessId={}, userId={}, status={}",
                businessId, currentUserId, status);

        ReservationResponseDto.BusinessReservationList response = reservationService.getBusinessReservations(
                businessId, currentUserId, status, startDate, endDate, page, size);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @GetBusinessReservationDetailOperation
    @GetMapping("/api/business/{businessId}/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.BusinessReservation>> getBusinessReservationDetail(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "예약 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID reservationId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 예약 상세 조회 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.BusinessReservation response = reservationService
                .getBusinessReservationDetail(businessId, reservationId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @ApproveReservationOperation
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/approve")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> approveReservation(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "예약 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID reservationId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("예약 승인 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.approveReservation(
                businessId, reservationId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @RejectReservationOperation
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/reject")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> rejectReservation(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "예약 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID reservationId,
            @Parameter(description = "거절 사유", example = "예약 가능 시간이 아닙니다")
            @RequestBody(required = false) String notes,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("예약 거절 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.rejectReservation(
                businessId, reservationId, currentUserId, notes);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @CompleteReservationOperation
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/complete")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> completeReservation(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "예약 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID reservationId,
            @Parameter(description = "완료 메모", example = "서비스가 정상적으로 완료되었습니다")
            @RequestBody(required = false) String notes,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("예약 완료 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.completeReservation(
                businessId, reservationId, currentUserId, notes);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @MarkAsNoShowOperation
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/no-show")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> markAsNoShow(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "예약 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID reservationId,
            @Parameter(description = "노쇼 메모", example = "예약 시간에 나타나지 않음")
            @RequestBody(required = false) String notes,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("노쇼 처리 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.markAsNoShow(
                businessId, reservationId, currentUserId, notes);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}