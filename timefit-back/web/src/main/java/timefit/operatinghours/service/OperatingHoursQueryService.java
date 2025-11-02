package timefit.operatinghours.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.entity.OperatingHours;
import timefit.business.repository.OperatingHoursRepository;
import timefit.operatinghours.dto.OperatingHoursResponse;
import timefit.operatinghours.service.util.BusinessFinder;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperatingHoursQueryService {

    private final OperatingHoursRepository operatingHoursRepository;
    private final BusinessFinder businessFinder;

    // 영업시간 조회
    public OperatingHoursResponse.OperatingHoursResult getOperatingHours(UUID businessId) {

        log.info("영업시간 조회 시작: businessId={}", businessId);

        // 1. Business 조회
        Business business = businessFinder.getBusinessEntity(businessId);

        // 2. 영업시간 조회 (요일순 정렬)
        List<OperatingHours> hours =
                operatingHoursRepository.findByBusinessIdOrderByDayOfWeekAsc(businessId);

        log.info("영업시간 조회 완료: businessId={}, count={}", businessId, hours.size());

        // 3. DTO 변환
        return OperatingHoursResponse.OperatingHoursResult.of(
                businessId,
                business.getBusinessName(),
                hours
        );
    }
}