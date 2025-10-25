package timefit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotRepository;
import timefit.booking.service.validator.BookingSlotValidator;
import timefit.business.entity.Business;
import timefit.business.entity.DayOfWeek;
import timefit.business.entity.OperatingHours;
import timefit.business.repository.OperatingHoursRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;
import timefit.menu.entity.Menu;
import timefit.menu.service.validator.MenuValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * BookingSlot 생성/수정/삭제 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingSlotCommandService {

    private final BookingSlotRepository bookingSlotRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final BusinessValidator businessValidator;
    private final MenuValidator menuValidator;
    private final BookingSlotValidator bookingSlotValidator;

    /**
     * 슬롯 생성 (허용된 시간만 받아서 처리 + OperatingHours 검증)
     */
    public BookingSlotResponse.SlotCreationResult createSlots(
            UUID businessId,
            BookingSlotRequest.Create request,
            UUID currentUserId) {

        log.info("슬롯 생성 시작: businessId={}, menuId={}", businessId, request.getMenuId());

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. 메뉴 검증
        Menu menu = menuValidator.validateMenuOfBusiness(request.getMenuId(), businessId);

        // 3. RESERVATION_BASED 확인
        if (!menu.isReservationBased()) {
            throw new BookingException(BookingErrorCode.SLOT_INVALID_MENU_TYPE);
        }

        // 4. 슬롯 생성
        List<BookingSlot> createdSlots = new ArrayList<>();
        int totalRequested = 0;

        for (BookingSlotRequest.DailyTimeSlot dailySlot : request.getDailyTimeSlots()) {
            LocalDate date = dailySlot.getDate();

            // 과거 날짜 체크
            if (date.isBefore(LocalDate.now())) {
                log.warn("과거 날짜 건너뜀: {}", date);
                continue;
            }

            // java.time.DayOfWeek → 커스텀 DayOfWeek enum 변환
            java.time.DayOfWeek standardDay = date.getDayOfWeek();
            int dayValue = standardDay.getValue() % 7; // 월(1)~일(7) → 일(0)~토(6)
            DayOfWeek customDayOfWeek = DayOfWeek.fromValue(dayValue);

            // OperatingHours 조회
            Optional<OperatingHours> operatingHoursOpt =
                    operatingHoursRepository.findByBusinessIdAndDayOfWeek(businessId, customDayOfWeek);

            // 휴무일 체크
            if (operatingHoursOpt.isEmpty()) {
                log.warn("영업시간 미설정 건너뜀: date={}, dayOfWeek={}", date, customDayOfWeek);
                continue;
            }

            OperatingHours operatingHours = operatingHoursOpt.get();
            if (operatingHours.getIsClosed()) {
                log.warn("휴무일 건너뜀: date={}", date);
                continue;
            }

            // 각 시간대별로 슬롯 생성
            for (BookingSlotRequest.TimeRange timeRange : dailySlot.getTimeRanges()) {
                totalRequested += createSlotsForTimeRange(
                        business, menu, date, timeRange,
                        request.getSlotInterval(), operatingHours, createdSlots
                );
            }
        }

        // 5. 일괄 저장
        List<BookingSlot> savedSlots = bookingSlotRepository.saveAll(createdSlots);

        log.info("슬롯 생성 완료: 요청={}, 생성={}", totalRequested, savedSlots.size());

        return BookingSlotResponse.SlotCreationResult.of(totalRequested, savedSlots);
    }

    /**
     * 특정 시간대에 대해 슬롯 생성
     */
    private int createSlotsForTimeRange(
            Business business, Menu menu, LocalDate date,
            BookingSlotRequest.TimeRange timeRange, Integer interval,
            OperatingHours operatingHours, List<BookingSlot> result) {

        int count = 0;
        LocalTime current = timeRange.getStartTime();

        while (current.plusMinutes(interval).isBefore(timeRange.getEndTime())
                || current.plusMinutes(interval).equals(timeRange.getEndTime())) {

            count++;
            LocalTime endTime = current.plusMinutes(interval);

            // OperatingHours 검증 (인위적 조작 방지)
            if (!isWithinOperatingHours(current, endTime, operatingHours)) {
                log.warn("영업시간 외 슬롯 거부: date={}, time={}", date, current);
                current = endTime;
                continue;
            }

            // 중복 체크
            if (bookingSlotRepository.existsByBusinessIdAndSlotDateAndStartTime(
                    business.getId(), date, current)) {
                log.debug("중복 슬롯 건너뜀: date={}, time={}", date, current);
                current = endTime;
                continue;
            }

            // 슬롯 생성
            try {
                BookingSlot slot = BookingSlot.create(
                        business, menu, date, current, endTime
                );
                result.add(slot);
                log.debug("슬롯 생성: date={}, time={}-{}", date, current, endTime);
            } catch (Exception e) {
                log.error("슬롯 생성 실패: date={}, time={}, error={}",
                        date, current, e.getMessage());
            }

            current = endTime;
        }

        return count;
    }

    /**
     * OperatingHours 내에 포함되는지 검증
     */
    private boolean isWithinOperatingHours(
            LocalTime slotStart, LocalTime slotEnd,
            OperatingHours operatingHours) {

        if (operatingHours.getIsClosed()) {
            return false;
        }

        // 슬롯이 영업시간 범위 내에 완전히 포함되는지 확인
        return !slotStart.isBefore(operatingHours.getOpenTime()) &&
                !slotEnd.isAfter(operatingHours.getCloseTime());
    }

    /**
     * 슬롯 삭제
     */
    public void deleteSlot(UUID businessId, UUID slotId, UUID currentUserId) {
        log.info("슬롯 삭제: businessId={}, slotId={}", businessId, slotId);

        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        BookingSlot slot = bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);

        bookingSlotRepository.delete(slot);
    }

    /**
     * 슬롯 비활성화
     */
    public BookingSlotResponse.SlotDetail deactivateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        BookingSlot slot = bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);

        slot.markAsUnavailable();
        BookingSlot updated = bookingSlotRepository.save(slot);

        return BookingSlotResponse.SlotDetail.of(updated, 0);
    }

    /**
     * 슬롯 재활성화
     */
    public BookingSlotResponse.SlotDetail activateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        BookingSlot slot = bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);

        slot.markAsAvailable();
        BookingSlot updated = bookingSlotRepository.save(slot);

        return BookingSlotResponse.SlotDetail.of(updated, 0);
    }

    /**
     * 과거 슬롯 일괄 삭제
     */
    public Integer deletePastSlots(UUID businessId, UUID currentUserId) {
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        List<BookingSlot> pastSlots = bookingSlotRepository
                .findBySlotDateBefore(LocalDate.now())
                .stream()
                .filter(slot -> slot.getBusiness().getId().equals(businessId))
                .toList();

        bookingSlotRepository.deleteAll(pastSlots);

        return pastSlots.size();
    }
}