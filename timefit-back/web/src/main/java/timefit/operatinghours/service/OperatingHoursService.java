package timefit.operatinghours.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.operatinghours.dto.OperatingHoursRequest;
import timefit.operatinghours.dto.OperatingHoursResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperatingHoursService {

    private final OperatingHoursQueryService queryService;
    private final OperatingHoursCommandService commandService;

    // 영업시간 조회
    public OperatingHoursResponse.OperatingHoursResult getOperatingHours(UUID businessId) {
        return queryService.getOperatingHours(businessId);
    }

    // 영업시간 설정
    @Transactional
    public OperatingHoursResponse.OperatingHoursResult setOperatingHours(
            UUID businessId,
            OperatingHoursRequest.SetOperatingHours request,
            UUID currentUserId) {
        return commandService.setOperatingHours(businessId, request, currentUserId);
    }
}