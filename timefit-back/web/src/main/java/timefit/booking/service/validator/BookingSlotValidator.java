package timefit.booking.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotQueryRepository;
import timefit.booking.repository.BookingSlotRepository;
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;
import timefit.menu.dto.MenuRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * BookingSlot 도메인 검증 클래스
 * - BookingSlot 고유의 검증 로직 분리
 * - 슬롯 유효성, 예약 가능 여부 등 검증
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
     * @param slotId     검증할 슬롯 ID
     * @param businessId 업체 ID
     */
    public void validateBookableSlot(UUID slotId, UUID businessId) {
        BookingSlot slot = validateSlotOfBusiness(slotId, businessId);
        validateSlotAvailable(slot);
        validateSlotNotPast(slot);
        validateSlotIsActive(slot);
        validateSlotTimeFormat(slot);
        // 일단 여기 내부에 다 throw 있어서 괜찮나? ㅋㅋ 하 씨
    }

    // ----- 메뉴 생성 시 사용되는 validator

    /**
     * BookingSlot 생성 설정 검증
     * - 날짜 범위 검증
     * - 시간대 검증
     *
     * @param settings BookingSlot 생성 설정
     * @throws BookingException 검증 실패 시
     */
    public void validateSlotSettings(MenuRequest.BookingSlotSettings settings) {
        LocalDate startDate = settings.startDate();
        LocalDate endDate = settings.endDate();

        // 1. 날짜 순서 검증
        if (startDate.isAfter(endDate)) {
            throw new BookingException(
                    BookingErrorCode.AVAILABLE_SLOT_INVALID_TIME,
                    "시작 날짜는 종료 날짜보다 이전이어야 합니다"
            );
        }

        // 2. 최대 기간 검증 (3개월)
        if (startDate.plusMonths(3).isBefore(endDate)) {
            throw new BookingException(
                    BookingErrorCode.AVAILABLE_SLOT_DATE_RANGE_EXCEEDED,
                    "슬롯 생성 기간은 최대 3개월입니다"
            );
        }

        // 3. 특정 시간대 검증 (선택사항)
        if (settings.specificTimeRanges() != null && !settings.specificTimeRanges().isEmpty()) {
            for (MenuRequest.TimeRange timeRange : settings.specificTimeRanges()) {
                validateTimeRange(timeRange);
            }
        }
    }

    /**
     * TimeRange 검증
     * - 시간 형식 검증 (HH:mm)
     * - startTime < endTime 검증
     *
     * @param timeRange 검증할 시간대
     * @throws BookingException 검증 실패 시
     */
    public void validateTimeRange(MenuRequest.TimeRange timeRange) {
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

}