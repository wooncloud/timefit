package timefit.schedule.dto;

import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class ScheduleRequestDto {

    /**
     * 영업시간 설정 요청
     */
    @Getter
    public static class SetOperatingHours {
        private final List<BusinessHour> businessHours;

        private SetOperatingHours(List<BusinessHour> businessHours) {
            this.businessHours = businessHours;
        }

        public static SetOperatingHours of(List<BusinessHour> businessHours) {
            return new SetOperatingHours(businessHours);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            SetOperatingHours that = (SetOperatingHours) other;
            return Objects.equals(businessHours, that.businessHours);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessHours);
        }
    }

    /**
     * 개별 영업시간 정보
     */
    @Getter
    public static class BusinessHour {
        private final Integer dayOfWeek;
        private final LocalTime openTime;
        private final LocalTime closeTime;
        private final Boolean isClosed;

        private BusinessHour(Integer dayOfWeek, LocalTime openTime, LocalTime closeTime, Boolean isClosed) {
            this.dayOfWeek = dayOfWeek;
            this.openTime = openTime;
            this.closeTime = closeTime;
            this.isClosed = isClosed;
        }

        public static BusinessHour of(Integer dayOfWeek, LocalTime openTime, LocalTime closeTime, Boolean isClosed) {
            return new BusinessHour(dayOfWeek, openTime, closeTime, isClosed);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            BusinessHour that = (BusinessHour) other;
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


    /**
     * 예약 슬롯 생성 요청
     */
    @Getter
    public static class CreateSlot {
        private final LocalDate slotDate;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer capacity;

        private CreateSlot(LocalDate slotDate, LocalTime startTime, LocalTime endTime, Integer capacity) {
            this.slotDate = slotDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.capacity = capacity;
        }

        public static CreateSlot of(LocalDate slotDate, LocalTime startTime, LocalTime endTime, Integer capacity) {
            return new CreateSlot(slotDate, startTime, endTime, capacity);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            CreateSlot that = (CreateSlot) other;
            return Objects.equals(slotDate, that.slotDate) &&
                    Objects.equals(startTime, that.startTime) &&
                    Objects.equals(endTime, that.endTime) &&
                    Objects.equals(capacity, that.capacity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(slotDate, startTime, endTime, capacity);
        }
    }

    /**
     * 여러 슬롯 일괄 생성 요청
     */
    @Getter
    public static class CreateMultipleSlots {
        private final List<CreateSlot> slots;

        private CreateMultipleSlots(List<CreateSlot> slots) {
            this.slots = slots;
        }

        public static CreateMultipleSlots of(List<CreateSlot> slots) {
            return new CreateMultipleSlots(slots);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            CreateMultipleSlots that = (CreateMultipleSlots) other;
            return Objects.equals(slots, that.slots);
        }

        @Override
        public int hashCode() {
            return Objects.hash(slots);
        }
    }

    /**
     * 슬롯 수정 요청
     */
    @Getter
    public static class UpdateSlot {
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer capacity;
        private final Boolean isAvailable;

        private UpdateSlot(LocalTime startTime, LocalTime endTime, Integer capacity, Boolean isAvailable) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.capacity = capacity;
            this.isAvailable = isAvailable;
        }

        public static UpdateSlot of(LocalTime startTime, LocalTime endTime, Integer capacity, Boolean isAvailable) {
            return new UpdateSlot(startTime, endTime, capacity, isAvailable);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            UpdateSlot that = (UpdateSlot) other;
            return Objects.equals(startTime, that.startTime) &&
                    Objects.equals(endTime, that.endTime) &&
                    Objects.equals(capacity, that.capacity) &&
                    Objects.equals(isAvailable, that.isAvailable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startTime, endTime, capacity, isAvailable);
        }
    }
}