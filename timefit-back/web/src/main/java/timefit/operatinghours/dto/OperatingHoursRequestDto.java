package timefit.operatinghours.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OperatingHoursRequestDto {

    // 영업시간 + 예약 가능 시간대 통합 설정
    public record SetOperatingHours(
            @NotNull(message = "스케줄 목록은 필수입니다")
            List<DaySchedule> schedules
    ) {}

    // 요일별 스케줄
    public record DaySchedule(
            @NotNull(message = "요일은 필수입니다")
            @Min(value = 0, message = "요일은 0(일요일)부터 6(토요일)까지입니다")
            @Max(value = 6, message = "요일은 0(일요일)부터 6(토요일)까지입니다")
            Integer dayOfWeek,

            // 총 영업시간 (BusinessHours)
            String openTime,        // "09:00"
            String closeTime,       // "22:00"
            Boolean isClosed,       // 휴무일 여부

            // 예약 가능 시간대 (OperatingHours)
            List<TimeRange> bookingTimeRanges
    ) {}

    // 예약 가능 시간대 범위
    public record TimeRange(
            @NotNull(message = "시작 시간은 필수입니다")
            String startTime,  // "09:00"

            @NotNull(message = "종료 시간은 필수입니다")
            String endTime     // "10:00"
    ) {}
}