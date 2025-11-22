package timefit.operatinghours.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

public class OperatingHoursRequestDto {

    // 영업시간 + 예약 가능 시간대 통합 설정
    @Getter
    public static class SetOperatingHours {
        @NotNull
        private List<DaySchedule> schedules;
    }

    // 요일별 스케줄
    @Getter
    public static class DaySchedule {
        @NotNull
        @Min(0) @Max(6)
        private Integer dayOfWeek;

        // 총 영업시간 (BusinessHours)
        private String openTime;        // "09:00"
        private String closeTime;       // "22:00"
        private Boolean isClosed;       // 휴무일 여부

        // 예약 가능 시간대 (OperatingHours)
        private List<TimeRange> bookingTimeRanges;
    }

    // 예약 가능 시간대 범위
    @Getter
    public static class TimeRange {
        @NotNull
        private String startTime;  // "09:00"

        @NotNull
        private String endTime;    // "10:00"
    }
}