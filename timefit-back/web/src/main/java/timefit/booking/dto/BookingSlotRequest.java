package timefit.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import timefit.booking.service.dto.DailySlotSchedule;

import java.util.List;
import java.util.UUID;

@Schema(description = "예약 슬롯 요청")
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
    @Schema(description = "예약 슬롯 생성 요청")
    public record BookingSlot(
            @Schema(
                    description = "예약 슬롯을 생성할 메뉴 ID",
                    example = "60000000-0000-0000-0000-000000000001",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "메뉴 ID는 필수입니다")
            UUID menuId,

            @Schema(
                    description = "슬롯 간격 (분). 메뉴 소요 시간보다 길거나 같아야 함",
                    example = "30",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "슬롯 간격은 필수입니다")
            @Positive(message = "슬롯 간격은 양수여야 합니다")
            @Min(value = 1, message = "슬롯 간격은 최소 1분 이상이어야 합니다")
            Integer slotIntervalMinutes,

            @Schema(
                    description = "일별 슬롯 생성 스케줄 목록",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
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