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
import timefit.common.entity.DayOfWeek;
import timefit.operatinghours.dto.OperatingHoursRequest;
import timefit.operatinghours.dto.OperatingHoursResponse;
import timefit.operatinghours.service.util.BusinessHoursDefaultConfig;
import timefit.operatinghours.service.util.OperatingHoursConverter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OperatingHours Command Service
 * - BusinessHours(총 영업시간) + OperatingHours(예약 가능 시간대) 통합 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OperatingHoursCommandService {

    private final BusinessHoursRepository businessHoursRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final BusinessValidator businessValidator;

    /**
     * 영업시간 설정 (BusinessHours + OperatingHours 통합)
     * BusinessHours: 기존 레코드 UPDATE 방식 (월~일 고정 7개)
     * OperatingHours: DELETE + INSERT 방식 (개수 가변적)
     *
     * @param businessId 업체 ID
     * @param request 영업시간 설정 요청
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 설정 결과
     */
    public OperatingHoursResponse.OperatingHoursResult setOperatingHours(
            UUID businessId,
            OperatingHoursRequest.SetOperatingHours request,
            UUID currentUserId) {

        log.info("영업시간 설정 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. Business 조회
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 3. BusinessHours 업데이트
        List<BusinessHours> updatedBusinessHours = updateBusinessHours(business, request);

        // 4. OperatingHours 재생성
        List<OperatingHours> newOperatingHours = recreateOperatingHours(business, request);

        log.info("영업시간 저장 완료: businessId={}, businessHours={}, operatingHours={}",
                businessId, updatedBusinessHours.size(), newOperatingHours.size());

        // 5. DTO 변환
        return OperatingHoursResponse.OperatingHoursResult.of(
                businessId,
                business.getBusinessName(),
                updatedBusinessHours,
                newOperatingHours
        );
    }

    /**
     * 영업시간 리셋
     *
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 리셋 결과
     */
    public OperatingHoursResponse.OperatingHoursResult resetToDefault(
            UUID businessId,
            UUID currentUserId) {

        log.info("영업시간 리셋 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. Business 조회
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 3. BusinessHours 업데이트 (디폴트 값으로)
        List<BusinessHours> updatedBusinessHours = resetBusinessHoursToDefault(business);

        // 4. OperatingHours 삭제 (디폴트는 예약 시간대 없음)
        operatingHoursRepository.deleteByBusinessId(businessId);

        log.info("영업시간 리셋 완료: businessId={}, 디폴트 설정 적용", businessId);

        // 5. DTO 변환 (OperatingHours는 빈 리스트)
        return OperatingHoursResponse.OperatingHoursResult.of(
                businessId,
                business.getBusinessName(),
                updatedBusinessHours,
                List.of()
        );
    }

    // ------ Private 메서드 ---------

    /**
     * 영업 시간 BusinessHours 업데이트
     * 로직:
     * - 기존 레코드가 있으면 UPDATE
     * - 없으면 INSERT
     *
     * @param business 업체 엔티티
     * @param request 영업시간 설정 요청
     * @return 업데이트된 BusinessHours 리스트
     */
    private List<BusinessHours> updateBusinessHours(
            Business business,
            OperatingHoursRequest.SetOperatingHours request) {

        // 1. 기존 레코드 조회 및 Map 변환
        List<BusinessHours> existingHours = businessHoursRepository
                .findByBusinessIdOrderByDayOfWeekAsc(business.getId());

        Map<DayOfWeek, BusinessHours> hoursMap = existingHours.stream()
                .collect(Collectors.toMap(BusinessHours::getDayOfWeek, h -> h));

        // 2. 요청 데이터로 업데이트 또는 생성
        List<BusinessHours> result = new ArrayList<>();

        for (OperatingHoursRequest.DaySchedule schedule : request.getSchedules()) {
            DayOfWeek dayOfWeek = DayOfWeek.fromValue(schedule.getDayOfWeek());
            BusinessHours existing = hoursMap.get(dayOfWeek);

            if (existing != null) {
                // 기존 레코드 UPDATE
                if (Boolean.TRUE.equals(schedule.getIsClosed())) {
                    existing.setClosed();
                } else {
                    LocalTime openTime = LocalTime.parse(schedule.getOpenTime());
                    LocalTime closeTime = LocalTime.parse(schedule.getCloseTime());
                    existing.updateHours(openTime, closeTime);
                }
                result.add(existing);
            } else {
                // INSERT (초기 생성 시에만 발생)
                BusinessHours newHours = OperatingHoursConverter.convertToBusinessHours(
                        business, schedule, dayOfWeek);
                result.add(businessHoursRepository.save(newHours));
            }
        }

        return result;
    }

    /**
     * OperatingHours 재생성 (개수가 가변적이므로 DELETE + INSERT 방식)
     *
     * @param business 업체 엔티티
     * @param request 영업시간 설정 요청
     * @return 새로 생성된 OperatingHours 리스트
     */
    private List<OperatingHours> recreateOperatingHours(
            Business business,
            OperatingHoursRequest.SetOperatingHours request) {

        // 1. 기존 데이터 삭제
        operatingHoursRepository.deleteByBusinessId(business.getId());
        log.info("기존 OperatingHours 삭제 완료: businessId={}", business.getId());

        // 2. 새 데이터 생성
        List<OperatingHours> newOperatingHours = new ArrayList<>();

        for (OperatingHoursRequest.DaySchedule schedule : request.getSchedules()) {
            DayOfWeek dayOfWeek = DayOfWeek.fromValue(schedule.getDayOfWeek());

            // 예약 가능 시간대가 있는 경우에만 생성
            if (schedule.getBookingTimeRanges() != null &&
                    !schedule.getBookingTimeRanges().isEmpty()) {

                List<OperatingHours> dayOperatingHours =
                        OperatingHoursConverter.convertToOperatingHours(
                                business,
                                schedule.getBookingTimeRanges(),
                                dayOfWeek
                        );
                newOperatingHours.addAll(dayOperatingHours);
            }
        }

        // 3. 저장
        return operatingHoursRepository.saveAll(newOperatingHours);
    }

    /**
     * BusinessHours를 디폴트 값으로 리셋
     *
     * @param business 업체 엔티티
     * @return 리셋된 BusinessHours 리스트
     */
    private List<BusinessHours> resetBusinessHoursToDefault(Business business) {
        // 1. 기존 레코드 조회
        List<BusinessHours> existingHours = businessHoursRepository
                .findByBusinessIdOrderByDayOfWeekAsc(business.getId());

        if (existingHours.isEmpty()) {
            // 기존 레코드가 없으면 새로 생성
            List<BusinessHours> defaultHours =
                    BusinessHoursDefaultConfig.createDefaultBusinessHours(business);
            return businessHoursRepository.saveAll(defaultHours);
        }

        // 2. 기존 레코드를 디폴트 값으로 업데이트
        Map<DayOfWeek, BusinessHours> hoursMap = existingHours.stream()
                .collect(Collectors.toMap(BusinessHours::getDayOfWeek, h -> h));

        LocalTime defaultOpen = BusinessHoursDefaultConfig.getDefaultOpenTime();
        LocalTime defaultClose = BusinessHoursDefaultConfig.getDefaultCloseTime();

        for (DayOfWeek day : DayOfWeek.values()) {
            BusinessHours hours = hoursMap.get(day);

            if (hours != null) {
                // 월~금: 09:00-18:00
                if (day.getValue() >= 1 && day.getValue() <= 5) {
                    hours.updateHours(defaultOpen, defaultClose);
                } else {
                    // 토~일: 휴무
                    hours.setClosed();
                }
            }
        }

        return existingHours;
    }
}