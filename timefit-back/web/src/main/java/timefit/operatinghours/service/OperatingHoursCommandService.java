package timefit.operatinghours.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.common.entity.DayOfWeek;
import timefit.business.entity.OperatingHours;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.OperatingHoursRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.common.entity.BusinessRole;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.operatinghours.dto.OperatingHoursRequest;
import timefit.operatinghours.dto.OperatingHoursResponse;
import timefit.operatinghours.service.util.BusinessFinder;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OperatingHours Command Service
 * - 영업시간 설정(생성/수정/삭제)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OperatingHoursCommandService {

    private final OperatingHoursRepository operatingHoursRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final BusinessFinder businessFinder;

    /**
     * 영업시간 설정 (완전 교체 방식)
     * - 기존 영업시간 전체 삭제
     * - 새 영업시간 생성
     */
    public OperatingHoursResponse.OperatingHoursResult setOperatingHours(
            UUID businessId,
            OperatingHoursRequest.SetOperatingHours request,
            UUID currentUserId) {

        log.info("영업시간 설정 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. Business 조회
        Business business = businessFinder.getBusinessEntity(businessId);

        // 2. 권한 검증
        validateManagerOrOwnerRole(currentUserId, businessId);

        // 3. 기존 영업시간 삭제
        operatingHoursRepository.deleteByBusinessId(businessId);
        log.info("기존 영업시간 삭제 완료: businessId={}", businessId);

        // 4. 새 영업시간 생성
        List<OperatingHours> newHours = request.getBusinessHours().stream()
                .map(schedule -> convertToEntity(business, schedule))
                .collect(Collectors.toList());

        // 5. 저장
        List<OperatingHours> savedHours = operatingHoursRepository.saveAll(newHours);
        log.info("새 영업시간 저장 완료: businessId={}, count={}", businessId, savedHours.size());

        // 6. DTO 변환
        return OperatingHoursResponse.OperatingHoursResult.of(
                businessId,
                business.getBusinessName(),
                savedHours
        );
    }

//    ------ Private 메서드 ---------

    // 권한 검증: OWNER 또는 MANAGER만 허용
    private void validateManagerOrOwnerRole(UUID userId, UUID businessId) {
        UserBusinessRole role = userBusinessRoleRepository
                .findByUserIdAndBusinessIdAndIsActive(userId, businessId, true)
                .orElseThrow(() -> new BusinessException(
                        BusinessErrorCode.BUSINESS_NOT_ACTIVE));

        if (role.getRole() != BusinessRole.OWNER &&
                role.getRole() != BusinessRole.MANAGER) {
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }
    }

    // DTO → Entity 변환
    private OperatingHours convertToEntity(
            Business business,
            OperatingHoursRequest.DaySchedule schedule) {

        DayOfWeek dayOfWeek = DayOfWeek.fromValue(schedule.getDayOfWeek());

        // 휴무일 처리
        if (Boolean.TRUE.equals(schedule.getIsClosed())) {
            return OperatingHours.createClosedDay(business, dayOfWeek);
        }

        // 영업일 처리
        LocalTime openTime = LocalTime.parse(schedule.getOpenTime());
        LocalTime closeTime = LocalTime.parse(schedule.getCloseTime());

        return OperatingHours.createSinglePeriod(
                business, dayOfWeek, openTime, closeTime, false
        );
    }
}