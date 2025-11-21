package timefit.booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class BookingSlotResponse {

    /**
     * 슬롯 생성 결과
     * 역할:
     * - 슬롯 생성 작업의 결과 요약 정보
     * - 요청/생성/건너뛴 슬롯 수를 포함
     * 사용 예시:
     * - totalRequested: 100개 슬롯 생성 요청
     * - created: 95개 생성 성공
     * - skipped: 5개 건너뜀 (중복 또는 영업시간 외)
     */
    public record CreationResult(
            Integer totalRequested,
            Integer created,
            Integer skipped
    ) {
        // 정적 팩토리 메서드
        public static CreationResult of(Integer totalRequested, List<BookingSlot> createdSlots) {
            int created = createdSlots.size();
            int skipped = totalRequested - created;
            return new CreationResult(totalRequested, created, skipped);
        }
    }

    /**
     * 예약 슬롯 정보 (단일)
     * 역할:
     * - 단일 예약 슬롯의 상세 정보
     * - 현재 예약 수를 포함하여 가용성 표시
     * 사용 예시:
     * - slotId: UUID
     * - slotDate: 2025-01-15
     * - startTime: 09:00
     * - endTime: 09:30
     * - isAvailable: 예약 가능 여부
     */
    public record BookingSlot(
            UUID slotId,
            UUID businessId,
            UUID menuId,
            String menuName,
            LocalDate slotDate,
            LocalTime startTime,
            LocalTime endTime,
            Boolean isAvailable
    ) {
        /**
         * Entity → DTO 변환 (정적 팩토리)
         *
         * @param slot BookingSlot 엔티티
         * @return BookingSlot DTO
         */
        public static BookingSlot of(
                timefit.booking.entity.BookingSlot slot
        ) {
            return new BookingSlot(
                    slot.getId(),
                    slot.getBusiness().getId(),
                    slot.getMenu().getId(),
                    slot.getMenu().getServiceName(),
                    slot.getSlotDate(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.getIsAvailable()
            );
        }
    }

    /**
     * 예약 슬롯 목록 (복수)
     * 역할:
     * - 여러 예약 슬롯의 목록 정보
     * - 조회 기간 정보를 포함
     * 사용 예시:
     * - businessId: 업체 ID
     * - startDate: 2025-01-01
     * - endDate: 2025-01-31
     * - slots: 해당 기간의 모든 슬롯 목록
     */
    public record BookingSlotList(
            UUID businessId,
            LocalDate startDate,
            LocalDate endDate,
            List<BookingSlot> slots
    ) {
        // 정적 팩토리 메서드
        public static BookingSlotList of(
                UUID businessId,
                LocalDate startDate,
                LocalDate endDate,
                List<BookingSlot> slots) {
            return new BookingSlotList(businessId, startDate, endDate, slots);
        }
    }
}