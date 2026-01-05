package timefit.operatinghours.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessHours;
import timefit.business.repository.BusinessHoursRepository;
import timefit.common.entity.DayOfWeek;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.operatinghours.dto.OperatingHoursRequestDto;
import timefit.operatinghours.service.util.OperatingHoursConverter;
import timefit.operatinghours.service.validator.OperatingHoursValidator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BusinessHours 관련 비즈니스 로직 Helper
 * - BusinessHours 업데이트
 * - 디폴트 리셋
 * - 특정 요일 토글
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessHoursHelper {

    private final BusinessHoursRepository businessHoursRepository;
    private final OperatingHoursValidator validator;

    /**
     * BusinessHours 업데이트
     *
     * 로직:
     * - 기존 레코드가 있으면 UPDATE
     * - 없으면 INSERT
     *
     * @param business 업체 엔티티
     * @param request 영업시간 설정 요청
     * @return 업데이트된 BusinessHours 리스트
     */
    public List<BusinessHours> updateBusinessHours(
            Business business,
            OperatingHoursRequestDto.SetOperatingHours request) {

        log.debug("BusinessHours 업데이트 시작: businessId={}", business.getId());

        // 1. 기존 레코드 조회 및 Map 변환
        List<BusinessHours> existingHours = businessHoursRepository
                .findByBusinessIdOrderByDayOfWeekAsc(business.getId());

        Map<DayOfWeek, BusinessHours> hoursMap = existingHours.stream()
                .collect(Collectors.toMap(BusinessHours::getDayOfWeek, h -> h));

        // 2. 요청 데이터로 업데이트 또는 생성
        List<BusinessHours> result = new ArrayList<>();

        for (OperatingHoursRequestDto.DaySchedule schedule : request.schedules()) {
            DayOfWeek dayOfWeek = DayOfWeek.fromValue(schedule.dayOfWeek());
            BusinessHours existing = hoursMap.get(dayOfWeek);

            if (existing != null) {
                // 기존 레코드 UPDATE
                if (Boolean.TRUE.equals(schedule.isClosed())) {
                    existing.setClosed();
                } else {
                    LocalTime openTime = LocalTime.parse(schedule.openTime());
                    LocalTime closeTime = LocalTime.parse(schedule.closeTime());

                    // 시간 순서 검증
                    validator.validateTimeOrder(openTime, closeTime);

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

        log.debug("BusinessHours 업데이트 완료: count={}", result.size());
        return result;
    }

    /**
     * BusinessHours 디폴트 리셋
     *
     * 월~금: 09:00-18:00
     * 토, 일: 휴무
     *
     * @param business 업체 엔티티
     * @return 디폴트 설정된 BusinessHours 리스트
     */
    public List<BusinessHours> resetToDefault(Business business) {

        log.debug("BusinessHours 디폴트 리셋 시작: businessId={}", business.getId());

        // 1. 기존 레코드 조회
        List<BusinessHours> existingHours = businessHoursRepository
                .findByBusinessIdOrderByDayOfWeekAsc(business.getId());

        Map<DayOfWeek, BusinessHours> hoursMap = existingHours.stream()
                .collect(Collectors.toMap(BusinessHours::getDayOfWeek, h -> h));

        // 2. 디폴트 설정 적용
        List<BusinessHours> result = new ArrayList<>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            BusinessHours existing = hoursMap.get(dayOfWeek);
            boolean isWeekday = dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5;

            if (existing != null) {
                // 기존 레코드 UPDATE
                if (isWeekday) {
                    // 월~금: 영업일
                    existing.updateHours(
                            BusinessHoursDefaultConfig.getDefaultOpenTime(),
                            BusinessHoursDefaultConfig.getDefaultCloseTime()
                    );
                } else {
                    // 토~일: 휴무일
                    existing.setClosed();
                }
                result.add(existing);
            } else {
                // INSERT (초기 생성)
                BusinessHours newHours;
                if (isWeekday) {
                    // 월~금: 영업일
                    newHours = BusinessHours.createOpenDay(
                            business,
                            dayOfWeek,
                            BusinessHoursDefaultConfig.getDefaultOpenTime(),
                            BusinessHoursDefaultConfig.getDefaultCloseTime()
                    );
                } else {
                    // 토~일: 휴무일
                    newHours = BusinessHours.createClosedDay(business, dayOfWeek);
                }
                result.add(businessHoursRepository.save(newHours));
            }
        }

        log.debug("BusinessHours 디폴트 리셋 완료: count={}", result.size());
        return result;
    }

    /**
     * 특정 요일 휴무 토글
     * BusinessHours의 isClosed 상태만 변경
     * (영업시간 정보는 유지 - 재활성화를 위해)
     *
     * @param businessId 업체 ID
     * @param dayOfWeek 요일
     * @throws BusinessException 해당 요일의 BusinessHours가 없는 경우
     */
    public void toggleBusinessDay(UUID businessId, DayOfWeek dayOfWeek) {

        log.debug("BusinessHours 토글 시작: businessId={}, dayOfWeek={}",
                businessId, dayOfWeek);

        BusinessHours businessHours = businessHoursRepository
                .findByBusinessIdAndDayOfWeek(businessId, dayOfWeek)
                .orElseThrow(() -> new BusinessException(
                        BusinessErrorCode.OPERATING_HOURS_NOT_FOUND,
                        String.format("요일 %s의 영업시간 정보를 찾을 수 없습니다.", dayOfWeek)
                ));

        businessHours.toggle();

        log.debug("BusinessHours 토글 완료: isClosed={}", businessHours.getIsClosed());
    }
}