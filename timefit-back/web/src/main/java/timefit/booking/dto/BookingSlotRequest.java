package timefit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * BookingSlot 요청 DTO
 */
public class BookingSlotRequest {

    /**
     * 슬롯 생성 요청 (허용된 날짜+시간만 전달)
     */
    @Getter
    public static class Create {
        @NotNull(message = "메뉴 ID는 필수입니다")
        private final UUID menuId;

        @NotNull(message = "슬롯 간격은 필수입니다")
        @Positive(message = "슬롯 간격은 양수여야 합니다")
        private final Integer slotInterval; // 분 단위 (예: 60)

        @NotNull(message = "날짜별 시간 슬롯은 필수입니다")
        private final List<DailyTimeSlot> dailyTimeSlots;

        private Create(UUID menuId, Integer slotInterval, List<DailyTimeSlot> dailyTimeSlots) {
            this.menuId = menuId;
            this.slotInterval = slotInterval;
            this.dailyTimeSlots = dailyTimeSlots;
        }

        public static Create of(UUID menuId, Integer slotInterval,
                                List<DailyTimeSlot> dailyTimeSlots) {
            return new Create(menuId, slotInterval, dailyTimeSlots);
        }
    }

    /**
     * 특정 날짜의 허용된 시간대들
     */
    @Getter
    public static class DailyTimeSlot {
        @NotNull(message = "날짜는 필수입니다")
        private final LocalDate date;

        @NotNull(message = "시간대 목록은 필수입니다")
        private final List<TimeRange> timeRanges;

        private DailyTimeSlot(LocalDate date, List<TimeRange> timeRanges) {
            this.date = date;
            this.timeRanges = timeRanges;
        }

        public static DailyTimeSlot of(LocalDate date, List<TimeRange> timeRanges) {
            return new DailyTimeSlot(date, timeRanges);
        }
    }

    /**
     * 시간 범위
     */
    @Getter
    public static class TimeRange {
        @NotNull(message = "시작 시간은 필수입니다")
        private final LocalTime startTime;

        @NotNull(message = "종료 시간은 필수입니다")
        private final LocalTime endTime;

        private TimeRange(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public static TimeRange of(LocalTime startTime, LocalTime endTime) {
            return new TimeRange(startTime, endTime);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeRange timeRange = (TimeRange) o;
            return Objects.equals(startTime, timeRange.startTime) &&
                    Objects.equals(endTime, timeRange.endTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startTime, endTime);
        }
    }
}