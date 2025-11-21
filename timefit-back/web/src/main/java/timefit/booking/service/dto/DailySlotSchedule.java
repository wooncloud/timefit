package timefit.booking.service.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * 일별 슬롯 생성 스케줄 (내부 Service DTO)
 * 역할:
 * - 특정 날짜에 대한 슬롯 생성 계획을 표현
 * - 해당 날짜의 예약 가능한 시간대 목록을 포함
 * 사용 예시:
 * - 2025-01-15 날짜에 대해
 * - 09:00~12:00, 13:00~18:00 시간대에
 * - 30분 간격으로 슬롯 생성
 * 참고:
 * - timeRanges 비어있으면 영업시간 전체를 사용
 * - timeRanges 있으면 지정된 시간대만 사용
 */
public record DailySlotSchedule(
        @NotNull(message = "날짜는 필수입니다")
        LocalDate date,

        @NotNull(message = "시간대 목록은 필수입니다")
        List<AvailableTimeRange> timeRanges
) {
    // 정적 팩토리 메서드
    public static DailySlotSchedule of(LocalDate date, List<AvailableTimeRange> timeRanges) {
        return new DailySlotSchedule(date, timeRanges);
    }
}