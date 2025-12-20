package timefit.menu.service.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.service.dto.AvailableTimeRange;
import timefit.booking.service.dto.DailySlotSchedule;
import timefit.menu.dto.MenuRequestDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Menu DTO → BookingSlot Schedule 변환기
 * - Menu 생성/수정 요청 → DailySlotSchedule 변환
 * - 2개월 범위 자동 생성
 * - String 시간 → LocalTime 변환
 */
@Slf4j
@Component
public class MenuScheduleConverter {

    private static final int AUTO_GENERATION_MONTHS = 2;

    /**
     * Menu 요청 → BookingSlot 스케줄 변환
     * [처리 흐름]
     * 1. 2개월 범위 생성 (오늘 ~ +2개월)
     * 2. specificTimeRanges → AvailableTimeRange 변환
     * 3. 모든 날짜에 동일한 시간대 적용
     * [사용처]
     * - Menu 생성 시 BookingSlot 자동 생성
     * - Menu 수정 시 BookingSlot 재생성
     *
     * @param request Menu 생성/수정 요청
     * @return DailySlotSchedule 목록 (2개월치)
     */
    public List<DailySlotSchedule> convertToBookingSlotSchedules(
            MenuRequestDto.CreateUpdateMenu request) {

        // 1. 2개월 범위 생성
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(AUTO_GENERATION_MONTHS);

        log.debug("BookingSlot 스케줄 생성 범위: {} ~ {} ({} 개월)",
                startDate, endDate, AUTO_GENERATION_MONTHS);

        // 2. Menu DTO → BookingSlot DTO 변환
        return convertToSchedules(
                startDate,
                endDate,
                request.slotSettings().specificTimeRanges()
        );
    }

    /**
     * 날짜 범위 + 시간대 → DailySlotSchedule 변환
     * [변환 흐름]
     * 1. MenuRequestDto.TimeRange → AvailableTimeRange
     * 2. 모든 날짜에 동일한 시간대 적용
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param specificTimeRanges Menu DTO의 시간대 목록
     * @return DailySlotSchedule 목록
     */
    private List<DailySlotSchedule> convertToSchedules(
            LocalDate startDate,
            LocalDate endDate,
            List<MenuRequestDto.TimeRange> specificTimeRanges) {

        // 1. String 시간 → AvailableTimeRange 변환
        // - "09:00" (String) → LocalTime.of(9, 0)
        List<AvailableTimeRange> timeRanges = specificTimeRanges.stream()
                // 1-1) MenuRequestDto.TimeRange → AvailableTimeRange
                .map(tr -> AvailableTimeRange.of(
                        LocalTime.parse(tr.startTime()),
                        LocalTime.parse(tr.endTime())
                ))
                // 1-2) AvailableTimeRange list로 수집
                .collect(Collectors.toList());

        // 2. 날짜 범위 → DailySlotSchedule 변환
        // - startDate ~ endDate 모든 날짜에 동일한 timeRanges 적용
        List<DailySlotSchedule> schedules = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            // 2-1) 각 날짜마다 DailySlotSchedule 생성 (동일한 시간대)
            schedules.add(new DailySlotSchedule(currentDate, timeRanges));

            // 2-2) 다음 날짜로 이동
            currentDate = currentDate.plusDays(1);
        }

        log.debug("DailySlotSchedule 생성 완료: {} 일치", schedules.size());

        return schedules;
    }
}