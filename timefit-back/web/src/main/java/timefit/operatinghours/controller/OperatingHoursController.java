package timefit.operatinghours.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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