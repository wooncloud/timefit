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
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.operatinghours.dto.OperatingHoursRequestDto;
import timefit.operatinghours.dto.OperatingHoursResponseDto;
import timefit.operatinghours.service.util.BusinessHoursDefaultConfig;
import timefit.operatinghours.service.util.OperatingHoursConverter;
import timefit.operatinghours.service.util.OperatingHoursResponseGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 변경사항:
 * - OperatingHoursRequest → OperatingHoursRequestDto
 * - OperatingHoursResponse.OperatingHoursResult → OperatingHoursResponseDto.OperatingHours
 * - ResponseGenerator 사용
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OperatingHoursCommandService {

    private final BusinessHoursRepository businessHoursRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final BusinessValidator businessValidator;
    private final OperatingHoursResponseGenerator responseGenerator;

    private final OperatingHoursQueryService queryService;

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
    public OperatingHoursResponseDto.OperatingHours setOperatingHours(
            UUID businessId,
            OperatingHoursRequestDto.SetOperatingHours request,
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

        // 5. Response DTO 생성
        return responseGenerator.generateResponse(
                businessId,
                business.getBusinessName(),
                updatedBusinessHours,
                newOperatingHours
        );
    }

    /**
     * 특정 요일 전체 시간대 휴무 토글
     * - 해당 요일의 모든 OperatingHours를 일괄 토글
     * - 기존 예약은 유지, 신규 예약만 차단
     *
     * @param businessId 업체 ID
     * @param dayOfWeek 요일 (0=월요일 ~ 6=일요일)
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 전체 조회 결과
     */
    public OperatingHoursResponseDto.OperatingHours toggleBusinessDayOpenStatus(
            UUID businessId,
            Integer dayOfWeek,
            UUID currentUserId) {

        log.info("요일 전체 휴무 토글 시작: businessId={}, dayOfWeek={}, userId={}",
                businessId, dayOfWeek, currentUserId);

        //   -------------     검증 페이즈
        // 1. Business 조회
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 3. DayOfWeek 변환
        DayOfWeek day = DayOfWeek.fromValue(dayOfWeek);

        //    -------------    조회 페이즈
        // 4. 해당 요일의 BusinessHours 조회
        BusinessHours businessHours = businessHoursRepository
                .findByBusinessIdAndDayOfWeek(businessId, day)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.OPERATING_HOURS_NOT_FOUND));

        // 5. 해당 요일의 모든 OperatingHours 조회
        List<OperatingHours> operatingHoursList =
                operatingHoursRepository.findByBusinessIdAndDayOfWeekOrderBySequenceAsc(businessId, day);

        //     -------------   조회 페이즈
        // 6. BusinessHours 토글 (요일 전체 휴무 상태)
        businessHours.toggle();

        // 7. OperatingHours 토글 (해당 요일에 종속된 시간대별 휴무 상태)
        operatingHoursList.forEach(OperatingHours::toggle);

        log.info("요일 전체 휴무 토글 완료: businessId={}, dayOfWeek={}, BusinessHours 토글됨, OperatingHours 토글 수={}",
                businessId, dayOfWeek, operatingHoursList.size());

        // -------------  반환 페이즈
        // 8. Response 생성에 필요한 전체 데이터 조회
        List<BusinessHours> allBusinessHours =
                businessHoursRepository.findByBusinessIdOrderByDayOfWeekAsc(businessId);

        List<OperatingHours> allOperatingHours =
                operatingHoursRepository.findByBusinessIdOrderByDayOfWeekAsc(businessId);

        // 9. Response DTO 생성
        return responseGenerator.generateResponse(
                businessId,
                business.getBusinessName(),
                allBusinessHours,
                allOperatingHours
        );
    }


    /**
     * 영업시간 리셋
     *
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 리셋 결과
     */
    public OperatingHoursResponseDto.OperatingHours resetToDefault(
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

        // 5. Response DTO 생성 (OperatingHours는 빈 리스트)
        return responseGenerator.generateResponse(
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
            OperatingHoursRequestDto.SetOperatingHours request) {

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
            OperatingHoursRequestDto.SetOperatingHours request) {

        // 1. 기존 데이터 삭제
        operatingHoursRepository.deleteByBusinessId(business.getId());
        log.info("기존 OperatingHours 삭제 완료: businessId={}", business.getId());

        // 2. 새 데이터 생성
        List<OperatingHours> newOperatingHours = new ArrayList<>();

        for (OperatingHoursRequestDto.DaySchedule schedule : request.schedules()) {
            DayOfWeek dayOfWeek = DayOfWeek.fromValue(schedule.dayOfWeek());

            // 예약 가능 시간대가 있는 경우에만 생성
            if (schedule.bookingTimeRanges() != null &&
                    !schedule.bookingTimeRanges().isEmpty()) {

                List<OperatingHours> dayOperatingHours =
                        OperatingHoursConverter.convertToOperatingHours(
                                business,
                                schedule.bookingTimeRanges(),
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