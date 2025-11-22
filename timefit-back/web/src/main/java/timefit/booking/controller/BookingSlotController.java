package timefit.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}/booking-slot")
@RequiredArgsConstructor
@Tag(name = "예약 슬롯 관리 (BookingSlot)", description = "업체 관리자가 예약 가능 슬롯을 생성, 조회, 수정하는 API")
public class BookingSlotController {

    private final BookingSlotService bookingSlotService;

    /**
     * 슬롯 생성
     * 허용된 날짜+시간대만 받아서 슬롯 생성
     * OperatingHours 검증 자동 수행
     */
    @Operation(summary = "예약 슬롯 일괄 생성", description = "메뉴, 간격, 날짜/시간 스케줄을 지정하여 예약 슬롯을 생성합니다. (Business Manager/Owner 권한 필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "슬롯 생성 요청 처리 완료",
                    content = @Content(schema = @Schema(implementation = BookingSlotResponse.CreationResult.class))),
            @ApiResponse(responseCode = "400", description = "요청 유효성 오류: DTO 제약, 유효하지 않은 시간 범위, 메뉴 타입 오류 (SLOT_INVALID_MENU_TYPE)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "Business/Menu ID를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
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
    @Operation(summary = "특정 날짜의 예약 슬롯 조회", description = "해당 날짜에 생성된 모든 예약 슬롯을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "슬롯 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = BookingSlotResponse.BookingSlotList.class))),
            @ApiResponse(responseCode = "404", description = "Business ID를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
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
    @Operation(summary = "기간별 예약 슬롯 조회", description = "시작 날짜부터 종료 날짜까지 생성된 모든 예약 슬롯을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "슬롯 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = BookingSlotResponse.BookingSlotList.class))),
            @ApiResponse(responseCode = "400", description = "기간 설정 오류 (시작일이 종료일보다 늦음)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "Business ID를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
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
    @Operation(summary = "메뉴별 기간 예약 슬롯 조회", description = "특정 메뉴에 대해 기간 내 생성된 예약 슬롯을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "슬롯 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = BookingSlotResponse.BookingSlotList.class))),
            @ApiResponse(responseCode = "400", description = "기간 설정 오류",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "Business ID를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
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
    @Operation(summary = "향후 활성 슬롯 조회", description = "오늘 날짜 이후 예약 가능한 상태의 모든 슬롯을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "슬롯 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = BookingSlotResponse.BookingSlotList.class))),
            @ApiResponse(responseCode = "404", description = "Business ID를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @GetMapping("/upcoming")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getUpcomingSlots(
            @PathVariable UUID businessId) {

        log.info("향후 활성 슬롯 조회 요청: businessId={}", businessId);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getUpcomingSlots(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // 슬롯 삭제
    @Operation(summary = "예약 슬롯 삭제", description = "특정 슬롯을 영구적으로 삭제합니다. (Business Manager/Owner 권한 필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "슬롯 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "활성 예약 존재로 삭제 불가 (AVAILABLE_SLOT_NOT_MODIFIABLE)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "Business/Slot ID를 찾을 수 없음 (AVAILABLE_SLOT_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
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
    @Operation(summary = "예약 슬롯 비활성화", description = "슬롯을 예약 불가능 상태로 변경합니다. (Business Manager/Owner 권한 필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "슬롯 비활성화 성공",
                    content = @Content(schema = @Schema(implementation = BookingSlotResponse.BookingSlot.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "Business/Slot ID를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
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
    @Operation(summary = "과거 슬롯 일괄 삭제", description = "현재 날짜 이전의 모든 슬롯을 일괄 삭제합니다. (Business Manager/Owner 권한 필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제된 슬롯 수 반환",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "Business ID를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
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