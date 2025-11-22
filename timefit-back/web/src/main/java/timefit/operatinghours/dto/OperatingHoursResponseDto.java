package timefit.operatinghours.dto;

import java.util.List;
import java.util.UUID;

public class OperatingHoursResponseDto {

    // 영업시간 + 예약 가능 시간대 통합 조회 결과
    public record OperatingHours(
            UUID businessId,
            String businessName,
            List<DaySchedule> schedules
    ) {}

    // 요일별 스케줄 결과
    public record DaySchedule(
            Integer dayOfWeek,
            String openTime,
            String closeTime,
            Boolean isClosed,
            List<TimeRange> bookingTimeRanges
    ) {}

    // 예약 가능 시간대 결과
    public record TimeRange(
            String startTime,
            String endTime
    ) {}
}