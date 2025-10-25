package timefit.booking.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotRepository;
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;

import java.time.LocalDate;
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
     * BookingSlot의 예약 가능 여부 검증 (capacity 제거)
     *
     * @param slot 검증할 BookingSlot 엔티티
     * @param currentBookings 현재 예약 수 (0 또는 1)
     * @throws BookingException 이미 예약된 슬롯일 경우
     */
    public void validateSlotCapacity(BookingSlot slot, Integer currentBookings) {
        if (!slot.canAcceptReservation(currentBookings)) {
            log.warn("슬롯 예약 불가 (이미 예약됨): slotId={}, currentBookings={}",
                    slot.getId(), currentBookings);
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
     * @param currentBookings 현재 예약 수 (0 또는 1)
     * @return 조회된 예약 가능한 BookingSlot 엔티티
     */
    public BookingSlot validateBookableSlot(UUID slotId, UUID businessId, Integer currentBookings) {
        BookingSlot slot = validateSlotOfBusiness(slotId, businessId);
        validateSlotAvailable(slot);
        validateSlotNotPast(slot);
        validateSlotCapacity(slot, currentBookings);
        validateSlotTimeFormat(slot);
        return slot;
    }
}