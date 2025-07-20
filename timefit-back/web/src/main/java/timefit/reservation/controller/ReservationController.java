package timefit.reservation.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.service.ReservationService;

import java.util.UUID;

/**
 * 고객용 예약 관리
 * - 예약 신청, 조회, 수정, 취소 기능
 */
@Slf4j
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 신청
     * 권한: 인증된 고객
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<ReservationResponseDto.ReservationDetail> createReservation(
            @Valid @RequestBody ReservationRequestDto.CreateReservation request,
            HttpServletRequest httpRequest) {
        UUID currentUserId = getCurrentUserId(httpRequest);

        log.info("예약 신청 요청: userId={}, businessId={}, availableSlotId={}",
                currentUserId, request.getBusinessId(), request.getAvailableSlotId());

        return reservationService.createReservation(request, currentUserId);
    }

    /**
     * 내 예약 목록 조회
     * 권한: 인증된 고객 본인
     */
    @GetMapping
    public ResponseData<ReservationResponseDto.ReservationListResult> getMyReservations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) UUID businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        UUID currentUserId = getCurrentUserId(request);

        log.info("내 예약 목록 조회 요청: userId={}, status={}, startDate={}, endDate={}",
                currentUserId, status, startDate, endDate);

        return reservationService.getMyReservations(currentUserId, status, startDate, endDate, businessId, page, size);
    }

    /**
     * 예약 상세 조회
     * 권한: 예약 소유자 본인
     */
    @GetMapping("/{reservationId}")
    public ResponseData<ReservationResponseDto.ReservationDetailWithHistory> getReservationDetail(
            @PathVariable UUID reservationId,
            HttpServletRequest request) {
        UUID currentUserId = getCurrentUserId(request);

        log.info("예약 상세 조회 요청: reservationId={}, userId={}", reservationId, currentUserId);

        return reservationService.getReservationDetail(reservationId, currentUserId);
    }

    /**
     * 예약 수정
     * 권한: 예약 소유자 본인
     */
    @PutMapping("/{reservationId}")
    public ResponseData<ReservationResponseDto.ReservationDetailWithHistory> updateReservation(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationRequestDto.UpdateReservation request,
            HttpServletRequest httpRequest) {
        UUID currentUserId = getCurrentUserId(httpRequest);

        log.info("예약 수정 요청: reservationId={}, userId={}, newDate={}, newTime={}",
                reservationId, currentUserId, request.getReservationDate(), request.getReservationTime());

        return reservationService.updateReservation(reservationId, currentUserId, request);
    }


    //    --- util

    /**
     * 현재 사용자 ID 추출 (Refactor 할 때 Common 단위로 빼는거 생각해야합니다.)
     * 그리고 생각해보니 여긴 Controller 인데
     */
    private UUID getCurrentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("사용자 인증 정보가 없습니다. AuthFilter 확인 필요");
        }

        return (UUID) userId;
    }
}
