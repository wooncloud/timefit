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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BookingSlot 생성/수정/삭제 전담 서비스
 * 1. 여러 영업시간대 지원 (휴게시간 처리)
 * 2. sequence 순서대로 정확한 검증
 * 3. isWithinAnyOperatingHours() 메서드 추가
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
     * 여러 영업시간대 처리하도록 수정됨.
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

            // 해당 요일의 모든 영업시간을 sequence 순서로 조회
            List<OperatingHours> operatingHoursList =
                    operatingHoursRepository.findByBusinessIdAndDayOfWeekOrderBySequenceAsc(
                            businessId,
                            customDayOfWeek
                    );

            // 영업시간 없음 체크
            if (operatingHoursList.isEmpty()) {
                log.warn("영업시간 미설정 건너뜀: date={}, dayOfWeek={}", date, customDayOfWeek);
                continue;
            }

            // 휴무가 아닌 영업시간만 필터링
            List<OperatingHours> activeOperatingHours = operatingHoursList.stream()
                    .filter(oh -> !oh.getIsClosed())
                    .collect(Collectors.toList());

            // 전부 휴무일인 경우
            if (activeOperatingHours.isEmpty()) {
                log.warn("휴무일 건너뜀: date={}", date);
                continue;
            }

            // 로깅: 영업시간 확인
            log.debug("영업시간 조회 완료: date={}, count={}", date, activeOperatingHours.size());
            for (OperatingHours oh : activeOperatingHours) {
                log.debug("  - sequence={}, {}~{}",
                        oh.getSequence(), oh.getOpenTime(), oh.getCloseTime());
            }

            // 각 시간대별로 슬롯 생성
            for (BookingSlotRequest.TimeRange timeRange : dailySlot.getTimeRanges()) {
                totalRequested += createSlotsForTimeRange(
                        business, menu, date, timeRange,
                        request.getSlotInterval(),
                        activeOperatingHours,  // List 전달
                        createdSlots
                );
            }
        }

        // 5. 일괄 저장
        List<BookingSlot> savedSlots = bookingSlotRepository.saveAll(createdSlots);

        log.info("슬롯 생성 완료: 요청={}, 생성={}", totalRequested, savedSlots.size());

        return BookingSlotResponse.SlotCreationResult.of(totalRequested, savedSlots);
    }

    // 특정 시간대에 대해 슬롯 생성 (List<OperatingHours> 받아서 처리하도록 변경)
    private int createSlotsForTimeRange(
            Business business, Menu menu, LocalDate date,
            BookingSlotRequest.TimeRange timeRange, Integer interval,
            List<OperatingHours> operatingHoursList,
            List<BookingSlot> result) {

        int count = 0;
        LocalTime current = timeRange.getStartTime();

        while (current.plusMinutes(interval).isBefore(timeRange.getEndTime())
                || current.plusMinutes(interval).equals(timeRange.getEndTime())) {

            count++;
            LocalTime endTime = current.plusMinutes(interval);

            //  모든 영업시간대 중 하나라도 포함되는지 검증
            if (!isWithinAnyOperatingHours(current, endTime, operatingHoursList)) {
                log.warn("영업시간 외 슬롯 거부: date={}, time={}-{}",
                        date, current, endTime);
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
     * 여러 영업시간대 중 하나라도 포함되는지 검증
     * 휴게시간 처리 로직:
     * - 슬롯이 여러 영업시간대 중 하나에 완전히 포함되면 OK
     * - 어디에도 포함되지 않으면 거부 (휴게시간)
     * -
     * 예시:
     * 영업시간: 09:00-12:00, 13:00-18:00
     * - 11:00-12:00 → 09:00-12:00에 포함 ✅
     * - 12:00-13:00 → 어디에도 포함 안 됨 ❌ (휴게시간)
     * - 13:00-14:00 → 13:00-18:00에 포함 ✅
     */
    private boolean isWithinAnyOperatingHours(
            LocalTime slotStart,
            LocalTime slotEnd,
            List<OperatingHours> operatingHoursList) {

        // 모든 영업시간대를 순회하며 하나라도 포함되면 true
        for (OperatingHours oh : operatingHoursList) {
            // 휴무일은 건너뜀 (이미 필터링되었지만 안전장치)
            if (oh.getIsClosed()) {
                continue;
            }

            // 슬롯이 이 영업시간대에 완전히 포함되는가?
            boolean isWithin = !slotStart.isBefore(oh.getOpenTime()) &&
                    !slotEnd.isAfter(oh.getCloseTime());

            if (isWithin) {
                log.trace("슬롯 검증 성공: {}-{} in [{}~{}]",
                        slotStart, slotEnd, oh.getOpenTime(), oh.getCloseTime());
                return true;  // 하나라도 포함되면 OK
            }
        }

        // 실패케이스. 모든 영업시간대에 포함되지 않음 (휴게시간)
        log.trace("슬롯 검증 실패: {}-{} (휴게시간 또는 영업시간 외)",
                slotStart, slotEnd);
        return false;
    }

    // 슬롯 삭제
    public void deleteSlot(UUID businessId, UUID slotId, UUID currentUserId) {
        log.info("슬롯 삭제: businessId={}, slotId={}", businessId, slotId);

        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        BookingSlot slot = bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);

        bookingSlotRepository.delete(slot);
    }

    // 슬롯 비활성화
    public BookingSlotResponse.SlotDetail deactivateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        BookingSlot slot = bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);

        slot.markAsUnavailable();
        BookingSlot updated = bookingSlotRepository.save(slot);

        return BookingSlotResponse.SlotDetail.of(updated, 0);
    }

    // 슬롯 재활성화
    public BookingSlotResponse.SlotDetail activateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        BookingSlot slot = bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);

        slot.markAsAvailable();
        BookingSlot updated = bookingSlotRepository.save(slot);

        return BookingSlotResponse.SlotDetail.of(updated, 0);
    }

    // 과거 슬롯 일괄 삭제
    public Integer deletePastSlots(UUID businessId, UUID currentUserId) {
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        LocalDate today = LocalDate.now();
        List<BookingSlot> pastSlots = bookingSlotRepository
                .findByBusinessIdAndSlotDateBefore(businessId, today);

        int count = pastSlots.size();
        bookingSlotRepository.deleteAll(pastSlots);

        log.info("과거 슬롯 삭제 완료: businessId={}, count={}", businessId, count);
        return count;
    }
}