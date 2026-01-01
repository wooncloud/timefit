package timefit.operatinghours.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.operatinghours.dto.OperatingHoursRequestDto;
import timefit.operatinghours.dto.OperatingHoursResponseDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperatingHoursService {

    private final OperatingHoursQueryService queryService;
    private final OperatingHoursCommandService commandService;

    // 영업시간 조회
    public OperatingHoursResponseDto.OperatingHours getOperatingHours(UUID businessId) {
        return queryService.getOperatingHours(businessId);
    }

    // 영업시간 설정
    @Transactional
    public OperatingHoursResponseDto.OperatingHours setOperatingHours(
            UUID businessId,
            OperatingHoursRequestDto.SetOperatingHours request,
            UUID currentUserId) {

        return commandService.setOperatingHours(businessId, request, currentUserId);
    }

    // 특정 요일 전체 시간대 휴무 토글
    @Transactional
    public OperatingHoursResponseDto.OperatingHours toggleBusinessDayOpenStatus(
            UUID businessId,
            Integer dayOfWeek,
            UUID currentUserId) {

        return commandService.toggleBusinessDayOpenStatus(businessId, dayOfWeek, currentUserId);
    }

    // 영업시간 리셋 (디폴트 값으로)
    @Transactional
    public OperatingHoursResponseDto.OperatingHours resetToDefault(
            UUID businessId,
            UUID currentUserId) {

        return commandService.resetToDefault(businessId, currentUserId);
    }
}