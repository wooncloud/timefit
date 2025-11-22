package timefit.operatinghours.dto;

import timefit.business.entity.BusinessHours;
import timefit.business.entity.OperatingHours;
import timefit.common.entity.DayOfWeek;

import java.util.*;
import java.util.stream.Collectors;

public class OperatingHoursResponseDto {

    // 영업시간 + 예약 가능 시간대 통합 조회 결과
    public record OperatingHoursResult(
            UUID businessId,
            String businessName,
            List<DayScheduleResult> schedules
    ) {
        // BusinessHours + OperatingHours 조합하여 DTO 생성
        public static OperatingHoursResult of(
                UUID businessId,
                String businessName,
                List<BusinessHours> businessHours,
                List<OperatingHours> operatingHours) {

            // 1. BusinessHours를 Map 으로 변환 (요일별)
            Map<DayOfWeek, BusinessHours> businessHoursMap = businessHours.stream()
                    .collect(Collectors.toMap(
                            BusinessHours::getDayOfWeek,
                            h -> h
                    ));

            // 2. OperatingHours를 요일별로 그룹화
            Map<DayOfWeek, List<OperatingHours>> operatingHoursMap = operatingHours.stream()
                    .collect(Collectors.groupingBy(OperatingHours::getDayOfWeek));

            // 3. 요일별로 DayScheduleResult 생성
            List<DayScheduleResult> schedules = new ArrayList<>();

            for (DayOfWeek day : DayOfWeek.values()) {
                BusinessHours bizHours = businessHoursMap.get(day);
                List<OperatingHours> opHours = operatingHoursMap.getOrDefault(day, List.of());

                schedules.add(new DayScheduleResult(day.getValue(), bizHours, opHours));
            }

            // 4. 요일 순으로 정렬
            schedules.sort(Comparator.comparing(DayScheduleResult::dayOfWeek));

            return new OperatingHoursResult(businessId, businessName, schedules);
        }
    }

    // 요일별 스케줄 결과
    public record DayScheduleResult(
            Integer dayOfWeek,
            String openTime,
            String closeTime,
            Boolean isClosed,
            List<TimeRangeResult> bookingTimeRanges
    ) {
        public DayScheduleResult(
                Integer dayOfWeek,
                BusinessHours businessHours,
                List<OperatingHours> operatingHours) {

            this(
                    dayOfWeek,
                    // BusinessHours 정보
                    (businessHours != null && !businessHours.getIsClosed())
                            ? businessHours.getOpenTime().toString()
                            : null,
                    (businessHours != null && !businessHours.getIsClosed())
                            ? businessHours.getCloseTime().toString()
                            : null,
                    (businessHours == null || businessHours.getIsClosed()),
                    // OperatingHours → TimeRangeResult 변환
                    operatingHours.stream()
                            .filter(h -> !h.getIsClosed())
                            .sorted(Comparator.comparing(OperatingHours::getSequence))
                            .map(TimeRangeResult::from)
                            .collect(Collectors.toList())
            );
        }
    }

    // 예약 가능 시간대 결과
    public record TimeRangeResult(
            String startTime,
            String endTime
    ) {
        public static TimeRangeResult from(OperatingHours hours) {
            return new TimeRangeResult(
                    hours.getOpenTime().toString(),
                    hours.getCloseTime().toString()
            );
        }
    }
}