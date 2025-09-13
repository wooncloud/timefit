package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.common.ResponseData;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.repository.ReservationRepositoryCustom;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 예약 캘린더 조회 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationCalendarService {

    private final BusinessRepository businessRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final ReservationRepositoryCustom reservationRepositoryCustom;

    /**
     * 예약 캘린더 조회
     */
    public ResponseData<ReservationResponseDto.ReservationCalendarResult> getReservationCalendar(
            UUID businessId, UUID currentUserId, LocalDate startDate, LocalDate endDate) {

        log.info("예약 캘린더 조회 시작: businessId={}, userId={}, period={}~{}",
                businessId, currentUserId, startDate, endDate);

        // 1. 업체 존재 및 권한 확인
        Business business = validateBusinessExists(businessId);
        validateUserBusinessAccess(currentUserId, businessId);

        // 2. 날짜 범위 검증
        validateDateRange(startDate, endDate);

        // 3. 해당 기간의 예약 목록 조회
        List<Reservation> reservations = reservationRepositoryCustom
                .findReservationsByBusinessAndDateRange(businessId, startDate, endDate);

        // 4. 날짜별로 그룹핑
        Map<LocalDate, List<Reservation>> reservationsByDate = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getReservationDate));

        // 5. 캘린더 날짜 정보 생성
        List<ReservationResponseDto.CalendarDayInfo> calendarDays =
                createCalendarDays(startDate, endDate, reservationsByDate);

        // 6. 응답 생성
        ReservationResponseDto.BusinessInfo businessInfo = ReservationResponseDto.BusinessInfo.of(
                business.getId(),
                business.getBusinessName(),
                business.getAddress(),
                business.getContactPhone()
        );

        ReservationResponseDto.CalendarInfo calendarInfo = ReservationResponseDto.CalendarInfo.of(
                startDate, endDate, calendarDays);

        ReservationResponseDto.ReservationCalendarResult result =
                ReservationResponseDto.ReservationCalendarResult.of(businessInfo, calendarInfo);

        log.info("예약 캘린더 조회 완료: businessId={}, 조회기간={}일, 총예약={}건",
                businessId, ChronoUnit.DAYS.between(startDate, endDate) + 1, reservations.size());

        return ResponseData.of(result);
    }

    // === Private Helper Methods ===

    /**
     * 캘린더 날짜 정보 생성
     */
    private List<ReservationResponseDto.CalendarDayInfo> createCalendarDays(
            LocalDate startDate, LocalDate endDate,
            Map<LocalDate, List<Reservation>> reservationsByDate) {

        List<ReservationResponseDto.CalendarDayInfo> calendarDays = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            List<Reservation> dayReservations = reservationsByDate.getOrDefault(currentDate, List.of());

            // 해당 날짜의 예약 항목 생성
            List<ReservationResponseDto.CalendarReservationItem> reservationItems =
                    dayReservations.stream()
                            .map(this::createCalendarReservationItem)
                            .sorted((r1, r2) -> r1.getTime().compareTo(r2.getTime())) // 시간순 정렬
                            .collect(Collectors.toList());

            // 일일 통계 생성
            ReservationResponseDto.DailyStats dailyStats = createDailyStats(dayReservations);

            // 요일 정보 생성
            String dayOfWeek = getDayOfWeekName(currentDate.getDayOfWeek());

            // 캘린더 날짜 정보 생성
            ReservationResponseDto.CalendarDayInfo dayInfo = ReservationResponseDto.CalendarDayInfo.of(
                    currentDate, dayOfWeek, reservationItems, dailyStats);

            calendarDays.add(dayInfo);
            currentDate = currentDate.plusDays(1);
        }

        return calendarDays;
    }

    /**
     * 캘린더용 예약 항목 생성
     */
    private ReservationResponseDto.CalendarReservationItem createCalendarReservationItem(Reservation reservation) {
        // 선택된 옵션 정보 생성 (향후 확장용)
        List<ReservationResponseDto.SelectedOptionInfo> selectedOptions = List.of();
        // TODO: 실제 선택된 옵션 정보를 reservation에서 조회하여 변환

        return ReservationResponseDto.CalendarReservationItem.of(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getReservationTime(),
                reservation.getDurationMinutes(),
                reservation.getStatus(),
                reservation.getCustomerName(),
                reservation.getCustomerPhone(),
                selectedOptions,
                reservation.getTotalPrice(),
                reservation.getNotes()
        );
    }

    /**
     * 일일 통계 생성
     */
    private ReservationResponseDto.DailyStats createDailyStats(List<Reservation> dayReservations) {
        int totalReservations = dayReservations.size();

        // 완료된 예약의 매출만 집계
        Integer totalRevenue = dayReservations.stream()
                .filter(r -> r.getStatus() == timefit.reservation.entity.ReservationStatus.COMPLETED)
                .mapToInt(r -> r.getTotalPrice() != null ? r.getTotalPrice() : 0)
                .sum();

        return ReservationResponseDto.DailyStats.of(totalReservations, totalRevenue);
    }

    /**
     * 요일명 반환
     */
    private String getDayOfWeekName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "MONDAY";
            case TUESDAY: return "TUESDAY";
            case WEDNESDAY: return "WEDNESDAY";
            case THURSDAY: return "THURSDAY";
            case FRIDAY: return "FRIDAY";
            case SATURDAY: return "SATURDAY";
            case SUNDAY: return "SUNDAY";
            default: return "UNKNOWN";
        }
    }

    /**
     * 날짜 범위 검증
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ReservationException(ReservationErrorCode.INVALID_DATE_FORMAT);
        }

        if (startDate.isAfter(endDate)) {
            throw new ReservationException(ReservationErrorCode.INVALID_DATE_FORMAT);
        }

        // 너무 긴 기간 조회 방지 (최대 1년)
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 365) {
            throw new ReservationException(ReservationErrorCode.INVALID_DATE_FORMAT);
        }
    }

    /**
     * 업체 존재 여부 확인
     */
    private Business validateBusinessExists(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    /**
     * 사용자가 해당 업체에 속하는지 확인
     */
    private UserBusinessRole validateUserBusinessAccess(UUID userId, UUID businessId) {
        return userBusinessRoleRepository.findByUserIdAndBusinessIdAndIsActive(userId, businessId, true)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_BUSINESS_MEMBER));
    }
}