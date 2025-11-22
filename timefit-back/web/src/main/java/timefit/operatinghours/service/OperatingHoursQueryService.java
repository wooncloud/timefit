package timefit.operatinghours.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessHours;
import timefit.business.entity.OperatingHours;
import timefit.business.repository.BusinessHoursRepository;
import timefit.business.repository.OperatingHoursRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.operatinghours.dto.OperatingHoursResponseDto;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperatingHoursQueryService {

    private final BusinessHoursRepository businessHoursRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final BusinessValidator businessValidator;

    // 영업시간 조회 (BusinessHours + OperatingHours 통합)
    public OperatingHoursResponseDto.OperatingHoursResult getOperatingHours(UUID businessId) {

        log.info("영업시간 조회 시작: businessId={}", businessId);

        // 1. Business 조회
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. BusinessHours 조회
        List<BusinessHours> businessHours =
                businessHoursRepository.findByBusinessIdOrderByDayOfWeekAsc(businessId);

        // 3. OperatingHours 조회
        List<OperatingHours> operatingHours =
                operatingHoursRepository.findByBusinessIdOrderByDayOfWeekAsc(businessId);

        log.info("영업시간 조회 완료: businessId={}, businessHours={}, operatingHours={}",
                businessId, businessHours.size(), operatingHours.size());

        // 4. DTO 변환
        return OperatingHoursResponseDto.OperatingHoursResult.of(
                businessId,
                business.getBusinessName(),
                businessHours,
                operatingHours
        );
    }
}