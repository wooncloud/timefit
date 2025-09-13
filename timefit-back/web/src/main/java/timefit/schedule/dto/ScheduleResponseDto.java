package timefit.schedule.dto;

import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ScheduleResponseDto {

    /**
     * 영업시간 설정 결과
     */
    @Getter
    public static class OperatingHoursResult {
        private final UUID businessId;
        private final String businessName;
        private final List<BusinessHourDetail> businessHours;
        private final LocalDateTime updatedAt;

        private OperatingHoursResult(UUID businessId, String businessName,
                                        List<BusinessHourDetail> businessHours, LocalDateTime updatedAt) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessHours = businessHours;
            this.updatedAt = updatedAt;
        }

        public static OperatingHoursResult of(UUID businessId, String businessName,
                                                List<BusinessHourDetail> businessHours, LocalDateTime updatedAt) {
            return new OperatingHoursResult(businessId, businessName, businessHours, updatedAt);
        }
    }

    /**
     * 개별 영업시간 상세 정보
     */
    @Getter
    public static class BusinessHourDetail {
        private final UUID hourId;
        private final Integer dayOfWeek;
        private final String dayName;
        private final LocalTime openTime;
        private final LocalTime closeTime;
        private final Boolean isClosed;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private BusinessHourDetail(UUID hourId, Integer dayOfWeek, String dayName,
                                    LocalTime openTime, LocalTime closeTime, Boolean isClosed,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.hourId = hourId;
            this.dayOfWeek = dayOfWeek;
            this.dayName = dayName;
            this.openTime = openTime;
            this.closeTime = closeTime;
            this.isClosed = isClosed;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static BusinessHourDetail of(UUID hourId, Integer dayOfWeek, String dayName,
                                            LocalTime openTime, LocalTime closeTime, Boolean isClosed,
                                            LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new BusinessHourDetail(hourId, dayOfWeek, dayName, openTime, closeTime,
                    isClosed, createdAt, updatedAt);
        }
    }


    /**
     * 예약 슬롯 상세 정보
     */
    @Getter
    public static class SlotDetail {
        private final UUID slotId;
        private final UUID businessId;
        private final LocalDate slotDate;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer capacity;
        private final Integer currentBookings;
        private final Boolean isAvailable;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private SlotDetail(UUID slotId, UUID businessId, LocalDate slotDate, LocalTime startTime,
                           LocalTime endTime, Integer capacity, Integer currentBookings, Boolean isAvailable,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.slotId = slotId;
            this.businessId = businessId;
            this.slotDate = slotDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.capacity = capacity;
            this.currentBookings = currentBookings;
            this.isAvailable = isAvailable;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static SlotDetail of(UUID slotId, UUID businessId, LocalDate slotDate, LocalTime startTime,
                                    LocalTime endTime, Integer capacity, Integer currentBookings, Boolean isAvailable,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new SlotDetail(slotId, businessId, slotDate, startTime, endTime, capacity,
                    currentBookings, isAvailable, createdAt, updatedAt);
        }
    }

    /**
     * 슬롯 요약 정보 (목록용)
     */
    @Getter
    public static class SlotSummary {
        private final UUID slotId;
        private final LocalDate slotDate;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Integer capacity;
        private final Integer currentBookings;
        private final Boolean isAvailable;

        private SlotSummary(UUID slotId, LocalDate slotDate, LocalTime startTime, LocalTime endTime,
                            Integer capacity, Integer currentBookings, Boolean isAvailable) {
            this.slotId = slotId;
            this.slotDate = slotDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.capacity = capacity;
            this.currentBookings = currentBookings;
            this.isAvailable = isAvailable;
        }

        public static SlotSummary of(UUID slotId, LocalDate slotDate, LocalTime startTime, LocalTime endTime,
                                     Integer capacity, Integer currentBookings, Boolean isAvailable) {
            return new SlotSummary(slotId, slotDate, startTime, endTime, capacity, currentBookings, isAvailable);
        }
    }

    /**
     * 날짜별 슬롯 목록
     */
    @Getter
    public static class DailySlotsResult {
        private final LocalDate date;
        private final String dayOfWeek;
        private final Boolean isBusinessOpen;
        private final List<SlotSummary> slots;

        private DailySlotsResult(LocalDate date, String dayOfWeek, Boolean isBusinessOpen, List<SlotSummary> slots) {
            this.date = date;
            this.dayOfWeek = dayOfWeek;
            this.isBusinessOpen = isBusinessOpen;
            this.slots = slots;
        }

        public static DailySlotsResult of(LocalDate date, String dayOfWeek, Boolean isBusinessOpen, List<SlotSummary> slots) {
            return new DailySlotsResult(date, dayOfWeek, isBusinessOpen, slots);
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

        public static SlotCreationResult of(Integer totalRequested, Integer successCount, List<SlotDetail> createdSlots) {
            return new SlotCreationResult(totalRequested, successCount, createdSlots);
        }
    }
}