package timefit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotRepository;
import timefit.booking.service.dto.AvailableTimeRange;
import timefit.booking.service.dto.DailySlotSchedule;
import timefit.booking.service.util.BookingSlotGenerationUtil;
import timefit.booking.service.validator.BookingSlotValidator;
import timefit.business.entity.Business;
import timefit.business.entity.OperatingHours;
import timefit.business.repository.OperatingHoursRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.common.entity.DayOfWeek;
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;
import timefit.menu.entity.Menu;
import timefit.menu.service.validator.MenuValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final BookingSlotGenerationUtil slotGenerationUtil;

    /**
     * 슬롯 생성
     * - 허용된 시간대만 받아서 처리 + OperatingHours 검증
     * - 여러 영업시간대 처리
     */
    public BookingSlotResponse.CreationResult createSlots(
            UUID businessId,
            BookingSlotRequest.BookingSlot request,
            UUID currentUserId) {

        log.info("슬롯 생성 시작: businessId={}, menuId={}", businessId, request.menuId());

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. 메뉴 검증
        Menu menu = menuValidator.validateMenuOfBusiness(request.menuId(), businessId);

        // 3. 메뉴 타입 검증 (RESERVATION_BASED만 가능)
        if (!menu.isReservationBased()) {
            throw new BookingException(BookingErrorCode.SLOT_INVALID_MENU_TYPE);
        }

        // 4. 슬롯 생성
        int totalRequested = 0;
        List<BookingSlot> createdSlots = new ArrayList<>();

        for (DailySlotSchedule schedule : request.schedules()) {
            LocalDate date = schedule.date();
            int dayOfWeekValue = date.getDayOfWeek().getValue();
            DayOfWeek dayOfWeek = DayOfWeek.fromValue(dayOfWeekValue);

            // 영업시간 조회 (sequence 순서대로)
            List<OperatingHours> operatingHoursList = operatingHoursRepository
                    .findByBusinessIdAndDayOfWeekOrderBySequenceAsc(business.getId(), dayOfWeek);

            if (operatingHoursList.isEmpty()) {
                log.warn("영업시간 미설정: businessId={}, date={}", businessId, date);
                continue;
            }

            // 휴무일이 아닌 영업시간만 필터링
            List<OperatingHours> activeOperatingHours = operatingHoursList.stream()
                    .filter(oh -> !oh.getIsClosed())
                    .collect(Collectors.toList());

            if (activeOperatingHours.isEmpty()) {
                log.info("휴무일: businessId={}, date={}", businessId, date);
                continue;
            }

            // 시간대 추출
            List<AvailableTimeRange> timeRanges = slotGenerationUtil.extractTimeRanges(schedule);

            // 슬롯 생성
            List<BookingSlot> dailySlots = slotGenerationUtil.generateSlotsForDay(
                    business, menu, date,
                    activeOperatingHours, timeRanges,
                    request.slotIntervalMinutes()
            );

            // 중복 체크 및 저장
            for (BookingSlot slot : dailySlots) {
                totalRequested++;

                // 중복 체크
                boolean exists = bookingSlotRepository.existsByBusinessIdAndSlotDateAndStartTime(
                        business.getId(), slot.getSlotDate(), slot.getStartTime()
                );

                if (!exists) {
                    createdSlots.add(slot);
                } else {
                    log.debug("중복 슬롯 건너뜀: date={}, startTime={}",
                            slot.getSlotDate(), slot.getStartTime());
                }
            }
        }

        // 5. 일괄 저장
        if (!createdSlots.isEmpty()) {
            bookingSlotRepository.saveAll(createdSlots);
        }

        log.info("슬롯 생성 완료: businessId={}, totalRequested={}, created={}, skipped={}",
                businessId, totalRequested, createdSlots.size(), totalRequested - createdSlots.size());

        return new BookingSlotResponse.CreationResult(
                totalRequested,
                createdSlots.size(),
                totalRequested - createdSlots.size()
        );
    }

    /**
     * 슬롯 삭제
     * - 활성 예약이 있으면 삭제 불가
     */
    public void deleteSlot(UUID businessId, UUID slotId, UUID currentUserId) {
        log.info("슬롯 삭제 시작: slotId={}", slotId);

        BookingSlot slot = validateAndGetSlot(businessId, slotId, currentUserId);
        bookingSlotValidator.validateNoActiveReservations(slotId);

        bookingSlotRepository.delete(slot);

        log.info("슬롯 삭제 완료: slotId={}", slotId);
    }

    /**
     * 슬롯 비활성화
     * - 활성 예약이 있어도 비활성화 가능 (단, 새 예약 불가)
     */
    public BookingSlotResponse.BookingSlot deactivateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        log.info("슬롯 비활성화 시작: slotId={}", slotId);

        BookingSlot slot = validateAndGetSlot(businessId, slotId, currentUserId);
        slot.markAsUnavailable();

        return saveAndConvertToResponse(slot, "비활성화");
    }

    // 슬롯 재활성화
    public BookingSlotResponse.BookingSlot activateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        log.info("슬롯 재활성화 시작: slotId={}", slotId);

        BookingSlot slot = validateAndGetSlot(businessId, slotId, currentUserId);
        slot.markAsAvailable();

        return saveAndConvertToResponse(slot, "재활성화");
    }

    // 과거 슬롯 일괄 삭제
    public Integer deletePastSlots(UUID businessId, UUID currentUserId) {
        log.info("과거 슬롯 일괄 삭제 시작: businessId={}", businessId);

        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        LocalDate today = LocalDate.now();
        List<BookingSlot> pastSlots = bookingSlotRepository
                .findByBusinessIdAndSlotDateBefore(businessId, today);

        int count = pastSlots.size();
        bookingSlotRepository.deleteAll(pastSlots);

        log.info("과거 슬롯 삭제 완료: businessId={}, count={}", businessId, count);

        return count;
    }

    // ------------------------------- Private

    /**
     * 권한 검증 및 슬롯 조회
     * - 삭제/활성화/비활성화 메서드에서 공통으로 사용
     */
    private BookingSlot validateAndGetSlot(UUID businessId, UUID slotId, UUID currentUserId) {
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        return bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);
    }

    /**
     * 슬롯 저장 및 응답 DTO 변환
     * - 활성화/비활성화 메서드에서 공통으로 사용
     */
    private BookingSlotResponse.BookingSlot saveAndConvertToResponse(BookingSlot slot, String action) {
        BookingSlot updated = bookingSlotRepository.save(slot);
        log.info("슬롯 {} 완료: slotId={}", action, slot.getId());
        return BookingSlotResponse.BookingSlot.of(updated);
    }
}