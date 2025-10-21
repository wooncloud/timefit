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
     * 슬롯 생성 요청
     */
    @Getter
    public static class CreateSlot {
        @NotNull(message = "메뉴 ID는 필수입니다")
        private final UUID menuId;

        @NotNull(message = "슬롯 날짜는 필수입니다")
        private final LocalDate slotDate;

        @NotNull(message = "시작 시간은 필수입니다")
        private final LocalTime startTime;

        @NotNull(message = "종료 시간은 필수입니다")
        private final LocalTime endTime;

        @NotNull(message = "용량은 필수입니다")
        @Positive(message = "용량은 1 이상이어야 합니다")
        private final Integer capacity;

        private CreateSlot(UUID menuId, LocalDate slotDate, LocalTime startTime,
                           LocalTime endTime, Integer capacity) {
            this.menuId = menuId;
            this.slotDate = slotDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.capacity = capacity;
        }

        public static CreateSlot of(UUID menuId, LocalDate slotDate, LocalTime startTime,
                                    LocalTime endTime, Integer capacity) {
            return new CreateSlot(menuId, slotDate, startTime, endTime, capacity);
        }
    }

    /**
     * 여러 슬롯 일괄 생성 요청
     */
    @Getter
    public static class CreateMultipleSlots {
        @NotNull(message = "메뉴 ID는 필수입니다")
        private final UUID menuId;

        @NotNull(message = "시작 날짜는 필수입니다")
        private final LocalDate startDate;

        @NotNull(message = "종료 날짜는 필수입니다")
        private final LocalDate endDate;

        @NotNull(message = "슬롯 시간 목록은 필수입니다")
        private final List<SlotTime> slotTimes;

        @NotNull(message = "용량은 필수입니다")
        @Positive(message = "용량은 1 이상이어야 합니다")
        private final Integer capacity;

        private CreateMultipleSlots(UUID menuId, LocalDate startDate, LocalDate endDate,
                                    List<SlotTime> slotTimes, Integer capacity) {
            this.menuId = menuId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.slotTimes = slotTimes;
            this.capacity = capacity;
        }

        public static CreateMultipleSlots of(UUID menuId, LocalDate startDate, LocalDate endDate,
                                             List<SlotTime> slotTimes, Integer capacity) {
            return new CreateMultipleSlots(menuId, startDate, endDate, slotTimes, capacity);
        }
    }

    /**
     * 슬롯 시간 정보
     */
    @Getter
    public static class SlotTime {
        @NotNull(message = "시작 시간은 필수입니다")
        private final LocalTime startTime;

        @NotNull(message = "종료 시간은 필수입니다")
        private final LocalTime endTime;

        private SlotTime(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public static SlotTime of(LocalTime startTime, LocalTime endTime) {
            return new SlotTime(startTime, endTime);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SlotTime slotTime = (SlotTime) o;
            return Objects.equals(startTime, slotTime.startTime) &&
                    Objects.equals(endTime, slotTime.endTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startTime, endTime);
        }
    }

    /**
     * 슬롯 조회 요청
     */
    @Getter
    public static class GetSlots {
        @NotNull(message = "시작 날짜는 필수입니다")
        private final LocalDate startDate;

        @NotNull(message = "종료 날짜는 필수입니다")
        private final LocalDate endDate;

        private final UUID menuId;  // Optional

        private GetSlots(LocalDate startDate, LocalDate endDate, UUID menuId) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.menuId = menuId;
        }

        public static GetSlots of(LocalDate startDate, LocalDate endDate, UUID menuId) {
            return new GetSlots(startDate, endDate, menuId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GetSlots getSlots = (GetSlots) o;
            return Objects.equals(startDate, getSlots.startDate) &&
                    Objects.equals(endDate, getSlots.endDate) &&
                    Objects.equals(menuId, getSlots.menuId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startDate, endDate, menuId);
        }
    }
}