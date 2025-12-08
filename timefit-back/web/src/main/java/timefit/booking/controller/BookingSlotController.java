package timefit.booking.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import timefit.common.swagger.operation.booking.*;
import timefit.common.swagger.requestbody.booking.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "06. 예약 슬롯", description = "예약 슬롯 생성, 조회, 관리 API (업체용)")
@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}/booking-slot")
@RequiredArgsConstructor
public class BookingSlotController {

    private final BookingSlotService bookingSlotService;

    @CreateSlotsOperation
    @PostMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.CreationResult>> createSlots(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @CreateSlotsRequestBody
            @Valid @RequestBody BookingSlotRequest.BookingSlot request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 생성 요청: businessId={}, menuId={}", businessId, request.menuId());

        BookingSlotResponse.CreationResult response = bookingSlotService.createSlots(
                businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @GetSlotsByDateOperation
    @GetMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getSlotsByDate(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", required = true, example = "2025-12-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("특정 날짜 슬롯 조회 요청: businessId={}, date={}", businessId, date);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getSlotsByDate(
                businessId, date);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @GetSlotsByDateRangeOperation
    @GetMapping("/range")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getSlotsByDateRange(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2025-12-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2025-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("기간별 슬롯 조회 요청: businessId={}, startDate={}, endDate={}",
                businessId, startDate, endDate);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getSlotsByDateRange(
                businessId, startDate, endDate);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @GetSlotsByMenuOperation
    @GetMapping("/menu/{menuId}")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getSlotsByMenu(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "메뉴 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440002")
            @PathVariable UUID menuId,
            @Parameter(description = "조회 시작 날짜 (YYYY-MM-DD)", required = true, example = "2025-12-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "조회 종료 날짜 (YYYY-MM-DD)", required = true, example = "2025-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("메뉴별 슬롯 조회 요청: businessId={}, menuId={}, startDate={}, endDate={}",
                businessId, menuId, startDate, endDate);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getSlotsByMenu(
                businessId, menuId, startDate, endDate);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @GetUpcomingSlotsOperation
    @GetMapping("/upcoming")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getUpcomingSlots(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId) {

        log.info("향후 활성 슬롯 조회 요청: businessId={}", businessId);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getUpcomingSlots(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @DeleteSlotOperation
    @DeleteMapping("/{slotId}")
    public ResponseEntity<ResponseData<Void>> deleteSlot(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "슬롯 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440003")
            @PathVariable UUID slotId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 삭제 요청: businessId={}, slotId={}", businessId, slotId);

        bookingSlotService.deleteSlot(businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(null));
    }

    @DeactivateSlotOperation
    @PatchMapping("/{slotId}/deactivate")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlot>> deactivateSlot(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "슬롯 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440003")
            @PathVariable UUID slotId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 비활성화 요청: businessId={}, slotId={}", businessId, slotId);

        BookingSlotResponse.BookingSlot response = bookingSlotService.deactivateSlot(
                businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @ActivateSlotOperation
    @PatchMapping("/{slotId}/activate")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlot>> activateSlot(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "슬롯 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440003")
            @PathVariable UUID slotId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 재활성화 요청: businessId={}, slotId={}", businessId, slotId);

        BookingSlotResponse.BookingSlot response = bookingSlotService.activateSlot(
                businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @DeletePastSlotsOperation
    @DeleteMapping("/past")
    public ResponseEntity<ResponseData<Integer>> deletePastSlots(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("과거 슬롯 일괄 삭제 요청: businessId={}", businessId);

        Integer deletedCount = bookingSlotService.deletePastSlots(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(deletedCount));
    }
}