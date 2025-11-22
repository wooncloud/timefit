package timefit.operatinghours.controller;

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
public class OperatingHoursController {

    private final OperatingHoursService operatingHoursService;

    /**
     * 영업시간 조회
     * 권한: 없음 (공개 API)
     * @param businessId 업체 ID
     * @return 영업시간 조회 결과
     */
    @GetMapping
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHoursResult>> getOperatingHours(
            @PathVariable UUID businessId) {

        log.info("영업시간 조회 요청: businessId={}", businessId);

        OperatingHoursResponseDto.OperatingHoursResult response =
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
    @PutMapping
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHoursResult>> setOperatingHours(
            @PathVariable UUID businessId,
            @Valid @RequestBody OperatingHoursRequestDto.SetOperatingHours request,
            @CurrentUserId UUID currentUserId) {

        log.info("영업시간 설정 요청: businessId={}, userId={}", businessId, currentUserId);

        OperatingHoursResponseDto.OperatingHoursResult response =
                operatingHoursService.setOperatingHours(businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 영업시간 리셋 (디폴트 값으로)
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 리셋 결과
     */
    @PatchMapping("/reset")
    public ResponseEntity<ResponseData<OperatingHoursResponseDto.OperatingHoursResult>> resetToDefault(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("영업시간 리셋 요청: businessId={}, userId={}", businessId, currentUserId);

        OperatingHoursResponseDto.OperatingHoursResult response =
                operatingHoursService.resetToDefault(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}