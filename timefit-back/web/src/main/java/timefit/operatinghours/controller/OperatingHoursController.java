package timefit.operatinghours.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.common.swagger.operation.operatinghours.*;
import timefit.common.swagger.requestbody.operatinghours.*;
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

    @GetOperatingHoursOperation
    @GetMapping
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> getOperatingHours(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId) {

        log.info("영업시간 조회 요청: businessId={}", businessId);

        OperatingHoursResponseDto.OperatingHours response =
                operatingHoursService.getOperatingHours(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @SetOperatingHoursOperation
    @PutMapping
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> setOperatingHours(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @SetOperatingHoursRequestBody
            @Valid @RequestBody OperatingHoursRequestDto.SetOperatingHours request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("영업시간 설정 요청: businessId={}, userId={}", businessId, currentUserId);

        OperatingHoursResponseDto.OperatingHours response =
                operatingHoursService.setOperatingHours(businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 특정 영업 요일 전체 시간대 휴무 토글
     * - 해당 요일의 모든 예약 시간대를 일괄 토글
     * - 기존 예약은 유지, 신규 예약만 차단
     * @example 월요일 전체 휴무 설정 시 오전(seq=0), 오후(seq=1) 모두 토글
     */
    @PatchMapping("/{dayOfWeek}/toggle")
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> toggleBusinessDayOpenStatus(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "요일 (0=월요일, 1=화요일, ..., 6=일요일)", required = true, example = "0")
            @PathVariable @Min(0) @Max(6) Integer dayOfWeek,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("요일 전체 휴무 토글 요청: businessId={}, dayOfWeek={}, userId={}",
                businessId, dayOfWeek, currentUserId);

        OperatingHoursResponseDto.OperatingHours response =
                operatingHoursService.toggleBusinessDayOpenStatus(businessId, dayOfWeek, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }



    @ResetOperatingHoursOperation
    @PatchMapping("/reset")
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> resetToDefault(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("영업시간 리셋 요청: businessId={}, userId={}", businessId, currentUserId);

        OperatingHoursResponseDto.OperatingHours response =
                operatingHoursService.resetToDefault(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}