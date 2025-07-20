package timefit.schedule.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessOperatingHours;
import timefit.business.entity.DayOfWeek;
import timefit.schedule.dto.ScheduleResponseDto;
import timefit.reservation.entity.ReservationTimeSlot;
import timefit.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleResponseFactory {

    private final ReservationRepository reservationRepository;

    /**
     * 영업시간 설정 결과 응답 생성
     */
    public ScheduleResponseDto.OperatingHoursResult createOperatingHoursResponse(
            Business business, List<BusinessOperatingHours> hours) {

        List<ScheduleResponseDto.BusinessHourDetail> hourDetails = hours.stream()
                .map(this::createBusinessHourDetailResponse)
                .collect(Collectors.toList());

        return ScheduleResponseDto.OperatingHoursResult.of(
                business.getId(),
                business.getBusinessName(),
                hourDetails,
                java.time.LocalDateTime.now()
        );
    }

    /**
     * 개별 영업시간 상세 정보 응답 생성
     */
    private ScheduleResponseDto.BusinessHourDetail createBusinessHourDetailResponse(BusinessOperatingHours hour) {
        String dayName = getDayName(hour.getDayOfWeek().getValue());

        return ScheduleResponseDto.BusinessHourDetail.of(
                hour.getId(),
                hour.getDayOfWeek().getValue(),
                dayName,
                hour.getOpenTime(),
                hour.getCloseTime(),
                hour.getIsClosed(),
                hour.getCreatedAt(),
                hour.getUpdatedAt()
        );
    }

    /**
     * 요일 값을 한글 요일명으로 변환
     */
    private String getDayName(Integer dayOfWeekValue) {
        // 0=일요일, 1=월요일, ..., 6=토요일
        String[] dayNames = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

        if (dayOfWeekValue != null && dayOfWeekValue >= 0 && dayOfWeekValue < dayNames.length) {
            return dayNames[dayOfWeekValue];
        }
        return "알 수 없음";
    }


    /**
     * 슬롯 상세 정보 응답 생성
     */
    public ScheduleResponseDto.SlotDetail createSlotDetailResponse(ReservationTimeSlot slot) {
        Integer currentBookings = getCurrentBookingsCount(slot.getId());

        return ScheduleResponseDto.SlotDetail.of(
                slot.getId(),
                slot.getBusiness().getId(),
                slot.getSlotDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getCapacity(),
                currentBookings,
                slot.getIsAvailable(),
                slot.getCreatedAt(),
                slot.getUpdatedAt()
        );
    }

    /**
     * 슬롯 요약 정보 응답 생성
     */
    public ScheduleResponseDto.SlotSummary createSlotSummaryResponse(ReservationTimeSlot slot) {
        Integer currentBookings = getCurrentBookingsCount(slot.getId());

        return ScheduleResponseDto.SlotSummary.of(
                slot.getId(),
                slot.getSlotDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getCapacity(),
                currentBookings,
                slot.getIsAvailable()
        );
    }

    /**
     * 날짜별 슬롯 목록 응답 생성
     */
    public ScheduleResponseDto.DailySlotsResult createDailySlotsResponse(
            LocalDate date, List<ReservationTimeSlot> slots, Boolean isBusinessOpen) {

        List<ScheduleResponseDto.SlotSummary> slotSummaries = slots.stream()
                .map(this::createSlotSummaryResponse)
                .collect(Collectors.toList());

        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

        return ScheduleResponseDto.DailySlotsResult.of(date, dayOfWeek, isBusinessOpen, slotSummaries);
    }

    /**
     * 슬롯 생성 결과 응답 생성
     */
    public ScheduleResponseDto.SlotCreationResult createSlotCreationResult(
            Integer totalRequested, List<ReservationTimeSlot> createdSlots, List<String> errors) {

        List<ScheduleResponseDto.SlotDetail> slotDetails = createdSlots.stream()
                .map(this::createSlotDetailResponse)
                .collect(Collectors.toList());

        return ScheduleResponseDto.SlotCreationResult.of(
                totalRequested,
                createdSlots.size(),
                errors.size(),
                errors,
                slotDetails
        );
    }

    /**
     * 현재 예약 수 계산 (취소/노쇼 제외)
     */
    private Integer getCurrentBookingsCount(java.util.UUID slotId) {
        return Math.toIntExact(reservationRepository.countBySlotIdAndStatusNotIn(
                slotId,
                List.of(timefit.reservation.entity.ReservationStatus.CANCELLED,
                        timefit.reservation.entity.ReservationStatus.NO_SHOW)
        ));
    }
}