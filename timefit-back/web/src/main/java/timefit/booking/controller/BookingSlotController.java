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
import java.util.UUID;

/**
 * BookingSlot Controller
 * API 경로: /api/businesses/{businessId}/booking-slots
 */
@Slf4j
@RestController
@RequestMapping("/api/businesses/{businessId}/booking-slots")
@RequiredArgsConstructor
public class BookingSlotController {

    private final BookingSlotService bookingSlotService;

    /**
     * 슬롯 생성
     * POST /api/businesses/{businessId}/booking-slots
     * 허용된 날짜+시간대만 받아서 슬롯 생성
     * OperatingHours 검증 자동 수행
     */
    @PostMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotCreationResult>> createSlots(
            @PathVariable UUID businessId,
            @Valid @RequestBody BookingSlotRequest.Create request,
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 생성 요청: businessId={}, menuId={}", businessId, request.getMenuId());

        BookingSlotResponse.SlotCreationResult response = bookingSlotService.createSlots(
                businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 특정 날짜의 슬롯 조회
     * GET /api/businesses/{businessId}/booking-slots?date=2025-01-15
     */
    @GetMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.SlotList>> getSlotsByDate(
            @PathVariable UUID businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("특정 날짜 슬롯 조회 요청: businessId={}, date={}", businessId, date);

        BookingSlotResponse.SlotList response = bookingSlotService.getSlotsByDate(
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