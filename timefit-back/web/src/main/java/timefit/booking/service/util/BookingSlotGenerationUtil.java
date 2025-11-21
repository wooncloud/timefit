package timefit.booking.service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.entity.BookingSlot;
import timefit.booking.service.dto.AvailableTimeRange;
import timefit.booking.service.dto.DailySlotSchedule;
import timefit.business.entity.Business;
import timefit.business.entity.OperatingHours;
import timefit.menu.entity.Menu;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingSlot 생성 로직 유틸리티
 * - 영업시간 기반 슬롯 생성
 * - 시간 범위 검증
 * - 슬롯 간격 계산
 */
@Slf4j
@Component
public class BookingSlotGenerationUtil {

    /**
     * 일별 스케줄 기반으로 슬롯 생성
     *
     * @param business 업체
     * @param menu 메뉴
     * @param date 날짜
     * @param operatingHoursList 해당 날짜의 영업시간 목록
     * @param timeRanges 사용자 지정 시간대 (비어있으면 영업시간 전체 사용)
     * @param slotIntervalMinutes 슬롯 간격 (분)
     * @return 생성된 슬롯 목록
     */
    public List<BookingSlot> generateSlotsForDay(
            Business business,
            Menu menu,
            LocalDate date,
            List<OperatingHours> operatingHoursList,
            List<AvailableTimeRange> timeRanges,
            Integer slotIntervalMinutes) {

        List<BookingSlot> slots = new ArrayList<>();

        // 사용자 지정 시간대가 있으면 해당 시간대만 사용
        if (timeRanges != null && !timeRanges.isEmpty()) {
            for (AvailableTimeRange timeRange : timeRanges) {
                // 지정된 시간대가 영업시간 내에 있는지 검증
                if (isWithinAnyOperatingHours(timeRange, operatingHoursList)) {
                    slots.addAll(createSlotsInTimeRange(
                            business, menu, date, timeRange, slotIntervalMinutes));
                }
            }
        } else {
            // 사용자 지정 시간대가 없으면 영업시간 전체 사용
            for (OperatingHours operatingHours : operatingHoursList) {
                AvailableTimeRange timeRange = AvailableTimeRange.of(
                        operatingHours.getOpenTime(),
                        operatingHours.getCloseTime()
                );
                slots.addAll(createSlotsInTimeRange(
                        business, menu, date, timeRange, slotIntervalMinutes));
            }
        }

        return slots;
    }

    // 특정 시간대 내에서 슬롯 생성
    private List<BookingSlot> createSlotsInTimeRange(
            Business business,
            Menu menu,
            LocalDate date,
            AvailableTimeRange timeRange,
            Integer slotIntervalMinutes) {

        List<BookingSlot> slots = new ArrayList<>();
        LocalTime startTime = timeRange.startTime();
        LocalTime endTime = timeRange.endTime();
        Integer durationMinutes = menu.getDurationMinutes();

        // 슬롯 생성 루프
        LocalTime currentTime = startTime;
        while (currentTime.plusMinutes(durationMinutes).isBefore(endTime)
                || currentTime.plusMinutes(durationMinutes).equals(endTime)) {

            LocalTime slotEndTime = currentTime.plusMinutes(durationMinutes);

            // BookingSlot 엔티티 생성
            BookingSlot slot = BookingSlot.create(
                    business,
                    menu,
                    date,
                    currentTime,
                    slotEndTime
            );

            slots.add(slot);

            // 다음 슬롯 시작 시간 계산
            currentTime = currentTime.plusMinutes(slotIntervalMinutes);
        }

        return slots;
    }

    /**
     * 시간대가 영업시간 내에 있는지 검증
     * - 여러 영업시간대 중 하나라도 포함되면 true
     */
    public boolean isWithinAnyOperatingHours(
            AvailableTimeRange timeRange,
            List<OperatingHours> operatingHoursList) {

        for (OperatingHours operatingHours : operatingHoursList) {
            if (isTimeRangeWithinOperatingHours(timeRange, operatingHours)) {
                return true;
            }
        }
        return false;
    }

    // 시간대가 특정 영업시간 내에 있는지 검증
    private boolean isTimeRangeWithinOperatingHours(
            AvailableTimeRange timeRange,
            OperatingHours operatingHours) {

        LocalTime rangeStart = timeRange.startTime();
        LocalTime rangeEnd = timeRange.endTime();
        LocalTime opStart = operatingHours.getOpenTime();
        LocalTime opEnd = operatingHours.getCloseTime();

        // 시간대가 영업시간 내에 완전히 포함되는지 확인
        return !rangeStart.isBefore(opStart)
                && !rangeEnd.isAfter(opEnd)
                && rangeStart.isBefore(rangeEnd);
    }

    /**
     * DailySlotSchedule에서 사용 가능한 시간대 추출
     * - 비어있으면 null 반환 (영업시간 전체 사용)
     */
    public List<AvailableTimeRange> extractTimeRanges(DailySlotSchedule schedule) {
        List<AvailableTimeRange> timeRanges = schedule.timeRanges();
        return (timeRanges == null || timeRanges.isEmpty()) ? null : timeRanges;
    }

    // 총 요청된 슬롯 수 계산
    public int calculateTotalRequestedSlots(
            List<DailySlotSchedule> schedules,
            Integer slotIntervalMinutes,
            Integer durationMinutes) {

        int total = 0;
        for (DailySlotSchedule schedule : schedules) {
            List<AvailableTimeRange> timeRanges = schedule.timeRanges();
            if (timeRanges != null && !timeRanges.isEmpty()) {
                for (AvailableTimeRange range : timeRanges) {
                    total += calculateSlotsInRange(range, slotIntervalMinutes, durationMinutes);
                }
            } else {
                // 시간대가 비어있으면 대략적인 계산 (영업시간 가정: 9시간)
                total += (9 * 60) / slotIntervalMinutes;
            }
        }
        return total;
    }

    // 특정 시간대에서 생성 가능한 슬롯 수 계산
    private int calculateSlotsInRange(
            AvailableTimeRange range,
            Integer slotIntervalMinutes,
            Integer durationMinutes) {

        LocalTime start = range.startTime();
        LocalTime end = range.endTime();

        int totalMinutes = (int) java.time.Duration.between(start, end).toMinutes();
        int availableMinutes = totalMinutes - durationMinutes;

        if (availableMinutes < 0) {
            return 0;
        }

        return (availableMinutes / slotIntervalMinutes) + 1;
    }
}