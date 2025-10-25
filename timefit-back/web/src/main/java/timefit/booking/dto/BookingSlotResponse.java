package timefit.booking.dto;

import lombok.Getter;
import timefit.booking.entity.BookingSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * BookingSlot 응답 DTO
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
        private final Integer currentBookings; // 현재 예약 수 (0 또는 1)
        private final Boolean isAvailable; // 예약 가능 여부

        // ❌ 제거: capacity
        // ❌ 제거: availableCapacity

        private SlotDetail(UUID slotId, UUID businessId, UUID menuId, String menuName,
                           LocalDate slotDate, LocalTime startTime, LocalTime endTime,
                           Integer currentBookings, Boolean isAvailable) {
            this.slotId = slotId;
            this.businessId = businessId;
            this.menuId = menuId;
            this.menuName = menuName;
            this.slotDate = slotDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.currentBookings = currentBookings;
            this.isAvailable = isAvailable;
        }

        /**
         * BookingSlot Entity → DTO 변환
         *
         * @param slot BookingSlot 엔티티
         * @param currentBookings 현재 예약 수 (0 또는 1)
         * @return SlotDetail DTO
         */
        public static SlotDetail of(BookingSlot slot, Integer currentBookings) {
            return new SlotDetail(
                    slot.getId(),
                    slot.getBusiness().getId(),
                    slot.getMenu().getId(),
                    slot.getMenu().getServiceName(),
                    slot.getSlotDate(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    currentBookings,
                    slot.getIsAvailable() && currentBookings == 0
            );
        }

        // BookingSlot Entity → DTO 변환 (현재 예약 수 없이)
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
    }

    /**
     * 슬롯 생성 결과
     */
    @Getter
    public static class SlotCreationResult {
        private final Integer totalRequested; // 요청된 슬롯 수
        private final Integer created; // 실제 생성된 슬롯 수
        private final Integer skipped; // 중복으로 건너뛴 슬롯 수

        private SlotCreationResult(Integer totalRequested, Integer created, Integer skipped) {
            this.totalRequested = totalRequested;
            this.created = created;
            this.skipped = skipped;
        }

        public static SlotCreationResult of(Integer totalRequested, List<BookingSlot> createdSlots) {
            int created = createdSlots.size();
            int skipped = totalRequested - created;
            return new SlotCreationResult(totalRequested, created, skipped);
        }
    }
}