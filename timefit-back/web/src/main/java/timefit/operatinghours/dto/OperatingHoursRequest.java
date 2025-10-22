package timefit.operatinghours.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

public class OperatingHoursRequest {

    // 영업시간 설정 요청
    @Getter
    public static class SetOperatingHours {

        @NotNull(message = "영업시간 목록은 필수입니다")
        @Valid
        private final List<DaySchedule> businessHours;

        public SetOperatingHours(List<DaySchedule> businessHours) {
            this.businessHours = businessHours;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SetOperatingHours that = (SetOperatingHours) o;
            return Objects.equals(businessHours, that.businessHours);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessHours);
        }
    }

    // 요일별 일정
    @Getter
    public static class DaySchedule {

        @NotNull(message = "요일은 필수입니다")
        private final Integer dayOfWeek;  // 0~6

        private final String openTime;    // "HH:mm"

        private final String closeTime;   // "HH:mm"

        @NotNull(message = "휴무 여부는 필수입니다")
        private final Boolean isClosed;

        public DaySchedule(Integer dayOfWeek, String openTime,
                           String closeTime, Boolean isClosed) {
            this.dayOfWeek = dayOfWeek;
            this.openTime = openTime;
            this.closeTime = closeTime;
            this.isClosed = isClosed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DaySchedule that = (DaySchedule) o;
            return Objects.equals(dayOfWeek, that.dayOfWeek) &&
                    Objects.equals(openTime, that.openTime) &&
                    Objects.equals(closeTime, that.closeTime) &&
                    Objects.equals(isClosed, that.isClosed);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dayOfWeek, openTime, closeTime, isClosed);
        }
    }
}