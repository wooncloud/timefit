package timefit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import timefit.booking.service.dto.DailySlotSchedule;

import java.util.List;
import java.util.UUID;

public class BookingSlotRequest {

    /**
     * 예약 슬롯 생성 요청
     * 역할:
     * - 특정 메뉴에 대한 예약 슬롯을 생성
     * - 여러 날짜에 대해 일괄 생성 가능
     * 사용 예시:
     * - menuId: 헤어 커트 서비스
     * - slotIntervalMinutes: 30분 간격
     * - schedules: 2025-01-15 ~ 2025-01-31 일별 스케줄
     */
    public record BookingSlot(
            @NotNull(message = "메뉴 ID는 필수입니다")
            UUID menuId,

            @NotNull(message = "슬롯 간격은 필수입니다")
            @Positive(message = "슬롯 간격은 양수여야 합니다")
            Integer slotIntervalMinutes,

            @NotNull(message = "일별 스케줄은 필수입니다")
            List<DailySlotSchedule> schedules
    ) {
        // 정적 팩토리 메서드
        public static BookingSlot of(
                UUID menuId,
                Integer slotIntervalMinutes,
                List<DailySlotSchedule> schedules) {
            return new BookingSlot(menuId, slotIntervalMinutes, schedules);
        }
    }
}