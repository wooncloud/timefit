package timefit.operatinghours.service.util;

import org.springframework.stereotype.Component;
import timefit.business.entity.BusinessHours;
import timefit.business.entity.OperatingHours;
import timefit.common.entity.DayOfWeek;
import timefit.operatinghours.dto.OperatingHoursResponseDto;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OperatingHours Response DTO 생성 유틸리티
 * 역할:
 * - Entity → Response DTO 변환
 * - 요일별 그룹화
 * - 시간대 정렬
 * - DaySchedule 조합
 */
@Component
public class OperatingHoursResponseGenerator {

    /**
     * BusinessHours + OperatingHours → OperatingHours Response DTO 변환
     *
     * @param businessId 업체 ID
     * @param businessName 업체명
     * @param businessHours BusinessHours 엔티티 리스트
     * @param operatingHours OperatingHours 엔티티 리스트
     * @return OperatingHours Response DTO
     */
    public OperatingHoursResponseDto.OperatingHours generateResponse(
            UUID businessId,
            String businessName,
            List<BusinessHours> businessHours,
            List<OperatingHours> operatingHours) {

        // 1. BusinessHours를 Map으로 변환 (요일별)
        Map<DayOfWeek, BusinessHours> businessHoursMap = businessHours.stream()
                .collect(Collectors.toMap(
                        BusinessHours::getDayOfWeek,
                        h -> h
                ));

        // 2. OperatingHours를 요일별로 그룹화
        Map<DayOfWeek, List<OperatingHours>> operatingHoursMap = operatingHours.stream()
                .collect(Collectors.groupingBy(OperatingHours::getDayOfWeek));

        // 3. 요일별로 DaySchedule 생성
        List<OperatingHoursResponseDto.DaySchedule> schedules = new ArrayList<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            BusinessHours bizHours = businessHoursMap.get(day);
            List<OperatingHours> opHours = operatingHoursMap.getOrDefault(day, List.of());

            schedules.add(createDaySchedule(day.getValue(), bizHours, opHours));
        }

        // 4. 요일 순으로 정렬
        schedules.sort(Comparator.comparing(OperatingHoursResponseDto.DaySchedule::dayOfWeek));

        return new OperatingHoursResponseDto.OperatingHours(
                businessId,
                businessName,
                schedules
        );
    }

    /**
     * 요일별 스케줄 생성
     *
     * @param dayOfWeek 요일 값 (0-6)
     * @param businessHours BusinessHours 엔티티
     * @param operatingHours OperatingHours 엔티티 리스트
     * @return DaySchedule DTO
     */
    private OperatingHoursResponseDto.DaySchedule createDaySchedule(
            Integer dayOfWeek,
            BusinessHours businessHours,
            List<OperatingHours> operatingHours) {

        // BusinessHours 정보
        String openTime = (businessHours != null && !businessHours.getIsClosed())
                ? businessHours.getOpenTime().toString()
                : null;

        String closeTime = (businessHours != null && !businessHours.getIsClosed())
                ? businessHours.getCloseTime().toString()
                : null;

        Boolean isClosed = (businessHours == null || businessHours.getIsClosed());

        // OperatingHours → TimeRange 변환 (활성 시간대만, sequence 순 정렬)
        List<OperatingHoursResponseDto.TimeRange> bookingTimeRanges = operatingHours.stream()
                .filter(h -> !h.getIsClosed())
                .sorted(Comparator.comparing(OperatingHours::getSequence))
                .map(this::convertToTimeRange)
                .collect(Collectors.toList());

        return new OperatingHoursResponseDto.DaySchedule(
                dayOfWeek,
                openTime,
                closeTime,
                isClosed,
                bookingTimeRanges
        );
    }

    // OperatingHours Entity → TimeRange DTO 변환
    private OperatingHoursResponseDto.TimeRange convertToTimeRange(OperatingHours hours) {
        return new OperatingHoursResponseDto.TimeRange(
                hours.getOpenTime().toString(),
                hours.getCloseTime().toString()
        );
    }
}