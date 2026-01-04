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
     * 영업시간(business_hour) 토글
     * - 지정한 요일의 영업 유무를 조작합니다.
     * - 특정 영업시간 (business_hours) 조작 시, 해당 요일의 예약 슬롯 시간(operating_hours) 도 같이 조작됩니다.
     * - 차후 '특정 예약 슬롯 시간' 의 상태를 바꿔야 되는 경우를 고려하여 현재 구조 유지하기로 결정.
     * - 기존 예약은 유지, 신규 예약만 차단 되게끔 유도.
     * @example 월요일(business_hours) 전체 휴무 설정 시 오전, 오후(operating_hours) 데이터들도 모두 토글
     */
    @PatchMapping("/{dayOfWeek}/toggle")
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHours>> toggleBusinessDayOpenStatus(
            @Parameter(description = "업체 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID businessId,
            @Parameter(description = "요일 (0=일요일, 1=월요일, ..., 6=토요일)", required = true, example = "0")
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