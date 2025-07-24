package timefit.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.service.BusinessReservationService;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 업체용 예약 관리
 * - 받은 예약 조회, 승인/거절, 완료 처리, 캘린더 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/businesses/{businessId}/reservations")
@RequiredArgsConstructor
public class BusinessReservationController {

    private final BusinessReservationService businessReservationService;

    /**
     * 받은 예약 신청 조회
     * 권한: OWNER, MANAGER, MEMBER (모든 업체 구성원)
     */
    @GetMapping
    public ResponseData<ReservationResponseDto.BusinessReservationListResult> getBusinessReservations(
            @PathVariable UUID businessId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        UUID currentUserId = getCurrentUserId(request);

        log.info("업체 예약 목록 조회 요청: businessId={}, userId={}, status={}, date={}",
                businessId, currentUserId, status, date);

        return businessReservationService.getBusinessReservations(
                businessId, currentUserId, status, date, startDate, endDate, page, size);
    }

    /**
     * 예약 승인/거절
     * 권한: OWNER, MANAGER만 가능
     */
    @PatchMapping("/{reservationId}/status")
    public ResponseData<ReservationResponseDto.ReservationStatusChangeResult> changeReservationStatus(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationRequestDto.ChangeReservationStatus request,
            HttpServletRequest httpRequest) {

        UUID currentUserId = getCurrentUserId(httpRequest);

        log.info("예약 상태 변경 요청: businessId={}, reservationId={}, userId={}, newStatus={}",
                businessId, reservationId, currentUserId, request.getStatus());

        return businessReservationService.changeReservationStatus(
                businessId, reservationId, currentUserId, request);
    }

    /**
     * 예약 완료/노쇼 처리
     * 권한: OWNER, MANAGER만 가능
     */
    @PatchMapping("/{reservationId}/complete")
    public ResponseData<ReservationResponseDto.ReservationStatusChangeResult> completeReservation(
            @PathVariable UUID businessId,
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationRequestDto.CompleteReservation request,
            HttpServletRequest httpRequest) {

        UUID currentUserId = getCurrentUserId(httpRequest);

        log.info("예약 완료/노쇼 처리 요청: businessId={}, reservationId={}, userId={}, status={}",
                businessId, reservationId, currentUserId, request.getStatus());

        return businessReservationService.completeReservation(
                businessId, reservationId, currentUserId, request);
    }



    // 현재 사용자 ID 추출
    private UUID getCurrentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("사용자 인증 정보가 없습니다. AuthFilter 확인 필요");
        }

        return (UUID) userId;
    }
}