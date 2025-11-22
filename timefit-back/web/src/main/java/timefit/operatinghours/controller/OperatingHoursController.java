package timefit.operatinghours.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}/operating-hours")
@RequiredArgsConstructor
@Tag(name = "영업시간 관리", description = "업체의 영업시간 및 예약 가능 시간대 관리 API")
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
            description = "업체의 영업시간 및 예약 가능 시간대를 조회합니다. 인증 없이 누구나 조회 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = OperatingHoursResponseDto.OperatingHours.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음 (BUSINESS_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))
            )
    })
    @GetMapping
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> getOperatingHours(
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
            summary = "영업시간 설정",
            description = "업체의 영업시간 및 예약 가능 시간대를 설정합니다. " +
                    "7개 요일 전체에 대한 설정이 필요합니다. " +
                    "권한: OWNER, MANAGER"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "설정 성공",
                    content = @Content(schema = @Schema(implementation = OperatingHoursResponseDto.OperatingHours.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터 (시간 형식 오류, 요일 범위 오류 등)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 부족 (OWNER, MANAGER만 가능)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음 (BUSINESS_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))
            )
    })
    @PutMapping
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> setOperatingHours(
            @PathVariable UUID businessId,
            @Valid @RequestBody OperatingHoursRequestDto.SetOperatingHours request,
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
            summary = "영업시간 디폴트로 리셋",
            description = "업체의 영업시간을 기본값으로 초기화합니다. " +
                    "기본값: 월~금 09:00-18:00, 토~일 휴무. " +
                    "권한: OWNER, MANAGER"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리셋 성공",
                    content = @Content(schema = @Schema(implementation = OperatingHoursResponseDto.OperatingHours.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 부족 (OWNER, MANAGER만 가능)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음 (BUSINESS_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))
            )
    })
    @PatchMapping("/reset")
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> resetToDefault(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("영업시간 리셋 요청: businessId={}, userId={}", businessId, currentUserId);

        OperatingHoursResponseDto.OperatingHours response =
                operatingHoursService.resetToDefault(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}