package timefit.booking.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/**
 * 예약 가능한 시간 범위 (내부 Service DTO)
 * 역할:
 * - 특정 날짜에 슬롯 생성이 가능한 시간대를 표현
 * - 영업시간 내에서 사업자가 지정한 예약 가능 시간 범위
 * 사용 예시:
 * - 영업시간: 09:00~18:00
 * - 점심시간 제외: 12:00~13:00
 * → AvailableTimeRange(09:00, 12:00), AvailableTimeRange(13:00, 18:00)
 */
@Schema(description = "슬롯 생성이 가능한 시간 범위")
public record AvailableTimeRange(
        @Schema(description = "시작 시간 (HH:mm)", example = "09:00")
        @NotNull(message = "시작 시간은 필수입니다")
        LocalTime startTime,

        @Schema(description = "종료 시간 (HH:mm)", example = "12:00")
        @NotNull(message = "종료 시간은 필수입니다")
        LocalTime endTime
) {
    // 정적 팩토리 메서드
    public static AvailableTimeRange of(LocalTime startTime, LocalTime endTime) {
        return new AvailableTimeRange(startTime, endTime);
    }
}