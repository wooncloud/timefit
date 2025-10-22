package timefit.operatinghours.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.operatinghours.dto.OperatingHoursRequest;
import timefit.operatinghours.dto.OperatingHoursResponse;
import timefit.operatinghours.service.OperatingHoursService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/businesses/{businessId}/operating-hours")
@RequiredArgsConstructor
public class OperatingHoursController {

    private final OperatingHoursService operatingHoursService;

    /**
     * 영업시간 조회
     * 권한: 없음 (공개 API)
     */
    @GetMapping
    public ResponseEntity<ResponseData<OperatingHoursResponse.OperatingHoursResult>> getOperatingHours(
            @PathVariable UUID businessId) {

        log.info("영업시간 조회 요청: businessId={}", businessId);

        OperatingHoursResponse.OperatingHoursResult response =
                operatingHoursService.getOperatingHours(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 영업시간 설정 (완전 교체)
     * 권한: OWNER, MANAGER
     */
    @PutMapping
    public ResponseEntity<ResponseData<OperatingHoursResponse.OperatingHoursResult>> setOperatingHours(
            @PathVariable UUID businessId,
            @Valid @RequestBody OperatingHoursRequest.SetOperatingHours request,
            @CurrentUserId UUID currentUserId) {

        log.info("영업시간 설정 요청: businessId={}, userId={}, hoursCount={}",
                businessId, currentUserId, request.getBusinessHours().size());

        OperatingHoursResponse.OperatingHoursResult response =
                operatingHoursService.setOperatingHours(businessId, request, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}