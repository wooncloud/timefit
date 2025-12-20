package timefit.booking.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotQueryRepository;
import timefit.booking.repository.BookingSlotRepository;
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * BookingSlot 검증 담당
 * - BookingSlot 존재 및 상태 검증
 * - 시간 범위 검증
 * - Menu에서 BookingSlot 생성 시 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingSlotValidator {

    private final BookingSlotRepository bookingSlotRepository;
    private final BookingSlotQueryRepository bookingSlotQueryRepository;

    /**
     * BookingSlot 존재 여부 검증 및 조회
     *
     * @param slotId 검증할 슬롯 ID
     * @return 조회된 BookingSlot 엔티티
     * @throws BookingException 슬롯이 존재하지 않을 경우
     */
    public BookingSlot validateSlotExists(UUID slotId) {
        return bookingSlotRepository.findById(slotId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 슬롯 ID: {}", slotId);
                    return new BookingException(BookingErrorCode.AVAILABLE_SLOT_NOT_FOUND);
                });
    }

    /**
     * BookingSlot이 특정 Business에 속하는지 검증
     *
     * @param slot 검증할 BookingSlot 엔티티
     * @param businessId 업체 ID
     * @throws BookingException 슬롯이 해당 Business에 속하지 않을 경우
     */
    public void validateSlotBelongsToBusiness(BookingSlot slot, UUID businessId) {
        if (!slot.getBusiness().getId().equals(businessId)) {
            log.warn("슬롯이 해당 업체에 속하지 않음: slotId={}, businessId={}",
                    slot.getId(), businessId);
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_NOT_FOUND);
        }
    }

    /**
     * BookingSlot이 예약 가능한 상태인지 검증
     *
     * @param slot 검증할 BookingSlot 엔티티
     * @throws BookingException 예약 불가능한 상태일 경우
     */
    public void validateSlotAvailable(BookingSlot slot) {
        if (!slot.getIsAvailable()) {
            log.warn("예약 불가능한 슬롯: slotId={}", slot.getId());
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_NOT_AVAILABLE);
        }
    }

    /**
     * BookingSlot이 과거 날짜가 아닌지 검증
     *
     * @param slot 검증할 BookingSlot 엔티티
     * @throws BookingException 과거 날짜 슬롯일 경우
     */
    public void validateSlotNotPast(BookingSlot slot) {
        if (slot.getSlotDate().isBefore(LocalDate.now())) {
            log.warn("과거 날짜의 슬롯: slotId={}, date={}", slot.getId(), slot.getSlotDate());
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_PAST_DATE);
        }
    }

    /**
     * BookingSlot의 예약 가능 상태 검증
     *
     * @param slot 검증할 BookingSlot 엔티티
     * @throws BookingException 비활성 슬롯일 경우
     */
    public void validateSlotIsActive(BookingSlot slot) {
        if (!slot.isActiveForReservation()) {
            log.warn("비활성 슬롯으로 예약 불가: slotId={}", slot.getId());
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_CAPACITY_EXCEEDED);
        }
    }

    /**
     * BookingSlot 시간 형식이 유효한지 검증
     *
     * @param slot 검증할 BookingSlot 엔티티
     * @throws BookingException 시간 형식이 올바르지 않을 경우
     */
    public void validateSlotTimeFormat(BookingSlot slot) {
        if (!slot.hasValidTime()) {
            log.warn("유효하지 않은 슬롯 시간: slotId={}", slot.getId());
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_TIME_FORMAT_INVALID);
        }
    }

    /**
     * BookingSlot 존재 및 Business 소속 동시 검증
     *
     * @param slotId 검증할 슬롯 ID
     * @param businessId 업체 ID
     * @return 조회된 BookingSlot 엔티티
     */
    public BookingSlot validateSlotOfBusiness(UUID slotId, UUID businessId) {
        BookingSlot slot = validateSlotExists(slotId);
        validateSlotBelongsToBusiness(slot, businessId);
        return slot;
    }

    /**
     * 예약 가능한 슬롯인지 전체 검증
     *
     * @param slotId 검증할 슬롯 ID
     * @param businessId 업체 ID
     */
    public void validateBookableSlot(UUID slotId, UUID businessId) {
        BookingSlot slot = validateSlotOfBusiness(slotId, businessId);
        validateSlotAvailable(slot);
        validateSlotNotPast(slot);
        validateSlotIsActive(slot);
        validateSlotTimeFormat(slot);
    }

    /**
     * BookingSlot 생성 설정 검증
     *
     * [검증 항목]
     * 1. settings null 체크
     * 2. startDate, endDate 필수 값 체크
     * 3. 날짜 순서 검증 (startDate <= endDate)
     * 4. 최대 기간 검증 (3개월)
     * 5. 특정 시간대 검증 (선택사항)
     *
     * @param settings BookingSlot 생성 설정
     * @throws BookingException 검증 실패 시
     */
    public void validateSlotSettings(MenuRequestDto.BookingSlotSettings settings) {
        // 0. settings null 체크
        if (settings == null) {
            throw new BookingException(
                    BookingErrorCode.AVAILABLE_SLOT_INCOMPLETE_SETTINGS);
        }

        // 1. startDate, endDate 필수 값 체크
        LocalDate startDate = settings.startDate();
        LocalDate endDate = settings.endDate();

        if (startDate == null || endDate == null) {
            throw new BookingException(
                    BookingErrorCode.AVAILABLE_SLOT_INCOMPLETE_SETTINGS);
        }

        // 2. 날짜 순서 검증
        if (startDate.isAfter(endDate)) {
            throw new BookingException(
                    BookingErrorCode.AVAILABLE_SLOT_INVALID_TIME);
        }

        // 3. 최대 기간 검증 (3개월)
        if (startDate.plusMonths(3).isBefore(endDate)) {
            throw new BookingException(
                    BookingErrorCode.AVAILABLE_SLOT_DATE_RANGE_EXCEEDED);
        }

        // 4. 특정 시간대 검증 (선택사항)
        if (settings.specificTimeRanges() != null && !settings.specificTimeRanges().isEmpty()) {
            for (MenuRequestDto.TimeRange timeRange : settings.specificTimeRanges()) {
                validateTimeRange(timeRange);
            }
        }
    }

    /**
     * TimeRange 검증
     *
     * [검증 항목]
     * 1. 시간 형식 검증 (HH:mm)
     * 2. startTime < endTime 검증
     *
     * @param timeRange 검증할 시간대
     * @throws BookingException 검증 실패 시
     */
    public void validateTimeRange(MenuRequestDto.TimeRange timeRange) {
        try {
            LocalTime start = LocalTime.parse(timeRange.startTime());
            LocalTime end = LocalTime.parse(timeRange.endTime());

            if (!start.isBefore(end)) {
                throw new BookingException(
                        BookingErrorCode.AVAILABLE_SLOT_INVALID_TIME,
                        String.format("시작 시간(%s)은 종료 시간(%s)보다 이전이어야 합니다",
                                timeRange.startTime(), timeRange.endTime())
                );
            }
        } catch (DateTimeParseException e) {
            throw new BookingException(
                    BookingErrorCode.AVAILABLE_SLOT_TIME_FORMAT_INVALID,
                    String.format("잘못된 시간 형식입니다. HH:mm 형식을 사용하세요 (예: 09:00, 18:30). " +
                                    "입력값: startTime=%s, endTime=%s",
                            timeRange.startTime(), timeRange.endTime())
            );
        }
    }

    /**
     * BookingSlot에 활성 예약이 존재하지 않는지 검증
     * - 슬롯 삭제/비활성화 전에 호출
     * - CANCELLED, NO_SHOW를 제외한 예약이 있으면 예외 발생
     *
     * @param slotId 검증할 슬롯 ID
     * @throws BookingException 활성 예약이 존재할 경우
     */
    public void validateNoActiveReservations(UUID slotId) {
        Integer activeReservations = bookingSlotQueryRepository
                .countActiveReservationsBySlot(slotId);

        if (activeReservations > 0) {
            log.warn("슬롯에 활성 예약 존재: slotId={}, activeReservations={}",
                    slotId, activeReservations);
            throw new BookingException(
                    BookingErrorCode.AVAILABLE_SLOT_NOT_MODIFIABLE,
                    String.format("이 슬롯에 %d개의 활성 예약이 존재하여 삭제/비활성화할 수 없습니다",
                            activeReservations)
            );
        }
    }

    // ===== Menu에서 BookingSlot 생성 시 검증 =====

    /**
     * Menu에서 BookingSlot 생성 가능 여부 검증
     *
     * [검증 흐름]
     * 1. ONDEMAND_BASED → 생성 불필요 (정상 종료)
     * 2. autoGenerateSlots=false → 생성 불필요 (정상 종료)
     * 3. slotSettings=null → 예외 발생 (로직 오류)
     * 4. slotSettings 유효성 검증
     *
     * [호출 시점]
     * - BookingSlotCommandService.createFromMenu()
     * - BookingSlotCommandService.regenerateFromMenu()
     *
     * @param menu 생성/수정된 Menu
     * @param request Menu 생성/수정 요청 DTO
     * @throws MenuException 생성 조건이 올바르지 않은 경우
     */
    public void validateCreationFromMenu(Menu menu, MenuRequestDto.CreateUpdateMenu request) {
        // 1. ONDEMAND_BASED 체크 (정상 종료)
        if (request.orderType() != OrderType.RESERVATION_BASED) {
            log.debug("ONDEMAND_BASED 메뉴 - BookingSlot 생성 불필요: menuId={}", menu.getId());
            return;
        }

        // 2. autoGenerateSlots 체크 (정상 종료)
        if (!Boolean.TRUE.equals(request.autoGenerateSlots())) {
            log.debug("autoGenerateSlots=false - BookingSlot 생성 생략: menuId={}", menu.getId());
            return;
        }

        // 3. slotSettings 필수 체크 (예외 발생)
        if (request.slotSettings() == null) {
            log.error("slotSettings 누락 - BookingSlot 생성 불가: menuId={}", menu.getId());
            throw new MenuException(MenuErrorCode.INVALID_SLOT_SETTINGS);
        }

        // 4. slotSettings 유효성 검증
        validateSlotSettings(request.slotSettings());
    }

    /**
     * BookingSlot 재생성 필요 여부 확인
     *
     * [확인 항목]
     * - request.durationMinutes가 null이 아니고
     * - oldDurationMinutes와 다른 경우
     *
     * @param request Menu 수정 요청 DTO
     * @param oldDurationMinutes 수정 전 durationMinutes
     * @return true: 재생성 필요, false: 재생성 불필요
     */
    public boolean shouldRegenerate(
            MenuRequestDto.CreateUpdateMenu request,
            Integer oldDurationMinutes) {

        // durationMinutes가 변경되지 않았으면 재생성 불필요
        if (request.durationMinutes() == null) {
            return false;
        }

        // durationMinutes가 변경되었으면 재생성 필요
        return !oldDurationMinutes.equals(request.durationMinutes());
    }
}