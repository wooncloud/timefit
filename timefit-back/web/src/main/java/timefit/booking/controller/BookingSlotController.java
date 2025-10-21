package timefit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.service.BookingSlotService;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * BookingSlot Controller
 * 기존 파일: ScheduleController.java
 *
 * API 경로 변경:
 * - 기존: /api/businesses/{businessId}/schedule/*
 * - 변경: /api/businesses/{businessId}/booking-slots/*
 */
@Slf4j
@RestController
@RequestMapping("/api/businesses/{businessId}/booking-slots")
@RequiredArgsConstructor
public class BookingSlotController {

    private final BookingSlotService bookingSlotService;

    /**
     * 단일 슬롯 생성
     * POST /api/businesses/{businessId}/booking-slots
     */
    @PostMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotDetail>> createSlot(
            @PathVariable UUID businessId,
            @Valid @RequestBody BookingSlotRequest.CreateSlot request,
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 생성 요청: businessId={}, menuId={}", businessId, request.getMenuId());

        BookingSlotResponse.SlotDetail response = bookingSlotService.createSlot(
                businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 여러 슬롯 일괄 생성
     * POST /api/businesses/{businessId}/booking-slots/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotCreationResult>> createMultipleSlots(
            @PathVariable UUID businessId,
            @Valid @RequestBody BookingSlotRequest.CreateMultipleSlots request,
            @CurrentUserId UUID currentUserId) {

        log.info("여러 슬롯 일괄 생성 요청: businessId={}, startDate={}, endDate={}",
                businessId, request.getStartDate(), request.getEndDate());

        BookingSlotResponse.SlotCreationResult response = bookingSlotService.createMultipleSlots(
                businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 특정 날짜의 예약 가능한 슬롯 조회
     * GET /api/businesses/{businessId}/booking-slots?date=2025-01-15
     */
    @GetMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotList>> getSlotsByDate(
            @PathVariable UUID businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("특정 날짜 슬롯 조회 요청: businessId={}, date={}", businessId, date);

        BookingSlotResponse.SlotList response = bookingSlotService.getAvailableSlots(
                businessId, date);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 기간별 슬롯 조회
     * GET /api/businesses/{businessId}/booking-slots/range?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/range")
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotList>> getSlotsByDateRange(
            @PathVariable UUID businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("기간별 슬롯 조회 요청: businessId={}, startDate={}, endDate={}",
                businessId, startDate, endDate);

        BookingSlotResponse.SlotList response = bookingSlotService.getSlotsByDateRange(
                businessId, startDate, endDate);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 날짜별로 그룹핑된 슬롯 조회 (캘린더용)
     * GET /api/businesses/{businessId}/booking-slots/calendar?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/calendar")
    public ResponseEntity<ResponseData<List<BookingSlotResponse.DailySlots>>> getDailySlotsGrouped(
            @PathVariable UUID businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("날짜별 그룹핑 슬롯 조회 요청: businessId={}, startDate={}, endDate={}",
                businessId, startDate, endDate);

        List<BookingSlotResponse.DailySlots> response = bookingSlotService.getDailySlotsGrouped(
                businessId, startDate, endDate);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 특정 메뉴의 슬롯 조회
     * GET /api/businesses/{businessId}/booking-slots/menu/{menuId}?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/menu/{menuId}")
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotList>> getSlotsByMenu(
            @PathVariable UUID businessId,
            @PathVariable UUID menuId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("메뉴별 슬롯 조회 요청: businessId={}, menuId={}", businessId, menuId);

        BookingSlotResponse.SlotList response = bookingSlotService.getSlotsByMenu(
                businessId, menuId, startDate, endDate);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 오늘 이후 활성 슬롯 조회
     * GET /api/businesses/{businessId}/booking-slots/upcoming
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotList>> getUpcomingSlots(
            @PathVariable UUID businessId) {

        log.info("향후 슬롯 조회 요청: businessId={}", businessId);

        BookingSlotResponse.SlotList response = bookingSlotService.getUpcomingSlots(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 슬롯 삭제
     * DELETE /api/businesses/{businessId}/booking-slots/{slotId}
     */
    @DeleteMapping("/{slotId}")
    public ResponseEntity<ResponseData<Void>> deleteSlot(
            @PathVariable UUID businessId,
            @PathVariable UUID slotId,
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 삭제 요청: businessId={}, slotId={}", businessId, slotId);

        bookingSlotService.deleteSlot(businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(null));
    }

    /**
     * 슬롯 비활성화
     * PATCH /api/businesses/{businessId}/booking-slots/{slotId}/deactivate
     */
    @PatchMapping("/{slotId}/deactivate")
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotDetail>> deactivateSlot(
            @PathVariable UUID businessId,
            @PathVariable UUID slotId,
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 비활성화 요청: businessId={}, slotId={}", businessId, slotId);

        BookingSlotResponse.SlotDetail response = bookingSlotService.deactivateSlot(
                businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 슬롯 재활성화
     * PATCH /api/businesses/{businessId}/booking-slots/{slotId}/activate
     */
    @PatchMapping("/{slotId}/activate")
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotDetail>> activateSlot(
            @PathVariable UUID businessId,
            @PathVariable UUID slotId,
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 재활성화 요청: businessId={}, slotId={}", businessId, slotId);

        BookingSlotResponse.SlotDetail response = bookingSlotService.activateSlot(
                businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 과거 슬롯 일괄 삭제 (정리 작업)
     * DELETE /api/businesses/{businessId}/booking-slots/past
     */
    @DeleteMapping("/past")
    public ResponseEntity<ResponseData<Integer>> deletePastSlots(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("과거 슬롯 일괄 삭제 요청: businessId={}", businessId);

        Integer deletedCount = bookingSlotService.deletePastSlots(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(deletedCount));
    }
}