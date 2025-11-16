package timefit.operatinghours.service.util;

import timefit.business.entity.Business;
import timefit.business.entity.BusinessHours;
import timefit.business.entity.OperatingHours;
import timefit.common.entity.DayOfWeek;
import timefit.operatinghours.dto.OperatingHoursRequest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OperatingHoursConverter {

    /**
     * DaySchedule DTO → BusinessHours Entity 변환
     *
     * @param business 업체 엔티티
     * @param schedule 요일별 스케줄 DTO
     * @param dayOfWeek 요일 Enum
     * @return BusinessHours 엔티티
     */
    public static BusinessHours convertToBusinessHours(
            Business business,
            OperatingHoursRequest.DaySchedule schedule,
            DayOfWeek dayOfWeek) {

        // 휴무일 처리
        if (Boolean.TRUE.equals(schedule.getIsClosed())) {
            return BusinessHours.createClosedDay(business, dayOfWeek);
        }

        // 영업일 처리
        LocalTime openTime = LocalTime.parse(schedule.getOpenTime());
        LocalTime closeTime = LocalTime.parse(schedule.getCloseTime());

        return BusinessHours.createOpenDay(business, dayOfWeek, openTime, closeTime);
    }

    /**
     * TimeRange DTO 리스트 → OperatingHours Entity 리스트 변환
     *
     * @param business 업체 엔티티
     * @param ranges 예약 가능 시간대 DTO 리스트
     * @param dayOfWeek 요일 Enum
     * @return OperatingHours 엔티티 리스트
     */
    public static List<OperatingHours> convertToOperatingHours(
            Business business,
            List<OperatingHoursRequest.TimeRange> ranges,
            DayOfWeek dayOfWeek) {

        List<OperatingHours> result = new ArrayList<>();
        int sequence = 0;

        for (OperatingHoursRequest.TimeRange range : ranges) {
            LocalTime startTime = LocalTime.parse(range.getStartTime());
            LocalTime endTime = LocalTime.parse(range.getEndTime());

            result.add(OperatingHours.createOperatingHours(
                    business, dayOfWeek, startTime, endTime, false, sequence++
            ));
        }

        return result;
    }
}