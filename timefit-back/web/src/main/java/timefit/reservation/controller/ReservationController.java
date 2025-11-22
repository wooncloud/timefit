package timefit.reservation.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.service.ReservationService;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "예약", description = "예약 관리 API (고객용/업체용)")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // ========================================
    // 고객용 API: /api/reservations
    // ========================================

    /**
     * 예약 생성
     * - bookingSlotId가 있으면 RESERVATION_BASED
     * - bookingSlotId가 없고 reservationDate/Time이 있으면 ONDEMAND_BASED
     */
    @Operation(
            summary = "예약 생성 (고객용)",
            description = """
                    새로운 예약을 생성합니다.
                    
                    1. Request Body 필수값
                       - businessId: 업체 ID
                       - menuId: 메뉴 ID
                       - durationMinutes: 서비스 시간
                       - totalPrice: 예약 금액
                       - customerName: 예약자 이름
                       - customerPhone: 연락처
                    
                    2. Request Body 선택값 (예약 유형에 따라)
                       - bookingSlotId: 슬롯 ID (RESERVATION_BASED일 때 필수)
                       - reservationDate: 예약 날짜 (ONDEMAND_BASED일 때 필수)
                       - reservationTime: 예약 시간 (ONDEMAND_BASED일 때 필수)
                       - notes: 메모
                    
                    3. 예약 유형
                       - RESERVATION_BASED: bookingSlotId 제공
                       - ONDEMAND_BASED: reservationDate, reservationTime 제공
                    
                    4. 제약사항
                       - durationMinutes: 10~480분
                       - customerPhone: 10~11자리 숫자
                       - notes: 최대 500자
                    
                    5. 권한
                       - 로그인 필요 (CUSTOMER)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.CustomerReservation.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "reservationId": "550e8400-e29b-41d4-a716-446655440000",
                                                "reservationNumber": "R20251123001",
                                                "status": "PENDING",
                                                "businessName": "강남 헤어샵",
                                                "menuServiceName": "헤어 컷",
                                                "reservationDate": "2025-12-01",
                                                "reservationTime": "14:00:00",
                                                "reservationPrice": 30000
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
                            1. businessId 은(는) 필수 값입니다.
                            2. menuId 은(는) 필수 값입니다.
                            3. durationMinutes 은(는) 필수 값입니다.
                            4. totalPrice 은(는) 필수 값입니다.
                            5. customerName 은(는) 필수 값입니다.
                            6. customerPhone 은(는) 필수 값입니다.
                            
                            SLOT_NOT_AVAILABLE - 슬롯 예약 불가
                            
                            MENU_NOT_ACTIVE - 비활성화된 메뉴
                            """,
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
                            
                            BOOKING_SLOT_NOT_FOUND - 슬롯을 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/api/reservation")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservation>> createReservation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예약 생성 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationRequestDto.CreateReservation.class),
                            examples = {
                                    @ExampleObject(
                                            name = "예약형 서비스 (슬롯 기반)",
                                            value = """
                                                    {
                                                      "businessId": "550e8400-e29b-41d4-a716-446655440001",
                                                      "menuId": "550e8400-e29b-41d4-a716-446655440002",
                                                      "bookingSlotId": "550e8400-e29b-41d4-a716-446655440003",
                                                      "durationMinutes": 60,
                                                      "totalPrice": 30000,
                                                      "customerName": "홍길동",
                                                      "customerPhone": "01012345678",
                                                      "notes": "처음 방문입니다"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "현장 주문형 서비스 (즉시 예약)",
                                            value = """
                                                    {
                                                      "businessId": "550e8400-e29b-41d4-a716-446655440001",
                                                      "menuId": "550e8400-e29b-41d4-a716-446655440002",
                                                      "reservationDate": "2025-12-01",
                                                      "reservationTime": "12:00:00",
                                                      "durationMinutes": 30,
                                                      "totalPrice": 8000,
                                                      "customerName": "홍길동",
                                                      "customerPhone": "01012345678"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody ReservationRequestDto.CreateReservation request,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("예약 생성 요청: customerId={}, businessId={}, menuId={}",
                customerId, request.businessId(), request.menuId());

        ReservationResponseDto.CustomerReservation response;

        // 예약 타입 판별 로직 (DTO에서 제거됨)
        if (request.bookingSlotId() != null) {
            // RESERVATION_BASED
            response = reservationService.createReservationBased(request, customerId);
        } else if (request.reservationDate() != null && request.reservationTime() != null) {
            // ONDEMAND_BASED
            response = reservationService.createOnDemandBased(request, customerId);
        } else {
            throw new IllegalArgumentException("유효하지 않은 예약 타입입니다");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    @Operation(
            summary = "내 예약 목록 조회 (고객용)",
            description = """
                    로그인한 고객의 예약 목록을 조회합니다.
                    
                    1. Query Parameter (모두 선택)
                       - status: 예약 상태 필터
                       - startDate: 시작 날짜 (YYYY-MM-DD)
                       - endDate: 종료 날짜 (YYYY-MM-DD)
                       - businessId: 업체 ID 필터
                       - page: 페이지 번호 (0부터 시작, 기본값: 0)
                       - size: 페이지 크기 (기본값: 20)
                    
                    2. 예약 상태
                       - PENDING: 대기중
                       - CONFIRMED: 확정됨
                       - REJECTED: 거절됨
                       - CANCELLED: 취소됨
                       - COMPLETED: 완료됨
                       - NO_SHOW: 노쇼
                    
                    3. 권한
                       - 로그인 필요 (CUSTOMER)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.CustomerReservationList.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "INVALID_PARAMETER - 잘못된 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @GetMapping("/api/reservations")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservationList>> getMyReservations(
            @Parameter(
                    description = "예약 상태 (PENDING/CONFIRMED/REJECTED/CANCELLED/COMPLETED/NO_SHOW)",
                    example = "CONFIRMED",
                    schema = @Schema(
                            allowableValues = {
                                    "PENDING (대기중)",
                                    "CONFIRMED (확정됨)",
                                    "REJECTED (거절됨)",
                                    "CANCELLED (취소됨)",
                                    "COMPLETED (완료됨)",
                                    "NO_SHOW (노쇼)"
                            }
                    )
            )
            @RequestParam(required = false) String status,
            @Parameter(
                    description = "시작 날짜 (YYYY-MM-DD)",
                    example = "2025-11-01"
            )
            @RequestParam(required = false) String startDate,
            @Parameter(
                    description = "종료 날짜 (YYYY-MM-DD)",
                    example = "2025-11-30"
            )
            @RequestParam(required = false) String endDate,
            @Parameter(
                    description = "업체 ID 필터",
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @RequestParam(required = false) UUID businessId,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "페이지 크기",
                    example = "20"
            )
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("내 예약 목록 조회 요청: customerId={}, status={}, page={}",
                customerId, status, page);

        ReservationResponseDto.CustomerReservationList response = reservationService.getMyReservations(
                customerId, status, startDate, endDate, businessId, page, size);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "예약 상세 조회 (고객용)",
            description = """
                    예약 상세 정보를 조회합니다.
                    
                    1. Path Parameter
                       - reservationId: 예약 ID (UUID)
                    
                    2. 권한
                       - 로그인 필요 (CUSTOMER)
                       - 본인의 예약만 조회 가능
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.CustomerReservation.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "RESERVATION_NOT_FOUND - 예약을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "RESERVATION_ACCESS_DENIED - 본인의 예약이 아님",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @GetMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservation>> getReservationDetail(
            @Parameter(
                    description = "예약 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reservationId,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("예약 상세 조회 요청: reservationId={}, customerId={}",
                reservationId, customerId);

        ReservationResponseDto.CustomerReservation response = reservationService.getReservationDetail(
                reservationId, customerId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "예약 수정 (고객용)",
            description = """
                    예약 정보를 수정합니다.
                    
                    1. Path Parameter
                       - reservationId: 예약 ID (UUID)
                    
                    2. Request Body (변경할 필드만 입력)
                       - reservationDate: 예약 날짜
                       - reservationTime: 예약 시간
                       - customerName: 예약자 이름
                       - customerPhone: 연락처
                       - notes: 메모
                       - reason: 수정 사유 (필수)
                    
                    3. 제약사항
                       - PENDING 상태에서만 수정 가능
                       - reason: 최대 200자
                    
                    4. 권한
                       - 로그인 필요 (CUSTOMER)
                       - 본인의 예약만 수정 가능
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.CustomerReservation.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. reason 은(는) 필수 값입니다.
                            
                            CANNOT_UPDATE_RESERVATION - 수정할 수 없는 상태
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "RESERVATION_NOT_FOUND - 예약을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "RESERVATION_ACCESS_DENIED - 본인의 예약이 아님",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PutMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerReservation>> updateReservation(
            @Parameter(
                    description = "예약 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reservationId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예약 수정 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationRequestDto.UpdateReservation.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "reservationDate": "2025-12-02",
                                              "reservationTime": "15:00:00",
                                              "customerPhone": "01087654321",
                                              "notes": "시간 변경 요청합니다",
                                              "reason": "개인 사정으로 시간 변경"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody ReservationRequestDto.UpdateReservation request,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("예약 수정 요청: reservationId={}, customerId={}",
                reservationId, customerId);

        ReservationResponseDto.CustomerReservation response = reservationService.updateReservation(
                reservationId, customerId, request);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "예약 취소 (고객용)",
            description = """
                    예약을 취소합니다.
                    
                    1. Path Parameter
                       - reservationId: 예약 ID (UUID)
                    
                    2. Request Body 필수값
                       - reason: 취소 사유 (최대 200자)
                    
                    3. 제약사항
                       - PENDING, CONFIRMED 상태에서만 취소 가능
                    
                    4. 권한
                       - 로그인 필요 (CUSTOMER)
                       - 본인의 예약만 취소 가능
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "취소 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.ReservationActionResult.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. reason 은(는) 필수 값입니다.
                            
                            CANNOT_CANCEL_RESERVATION - 취소할 수 없는 상태
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "RESERVATION_NOT_FOUND - 예약을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "RESERVATION_ACCESS_DENIED - 본인의 예약이 아님",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @DeleteMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> cancelReservation(
            @Parameter(
                    description = "예약 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reservationId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "예약 취소 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationRequestDto.CancelReservation.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "reason": "개인 사정으로 취소합니다"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody ReservationRequestDto.CancelReservation request,
            @Parameter(hidden = true)
            @CurrentUserId UUID customerId) {

        log.info("예약 취소 요청: reservationId={}, customerId={}",
                reservationId, customerId);

        ReservationResponseDto.ReservationActionResult response = reservationService.cancelReservation(
                reservationId, customerId, request);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // ========================================
    // 업체용 API: /api/business/{businessId}/reservations
    // ========================================

    @Operation(
            summary = "업체 예약 목록 조회 (업체용)",
            description = """
                    업체의 모든 예약을 조회합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. Query Parameter (모두 선택)
                       - status: 예약 상태 필터
                       - startDate: 시작 날짜 (YYYY-MM-DD)
                       - endDate: 종료 날짜 (YYYY-MM-DD)
                       - page: 페이지 번호 (0부터 시작, 기본값: 0)
                       - size: 페이지 크기 (기본값: 20)
                    
                    3. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.BusinessReservationList.class)
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
    @GetMapping("/api/business/{businessId}/reservations")
    public ResponseEntity<ResponseData<ReservationResponseDto.BusinessReservationList>> getBusinessReservations(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "예약 상태 (PENDING/CONFIRMED/REJECTED/CANCELLED/COMPLETED/NO_SHOW)",
                    example = "PENDING",
                    schema = @Schema(
                            allowableValues = {
                                    "PENDING (대기중)",
                                    "CONFIRMED (확정됨)",
                                    "REJECTED (거절됨)",
                                    "CANCELLED (취소됨)",
                                    "COMPLETED (완료됨)",
                                    "NO_SHOW (노쇼)"
                            }
                    )
            )
            @RequestParam(required = false) String status,
            @Parameter(
                    description = "시작 날짜",
                    example = "2025-11-01"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(
                    description = "종료 날짜",
                    example = "2025-11-30"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "페이지 크기",
                    example = "20"
            )
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 예약 목록 조회 요청: businessId={}, userId={}, status={}",
                businessId, currentUserId, status);

        ReservationResponseDto.BusinessReservationList response = reservationService.getBusinessReservations(
                businessId, currentUserId, status, startDate, endDate, page, size);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "업체 예약 상세 조회 (업체용)",
            description = """
                    업체의 특정 예약을 상세 조회합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - reservationId: 예약 ID (UUID)
                    
                    2. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.BusinessReservation.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            RESERVATION_NOT_FOUND - 예약을 찾을 수 없음
                            
                            BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음
                            
                            RESERVATION_ACCESS_DENIED - 해당 업체의 예약이 아님
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @GetMapping("/api/business/{businessId}/reservation/{reservationId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.BusinessReservation>> getBusinessReservationDetail(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "예약 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reservationId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 예약 상세 조회 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.BusinessReservation response = reservationService
                .getBusinessReservationDetail(businessId, reservationId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "예약 승인 (업체용)",
            description = """
                    대기중인 예약을 승인합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - reservationId: 예약 ID (UUID)
                    
                    2. 제약사항
                       - PENDING 상태에서만 승인 가능
                    
                    3. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "승인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.ReservationActionResult.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "CANNOT_APPROVE_RESERVATION - 승인할 수 없는 상태",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "RESERVATION_NOT_FOUND - 예약을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음
                            
                            RESERVATION_ACCESS_DENIED - 해당 업체의 예약이 아님
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/approve")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> approveReservation(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "예약 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reservationId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("예약 승인 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.approveReservation(
                businessId, reservationId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "예약 거절 (업체용)",
            description = """
                    대기중인 예약을 거절합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - reservationId: 예약 ID (UUID)
                    
                    2. Request Body
                       - reason: 거절 사유
                    
                    3. 제약사항
                       - PENDING 상태에서만 거절 가능
                    
                    4. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "거절 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.ReservationActionResult.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "CANNOT_REJECT_RESERVATION - 거절할 수 없는 상태",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "RESERVATION_NOT_FOUND - 예약을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음
                            
                            RESERVATION_ACCESS_DENIED - 해당 업체의 예약이 아님
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/reject")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> rejectReservation(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "예약 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reservationId,
            @Parameter(
                    description = "거절 사유",
                    example = "해당 시간에 예약이 불가능합니다"
            )
            @RequestBody(required = false) String reason,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("예약 거절 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.rejectReservation(
                businessId, reservationId, currentUserId, reason);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "예약 완료 처리 (업체용)",
            description = """
                    승인된 예약을 완료 처리합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - reservationId: 예약 ID (UUID)
                    
                    2. Request Body
                       - notes: 완료 메모
                    
                    3. 제약사항
                       - CONFIRMED 상태에서만 완료 처리 가능
                    
                    4. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "완료 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.ReservationActionResult.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "CANNOT_COMPLETE_RESERVATION - 완료 처리할 수 없는 상태",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "RESERVATION_NOT_FOUND - 예약을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음
                            
                            RESERVATION_ACCESS_DENIED - 해당 업체의 예약이 아님
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/complete")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> completeReservation(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "예약 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reservationId,
            @Parameter(
                    description = "완료 메모",
                    example = "서비스가 정상적으로 완료되었습니다"
            )
            @RequestBody(required = false) String notes,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("예약 완료 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.completeReservation(
                businessId, reservationId, currentUserId, notes);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @Operation(
            summary = "노쇼 처리 (업체용)",
            description = """
                    예약을 노쇼로 처리합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - reservationId: 예약 ID (UUID)
                    
                    2. Request Body
                       - notes: 노쇼 메모
                    
                    3. 제약사항
                       - CONFIRMED 상태에서만 노쇼 처리 가능
                    
                    4. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "노쇼 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.ReservationActionResult.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "CANNOT_MARK_AS_NO_SHOW - 노쇼 처리할 수 없는 상태",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "RESERVATION_NOT_FOUND - 예약을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음
                            
                            RESERVATION_ACCESS_DENIED - 해당 업체의 예약이 아님
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/api/business/{businessId}/reservation/{reservationId}/no-show")
    public ResponseEntity<ResponseData<ReservationResponseDto.ReservationActionResult>> markAsNoShow(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "예약 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reservationId,
            @Parameter(
                    description = "노쇼 메모",
                    example = "예약 시간에 나타나지 않음"
            )
            @RequestBody(required = false) String notes,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("노쇼 처리 요청: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        ReservationResponseDto.ReservationActionResult response = reservationService.markAsNoShow(
                businessId, reservationId, currentUserId, notes);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}