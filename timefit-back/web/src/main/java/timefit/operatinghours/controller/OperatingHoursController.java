package timefit.operatinghours.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.operatinghours.dto.OperatingHoursRequestDto;
import timefit.operatinghours.dto.OperatingHoursResponseDto;
import timefit.operatinghours.service.OperatingHoursService;

import java.util.UUID;

@Tag(name = "03. 영업시간 관리", description = "업체의 영업시간 및 예약 가능 시간대 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}/operating-hours")
@RequiredArgsConstructor
public class OperatingHoursController {

    private final OperatingHoursService operatingHoursService;

    /**
     * 영업시간 조회
     * 권한: 없음 (공개 API)
     * @param businessId 업체 ID
     * @return 영업시간 조회 결과
     */
    @Operation(
            summary = "영업시간 조회",
            description = """
                    업체의 영업시간 및 예약 가능 시간대를 조회합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. 응답 구조
                       - businessId: 업체 ID
                       - businessName: 업체명
                       - schedules: 요일별 스케줄 목록 (7개 요일)
                    
                    3. 요일별 스케줄 정보
                       - dayOfWeek: 요일 (0=일요일, 1=월요일, ..., 6=토요일)
                       - openTime: 영업 시작 시간 (HH:mm)
                       - closeTime: 영업 종료 시간 (HH:mm)
                       - isClosed: 휴무일 여부
                       - bookingTimeRanges: 예약 가능 시간대 목록
                    
                    4. 권한
                       - 인증 불필요 (공개 조회)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OperatingHoursResponseDto.OperatingHours.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "businessId": "550e8400-e29b-41d4-a716-446655440001",
                                                "businessName": "홍길동 미용실",
                                                "schedules": [
                                                  {
                                                    "dayOfWeek": 0,
                                                    "openTime": null,
                                                    "closeTime": null,
                                                    "isClosed": true,
                                                    "bookingTimeRanges": []
                                                  },
                                                  {
                                                    "dayOfWeek": 1,
                                                    "openTime": "09:00",
                                                    "closeTime": "18:00",
                                                    "isClosed": false,
                                                    "bookingTimeRanges": [
                                                      {
                                                        "startTime": "09:00",
                                                        "endTime": "12:00"
                                                      },
                                                      {
                                                        "startTime": "13:00",
                                                        "endTime": "18:00"
                                                      }
                                                    ]
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
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
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> getOperatingHours(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId) {

        log.info("영업시간 조회 요청: businessId={}", businessId);

        OperatingHoursResponseDto.OperatingHours response =
                operatingHoursService.getOperatingHours(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 영업시간 설정
     * 권한: OWNER, MANAGER
     * @param businessId 업체 ID
     * @param request 영업시간 설정 요청
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 설정 결과
     */
    @Operation(
            summary = "영업시간 설정 (업체용)",
            description = """
                    업체의 영업시간 및 예약 가능 시간대를 설정합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. Request Body 필수값
                       - schedules: 요일별 스케줄 목록 (7개 요일 전체 필수)
                    
                    3. 요일별 스케줄 구조
                       - dayOfWeek: 요일 (0=일요일 ~ 6=토요일) - 필수
                       - openTime: 영업 시작 시간 (HH:mm) - 영업일인 경우 필수
                       - closeTime: 영업 종료 시간 (HH:mm) - 영업일인 경우 필수
                       - isClosed: 휴무일 여부 - 선택 (기본값: false)
                       - bookingTimeRanges: 예약 가능 시간대 목록 - 선택
                    
                    4. 예약 가능 시간대 구조
                       - startTime: 시작 시간 (HH:mm) - 필수
                       - endTime: 종료 시간 (HH:mm) - 필수
                       - 영업시간 내에 포함되어야 함
                    
                    5. 제약사항
                       - 7개 요일 모두 설정 필요
                       - openTime < closeTime
                       - 예약 가능 시간대는 영업시간 내에 포함
                       - 예약 가능 시간대 중복 불가
                    
                    6. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "설정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OperatingHoursResponseDto.OperatingHours.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. schedules 은(는) 필수 값입니다.
                            2. dayOfWeek 은(는) 필수 값입니다.
                            3. 요일은 0(일요일)부터 6(토요일)까지입니다.
                            4. startTime 은(는) 필수 값입니다.
                            5. endTime 은(는) 필수 값입니다.
                            
                            INVALID_TIME_FORMAT - 시간 형식 오류 (HH:mm 형식이 아님)
                            
                            INVALID_TIME_RANGE - 시간 범위 오류 (종료 시간이 시작 시간보다 빠름)
                            
                            BOOKING_TIME_OUT_OF_OPERATING_HOURS - 예약 가능 시간대가 영업시간을 벗어남
                            
                            MISSING_DAY_OF_WEEK - 7개 요일 전체가 아님
                            
                            DUPLICATE_DAY_OF_WEEK - 중복된 요일
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "BUSINESS_ACCESS_DENIED - 권한 부족 (OWNER, MANAGER만 가능)",
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
    @PutMapping
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> setOperatingHours(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "영업시간 설정 요청 (7개 요일 전체)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OperatingHoursRequestDto.SetOperatingHours.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "schedules": [
                                                {
                                                  "dayOfWeek": 0,
                                                  "openTime": null,
                                                  "closeTime": null,
                                                  "isClosed": true,
                                                  "bookingTimeRanges": []
                                                },
                                                {
                                                  "dayOfWeek": 1,
                                                  "openTime": "09:00",
                                                  "closeTime": "18:00",
                                                  "isClosed": false,
                                                  "bookingTimeRanges": [
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
                                                  "dayOfWeek": 2,
                                                  "openTime": "09:00",
                                                  "closeTime": "18:00",
                                                  "isClosed": false,
                                                  "bookingTimeRanges": [
                                                    {
                                                      "startTime": "09:00",
                                                      "endTime": "18:00"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "dayOfWeek": 3,
                                                  "openTime": "09:00",
                                                  "closeTime": "18:00",
                                                  "isClosed": false,
                                                  "bookingTimeRanges": [
                                                    {
                                                      "startTime": "09:00",
                                                      "endTime": "18:00"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "dayOfWeek": 4,
                                                  "openTime": "09:00",
                                                  "closeTime": "18:00",
                                                  "isClosed": false,
                                                  "bookingTimeRanges": [
                                                    {
                                                      "startTime": "09:00",
                                                      "endTime": "18:00"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "dayOfWeek": 5,
                                                  "openTime": "09:00",
                                                  "closeTime": "18:00",
                                                  "isClosed": false,
                                                  "bookingTimeRanges": [
                                                    {
                                                      "startTime": "09:00",
                                                      "endTime": "18:00"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "dayOfWeek": 6,
                                                  "openTime": null,
                                                  "closeTime": null,
                                                  "isClosed": true,
                                                  "bookingTimeRanges": []
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody OperatingHoursRequestDto.SetOperatingHours request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("영업시간 설정 요청: businessId={}, userId={}", businessId, currentUserId);

        OperatingHoursResponseDto.OperatingHours response =
                operatingHoursService.setOperatingHours(businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 영업시간 리셋 (디폴트 값으로)
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 리셋 결과
     */
    @Operation(
            summary = "영업시간 기본값으로 리셋 (업체용)",
            description = """
                    업체의 영업시간을 기본값으로 초기화합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. 기본값 설정
                       - 월~금: 09:00-18:00 (영업)
                       - 토~일: 휴무
                       - 예약 가능 시간대: 영업시간 전체
                    
                    3. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리셋 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OperatingHoursResponseDto.OperatingHours.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "BUSINESS_ACCESS_DENIED - 권한 부족 (OWNER, MANAGER만 가능)",
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
    @PatchMapping("/reset")
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> resetToDefault(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("영업시간 리셋 요청: businessId={}, userId={}", businessId, currentUserId);

        OperatingHoursResponseDto.OperatingHours response =
                operatingHoursService.resetToDefault(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}