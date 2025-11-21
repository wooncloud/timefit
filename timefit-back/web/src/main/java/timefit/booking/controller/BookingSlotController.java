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

@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}/booking-slot")
@RequiredArgsConstructor
public class BookingSlotController {

    private final BookingSlotService bookingSlotService;

    /**
     * 슬롯 생성
     * 허용된 날짜+시간대만 받아서 슬롯 생성
     * OperatingHours 검증 자동 수행
     */
    @PostMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.CreationResult>> createSlots(
            @PathVariable UUID businessId,
            @Valid @RequestBody BookingSlotRequest.BookingSlot request,
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 생성 요청: businessId={}, menuId={}", businessId, request.menuId());

        BookingSlotResponse.CreationResult response = bookingSlotService.createSlots(
                businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 특정 날짜의 슬롯 조회
     * GET /api/business/{businessId}/booking-slot?date=2025-01-15
     */
    @GetMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getSlotsByDate(
            @PathVariable UUID businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("특정 날짜 슬롯 조회 요청: businessId={}, date={}", businessId, date);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getSlotsByDate(
                businessId, date);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 기간별 슬롯 조회
     * GET /api/business/{businessId}/booking-slot/range?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/range")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getSlotsByDateRange(
            @PathVariable UUID businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("기간별 슬롯 조회 요청: businessId={}, startDate={}, endDate={}",
                businessId, startDate, endDate);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getSlotsByDateRange(
                businessId, startDate, endDate);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 메뉴별 슬롯 조회
     * GET /api/business/{businessId}/booking-slot/menu/{menuId}?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/menu/{menuId}")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getSlotsByMenu(
            @PathVariable UUID businessId,
            @PathVariable UUID menuId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("메뉴별 슬롯 조회 요청: businessId={}, menuId={}, startDate={}, endDate={}",
                businessId, menuId, startDate, endDate);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getSlotsByMenu(
                businessId, menuId, startDate, endDate);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 향후 활성 슬롯 조회
    @GetMapping("/upcoming")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getUpcomingSlots(
            @PathVariable UUID businessId) {

        log.info("향후 활성 슬롯 조회 요청: businessId={}", businessId);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getUpcomingSlots(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 슬롯 삭제
    @DeleteMapping("/{slotId}")
    public ResponseEntity<ResponseData<Void>> deleteSlot(
            @PathVariable UUID businessId,
            @PathVariable UUID slotId,
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 삭제 요청: businessId={}, slotId={}", businessId, slotId);

        bookingSlotService.deleteSlot(businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(null));
    }

    // 슬롯 비활성화
    @PatchMapping("/{slotId}/deactivate")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlot>> deactivateSlot(
            @PathVariable UUID businessId,
            @PathVariable UUID slotId,
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 비활성화 요청: businessId={}, slotId={}", businessId, slotId);

        BookingSlotResponse.BookingSlot response = bookingSlotService.deactivateSlot(
                businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 슬롯 활성화
    @PatchMapping("/{slotId}/activate")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlot>> activateSlot(
            @PathVariable UUID businessId,
            @PathVariable UUID slotId,
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 재활성화 요청: businessId={}, slotId={}", businessId, slotId);

        BookingSlotResponse.BookingSlot response = bookingSlotService.activateSlot(
                businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 과거 슬롯 일괄 삭제 (정리 작업)
    @DeleteMapping("/past")
    public ResponseEntity<ResponseData<Integer>> deletePastSlots(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("과거 슬롯 일괄 삭제 요청: businessId={}", businessId);

        Integer deletedCount = bookingSlotService.deletePastSlots(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(deletedCount));
    }
}