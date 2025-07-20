package timefit.schedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import timefit.schedule.dto.ScheduleRequestDto;
import timefit.schedule.dto.ScheduleResponseDto;
import timefit.schedule.service.ScheduleService;
import timefit.common.ResponseData;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 예약 스케줄 관리 컨트롤러
 * - OWNER, MANAGER: 슬롯 생성, 수정, 삭제 가능
 * - 모든 사용자: 슬롯 조회 가능 (고객용)
 */
@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 영업시간 설정
     * 권한: OWNER, MANAGER만 가능
     */
    @PutMapping("/operating-hours")
    public ResponseData<ScheduleResponseDto.OperatingHoursResult> setOperatingHours(
            @RequestParam UUID businessId,
            @RequestBody ScheduleRequestDto.SetOperatingHours request,
            HttpServletRequest httpRequest) {

        UUID currentUserId = getCurrentUserId(httpRequest);
        log.info("영업시간 설정 요청: businessId={}, userId={}", businessId, currentUserId);

        return scheduleService.setOperatingHours(businessId, request, currentUserId);
    }

    /**
     * 영업시간 조회 (인증 불필요)
     */
    @GetMapping("/operating-hours")
    public ResponseData<ScheduleResponseDto.OperatingHoursResult> getOperatingHours(
            @RequestParam UUID businessId) {

        log.info("영업시간 조회 요청: businessId={}", businessId);

        return scheduleService.getOperatingHours(businessId);
    }


    /**
     * 예약 슬롯 생성
     * 권한: OWNER, MANAGER만 가능
     */
    @PostMapping("/available-slots")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<ScheduleResponseDto.SlotDetail> createSlot(
            @RequestParam UUID businessId,
            @RequestBody ScheduleRequestDto.CreateSlot request,
            HttpServletRequest httpRequest) {

        UUID currentUserId = getCurrentUserId(httpRequest);
        log.info("예약 슬롯 생성 요청: businessId={}, userId={}", businessId, currentUserId);

        return scheduleService.createSlot(businessId, request, currentUserId);
    }

    /**
     * 여러 슬롯 일괄 생성
     * 권한: OWNER, MANAGER만 가능
     */
    @PostMapping("/available-slots/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<ScheduleResponseDto.SlotCreationResult> createMultipleSlots(
            @RequestParam UUID businessId,
            @RequestBody ScheduleRequestDto.CreateMultipleSlots request,
            HttpServletRequest httpRequest) {

        UUID currentUserId = getCurrentUserId(httpRequest);
        log.info("다중 예약 슬롯 생성 요청: businessId={}, userId={}, count={}",
                businessId, currentUserId, request.getSlots().size());

        return scheduleService.createMultipleSlots(businessId, request, currentUserId);
    }

    /**
     * 날짜별 예약 슬롯 조회 (고객용 - 인증 불필요)
     */
    @GetMapping("/available-slots")
    public ResponseData<ScheduleResponseDto.DailySlotsResult> getDailySlots(
            @RequestParam UUID businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("날짜별 예약 슬롯 조회 요청: businessId={}, date={}", businessId, date);

        return scheduleService.getDailySlots(businessId, date);
    }

    /**
     * 현재 사용자 ID 추출
     */
    private UUID getCurrentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("사용자 인증 정보가 없습니다. AuthFilter 확인 필요");
        }
        return (UUID) userId;
    }
}