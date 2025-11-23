package timefit.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

@Tag(name = "06. 예약 슬롯", description = "예약 슬롯 생성, 조회, 관리 API (업체용)")
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
    @Operation(
            summary = "예약 슬롯 일괄 생성 (업체용)",
            description = """
                    메뉴, 간격, 날짜/시간 스케줄을 지정하여 예약 슬롯을 일괄 생성합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. Request Body 필수값
                       - menuId: 슬롯을 생성할 메뉴 ID
                       - slotIntervalMinutes: 슬롯 간격 (분)
                       - schedules: 일별 슬롯 생성 스케줄 목록
                    
                    3. 슬롯 생성 규칙
                       - RESERVATION_BASED 메뉴만 슬롯 생성 가능
                       - slotIntervalMinutes는 메뉴 소요 시간 이상이어야 함
                       - 영업시간 내에서만 슬롯 생성
                       - 중복 슬롯은 자동으로 건너뜀
                    
                    4. schedules 구조
                       - date: 슬롯 생성 날짜 (오늘 또는 미래)
                       - timeRanges: 시간대 목록 (비어있으면 전체 영업시간 사용)
                         * startTime: 시작 시간 (HH:mm)
                         * endTime: 종료 시간 (HH:mm)
                    
                    5. 응답
                       - totalRequested: 총 요청된 슬롯 수
                       - created: 실제 생성된 슬롯 수
                       - skipped: 건너뛴 슬롯 수 (중복 또는 영업시간 외)
                    
                    6. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "슬롯 생성 요청 처리 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingSlotResponse.CreationResult.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "totalRequested": 100,
                                                "created": 95,
                                                "skipped": 5
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. menuId 은(는) 필수 값입니다.
                            2. slotIntervalMinutes 은(는) 필수 값입니다.
                            3. schedules 은(는) 필수 값입니다.
                            
                            SLOT_INVALID_MENU_TYPE - 슬롯 생성 불가능한 메뉴 타입 (ONDEMAND_BASED)
                            
                            INVALID_SLOT_INTERVAL - 슬롯 간격이 메뉴 소요 시간보다 짧음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                            
                            MENU_NOT_FOUND - 메뉴를 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.CreationResult>> createSlots(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "슬롯 생성 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingSlotRequest.BookingSlot.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "menuId": "550e8400-e29b-41d4-a716-446655440002",
                                              "slotIntervalMinutes": 30,
                                              "schedules": [
                                                {
                                                  "date": "2025-12-01",
                                                  "timeRanges": [
                                                    {
                                                      "startTime": "09:00",
                                                      "endTime": "12:00"
                                                    },
                                                    {
                                                      "startTime": "13:00",
                                                      "endTime": "18:00"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "date": "2025-12-02",
                                                  "timeRanges": []
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody BookingSlotRequest.BookingSlot request,
            @Parameter(hidden = true)
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
    @Operation(
            summary = "특정 날짜의 예약 슬롯 조회",
            description = """
                    해당 날짜에 생성된 모든 예약 슬롯을 조회합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. Query Parameter 필수값
                       - date: 조회할 날짜 (YYYY-MM-DD)
                    
                    3. 응답
                       - businessId: 업체 ID
                       - startDate: 조회 시작 날짜 (= date)
                       - endDate: 조회 종료 날짜 (= date)
                       - slots: 해당 날짜의 슬롯 목록
                    
                    4. 권한
                       - 인증 불필요 (공개 조회)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "슬롯 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingSlotResponse.BookingSlotList.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "BUSINESS_NOT_FOUND - 업체를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getSlotsByDate(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "조회할 날짜 (YYYY-MM-DD)",
                    required = true,
                    example = "2025-12-01"
            )
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
    @Operation(
            summary = "기간별 예약 슬롯 조회",
            description = """
                    시작 날짜부터 종료 날짜까지 생성된 모든 예약 슬롯을 조회합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. Query Parameter 필수값
                       - startDate: 조회 시작 날짜 (YYYY-MM-DD)
                       - endDate: 조회 종료 날짜 (YYYY-MM-DD)
                    
                    3. 제약사항
                       - startDate는 endDate보다 이전이어야 함
                    
                    4. 권한
                       - 인증 불필요 (공개 조회)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "슬롯 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingSlotResponse.BookingSlotList.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "INVALID_DATE_RANGE - 시작일이 종료일보다 늦음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "BUSINESS_NOT_FOUND - 업체를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @GetMapping("/range")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getSlotsByDateRange(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "조회 시작 날짜 (YYYY-MM-DD)",
                    required = true,
                    example = "2025-12-01"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(
                    description = "조회 종료 날짜 (YYYY-MM-DD)",
                    required = true,
                    example = "2025-12-31"
            )
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
    @Operation(
            summary = "메뉴별 기간 예약 슬롯 조회",
            description = """
                    특정 메뉴에 대해 기간 내 생성된 예약 슬롯을 조회합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - menuId: 메뉴 ID (UUID)
                    
                    2. Query Parameter 필수값
                       - startDate: 조회 시작 날짜 (YYYY-MM-DD)
                       - endDate: 조회 종료 날짜 (YYYY-MM-DD)
                    
                    3. 제약사항
                       - startDate는 endDate보다 이전이어야 함
                    
                    4. 권한
                       - 인증 불필요 (공개 조회)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "슬롯 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingSlotResponse.BookingSlotList.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "INVALID_DATE_RANGE - 시작일이 종료일보다 늦음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "BUSINESS_NOT_FOUND - 업체를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @GetMapping("/menu/{menuId}")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getSlotsByMenu(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "메뉴 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440002"
            )
            @PathVariable UUID menuId,
            @Parameter(
                    description = "조회 시작 날짜 (YYYY-MM-DD)",
                    required = true,
                    example = "2025-12-01"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(
                    description = "조회 종료 날짜 (YYYY-MM-DD)",
                    required = true,
                    example = "2025-12-31"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("메뉴별 슬롯 조회 요청: businessId={}, menuId={}, startDate={}, endDate={}",
                businessId, menuId, startDate, endDate);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getSlotsByMenu(
                businessId, menuId, startDate, endDate);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "향후 활성 슬롯 조회",
            description = """
                    오늘 날짜 이후 예약 가능한 상태의 모든 슬롯을 조회합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. 조회 조건
                       - slotDate >= 오늘
                       - isAvailable = true
                    
                    3. 권한
                       - 인증 불필요 (공개 조회)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "슬롯 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingSlotResponse.BookingSlotList.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "BUSINESS_NOT_FOUND - 업체를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @GetMapping("/upcoming")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlotList>> getUpcomingSlots(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId) {

        log.info("향후 활성 슬롯 조회 요청: businessId={}", businessId);

        BookingSlotResponse.BookingSlotList response = bookingSlotService.getUpcomingSlots(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "예약 슬롯 삭제 (업체용)",
            description = """
                    특정 슬롯을 영구적으로 삭제합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - slotId: 슬롯 ID (UUID)
                    
                    2. 제약사항
                       - 활성 예약이 없는 슬롯만 삭제 가능
                       - 예약이 있는 슬롯은 먼저 비활성화 권장
                    
                    3. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "슬롯 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "AVAILABLE_SLOT_NOT_MODIFIABLE - 활성 예약 존재로 삭제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            AVAILABLE_SLOT_NOT_FOUND - 슬롯을 찾을 수 없음
                            
                            BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @DeleteMapping("/{slotId}")
    public ResponseEntity<ResponseData<Void>> deleteSlot(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "슬롯 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440003"
            )
            @PathVariable UUID slotId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 삭제 요청: businessId={}, slotId={}", businessId, slotId);

        bookingSlotService.deleteSlot(businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(null));
    }

    @Operation(
            summary = "예약 슬롯 비활성화 (업체용)",
            description = """
                    슬롯을 예약 불가능 상태로 변경합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - slotId: 슬롯 ID (UUID)
                    
                    2. 처리 내용
                       - isAvailable = false로 변경
                       - 고객이 더 이상 예약할 수 없음
                       - 기존 예약은 유지됨
                    
                    3. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "슬롯 비활성화 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingSlotResponse.BookingSlot.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            AVAILABLE_SLOT_NOT_FOUND - 슬롯을 찾을 수 없음
                            
                            BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PatchMapping("/{slotId}/deactivate")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlot>> deactivateSlot(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "슬롯 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440003"
            )
            @PathVariable UUID slotId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 비활성화 요청: businessId={}, slotId={}", businessId, slotId);

        BookingSlotResponse.BookingSlot response = bookingSlotService.deactivateSlot(
                businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "예약 슬롯 활성화 (업체용)",
            description = """
                    비활성화된 슬롯을 다시 예약 가능 상태로 변경합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - slotId: 슬롯 ID (UUID)
                    
                    2. 처리 내용
                       - isAvailable = true로 변경
                       - 고객이 다시 예약할 수 있음
                    
                    3. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "슬롯 활성화 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingSlotResponse.BookingSlot.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            AVAILABLE_SLOT_NOT_FOUND - 슬롯을 찾을 수 없음
                            
                            BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PatchMapping("/{slotId}/activate")
    public ResponseEntity<ResponseData<BookingSlotResponse.BookingSlot>> activateSlot(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "슬롯 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440003"
            )
            @PathVariable UUID slotId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("슬롯 재활성화 요청: businessId={}, slotId={}", businessId, slotId);

        BookingSlotResponse.BookingSlot response = bookingSlotService.activateSlot(
                businessId, slotId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "과거 슬롯 일괄 삭제 (업체용)",
            description = """
                    현재 날짜 이전의 모든 슬롯을 일괄 삭제합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. 삭제 조건
                       - slotDate < 오늘
                       - 모든 상태의 슬롯 (활성/비활성)
                    
                    3. 응답
                       - 삭제된 슬롯 수 (Integer)
                    
                    4. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제된 슬롯 수 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "BUSINESS_NOT_FOUND - 업체를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @DeleteMapping("/past")
    public ResponseEntity<ResponseData<Integer>> deletePastSlots(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("과거 슬롯 일괄 삭제 요청: businessId={}", businessId);

        Integer deletedCount = bookingSlotService.deletePastSlots(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(deletedCount));
    }
}