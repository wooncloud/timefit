package timefit.booking.dto;

import lombok.Getter;
import timefit.booking.entity.BookingSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BookingSlot 응답 DTO
 * 기존 파일: ScheduleResponseDto.java
 */
public class BookingSlotResponse {

    /**
     * 슬롯 상세 정보
     */
    @Getter
    public static class SlotDetail {
        private final UUID slotId;
        private final UUID businessId;
        private final UUID menuId;
        private final String menuName;
        private final LocalDate slotDate;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer capacity;
        private final Integer currentBookings;
        private final Integer availableCapacity;
        private final Boolean isAvailable;

        private SlotDetail(UUID slotId, UUID businessId, UUID menuId, String menuName,
                           LocalDate slotDate, LocalTime startTime, LocalTime endTime,
                           Integer capacity, Integer currentBookings, Integer availableCapacity,
                           Boolean isAvailable) {
            this.slotId = slotId;
            this.businessId = businessId;
            this.menuId = menuId;
            this.menuName = menuName;
            this.slotDate = slotDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.capacity = capacity;
            this.currentBookings = currentBookings;
            this.availableCapacity = availableCapacity;
            this.isAvailable = isAvailable;
        }

        /**
         * BookingSlot Entity → DTO 변환 (정적 팩토리 메서드)
         */
        public static SlotDetail of(BookingSlot slot, Integer currentBookings) {
            Integer availableCapacity = slot.getCapacity() - currentBookings;

            return new SlotDetail(
                    slot.getId(),
                    slot.getBusiness().getId(),
                    slot.getMenu().getId(),
                    slot.getMenu().getServiceName(),
                    slot.getSlotDate(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.getCapacity(),
                    currentBookings,
                    availableCapacity,
                    slot.getIsAvailable() && availableCapacity > 0
            );
        }

        /**
         * BookingSlot Entity → DTO 변환 (현재 예약 수 없이)
         */
        public static SlotDetail of(BookingSlot slot) {
            return of(slot, 0);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SlotDetail that = (SlotDetail) o;
            return Objects.equals(slotId, that.slotId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(slotId);
        }
    }

    /**
     * 슬롯 목록 응답
     */
    @Getter
    public static class SlotList {
        private final UUID businessId;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final Integer totalSlots;
        private final List<SlotDetail> slots;

        private SlotList(UUID businessId, LocalDate startDate, LocalDate endDate,
                         Integer totalSlots, List<SlotDetail> slots) {
            this.businessId = businessId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalSlots = totalSlots;
            this.slots = slots;
        }

        public static SlotList of(UUID businessId, LocalDate startDate, LocalDate endDate,
                                  List<SlotDetail> slots) {
            return new SlotList(businessId, startDate, endDate, slots.size(), slots);
        }

        public static SlotList of(UUID businessId, LocalDate startDate, LocalDate endDate,
                                  List<BookingSlot> slots, java.util.function.Function<BookingSlot, Integer> currentBookingsGetter) {
            List<SlotDetail> slotDetails = slots.stream()
                    .map(slot -> SlotDetail.of(slot, currentBookingsGetter.apply(slot)))
                    .collect(Collectors.toList());

            return of(businessId, startDate, endDate, slotDetails);
        }
    }

    /**
     * 슬롯 생성 결과
     */
    @Getter
    public static class SlotCreationResult {
        private final Integer totalRequested;
        private final Integer successCount;
        private final List<SlotDetail> createdSlots;

        private SlotCreationResult(Integer totalRequested, Integer successCount,
                                   List<SlotDetail> createdSlots) {
            this.totalRequested = totalRequested;
            this.successCount = successCount;
            this.createdSlots = createdSlots;
        }

        public static SlotCreationResult of(Integer totalRequested, Integer successCount,
                                            List<SlotDetail> createdSlots) {
            return new SlotCreationResult(totalRequested, successCount, createdSlots);
        }

        public static SlotCreationResult of(Integer totalRequested, List<BookingSlot> createdSlots) {
            List<SlotDetail> slotDetails = createdSlots.stream()
                    .map(SlotDetail::of)
                    .collect(Collectors.toList());

            return of(totalRequested, createdSlots.size(), slotDetails);
        }
    }

    /**
     * 날짜별 슬롯 그룹 응답
     */
    @Getter
    public static class DailySlots {
        private final LocalDate date;
        private final Integer totalSlots;
        private final Integer availableSlots;
        private final List<SlotDetail> slots;

        private DailySlots(LocalDate date, Integer totalSlots, Integer availableSlots,
                           List<SlotDetail> slots) {
            this.date = date;
            this.totalSlots = totalSlots;
            this.availableSlots = availableSlots;
            this.slots = slots;
        }

        public static DailySlots of(LocalDate date, List<SlotDetail> slots) {
            Integer availableCount = (int) slots.stream()
                    .filter(SlotDetail::getIsAvailable)
                    .count();

            return new DailySlots(date, slots.size(), availableCount, slots);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DailySlots that = (DailySlots) o;
            return Objects.equals(date, that.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date);
        }
    }

    /**
     * 슬롯 요약 정보 (캘린더용)
     */
    @Getter
    public static class SlotSummary {
        private final UUID slotId;
        private final LocalDate slotDate;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Boolean isAvailable;

        private SlotSummary(UUID slotId, LocalDate slotDate, LocalTime startTime,
                            LocalTime endTime, Boolean isAvailable) {
            this.slotId = slotId;
            this.slotDate = slotDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.isAvailable = isAvailable;
        }

        public static SlotSummary of(BookingSlot slot) {
            return new SlotSummary(
                    slot.getId(),
                    slot.getSlotDate(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.getIsAvailable()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SlotSummary that = (SlotSummary) o;
            return Objects.equals(slotId, that.slotId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(slotId);
        }
    }
}