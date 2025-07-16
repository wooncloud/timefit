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

        log.info("예약 신청 요청: userId={}, businessId={}, date={}, time={}",
                currentUserId, request.getBusinessId(), request.getReservationDate(), request.getReservationTime());

        return reservationService.createReservation(request, currentUserId);
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
