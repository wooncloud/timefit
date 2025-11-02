package timefit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotQueryRepository;
import timefit.booking.repository.BookingSlotRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingSlotQueryService {

    private final BookingSlotRepository bookingSlotRepository;
    private final BookingSlotQueryRepository bookingSlotQueryRepository;
    private final BusinessValidator businessValidator;

    /**
     * 특정 날짜의 슬롯 조회
     */
    public BookingSlotResponse.SlotList getSlotsByDate(UUID businessId, LocalDate slotDate) {
        log.info("특정 날짜 슬롯 조회 시작: businessId={}, date={}", businessId, slotDate);

        // 1. 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 2. 해당 날짜의 슬롯 조회
        List<BookingSlot> slots = bookingSlotRepository
                .findByBusinessIdAndSlotDateOrderByStartTimeAsc(businessId, slotDate);

        // 3. DTO 변환 (현재 예약 수 포함)
        List<BookingSlotResponse.SlotDetail> slotDetails = slots.stream()
                .map(slot -> {
                    Integer currentBookings = bookingSlotQueryRepository
                            .countActiveReservationsBySlot(slot.getId());
                    return BookingSlotResponse.SlotDetail.of(slot, currentBookings);
                })
                .collect(Collectors.toList());

        log.info("특정 날짜 슬롯 조회 완료: businessId={}, date={}, count={}",
                businessId, slotDate, slotDetails.size());

        return BookingSlotResponse.SlotList.of(businessId, slotDate, slotDate, slotDetails);
    }

    /**
     * 기간별 슬롯 조회
     */
    public BookingSlotResponse.SlotList getSlotsByDateRange(
            UUID businessId, LocalDate startDate, LocalDate endDate) {

        log.info("기간별 슬롯 조회 시작: businessId={}, startDate={}, endDate={}",
                businessId, startDate, endDate);

        // 1. 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 2. 기간 유효성 검증
        validateDateRange(startDate, endDate);

        // 3. 기간 내 슬롯 조회
        List<BookingSlot> slots = bookingSlotQueryRepository
                .findByBusinessIdAndDateRange(businessId, startDate, endDate);

        // 4. DTO 변환
        List<BookingSlotResponse.SlotDetail> slotDetails = slots.stream()
                .map(slot -> {
                    Integer currentBookings = bookingSlotQueryRepository
                            .countActiveReservationsBySlot(slot.getId());
                    return BookingSlotResponse.SlotDetail.of(slot, currentBookings);
                })
                .collect(Collectors.toList());

        log.info("기간별 슬롯 조회 완료: businessId={}, count={}", businessId, slotDetails.size());

        return BookingSlotResponse.SlotList.of(businessId, startDate, endDate, slotDetails);
    }

    /**
     * 특정 메뉴의 슬롯 조회
     */
    public BookingSlotResponse.SlotList getSlotsByMenu(
            UUID businessId, UUID menuId, LocalDate startDate, LocalDate endDate) {

        log.info("메뉴별 슬롯 조회 시작: businessId={}, menuId={}", businessId, menuId);

        // 1. 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 2. 기간 유효성 검증
        validateDateRange(startDate, endDate);

        // 3. 메뉴의 슬롯 조회
        List<BookingSlot> allSlots = bookingSlotQueryRepository
                .findByBusinessIdAndDateRange(businessId, startDate, endDate);

        // 4. 특정 메뉴 필터링
        List<BookingSlot> menuSlots = allSlots.stream()
                .filter(slot -> slot.getMenu().getId().equals(menuId))
                .collect(Collectors.toList());

        // 5. DTO 변환
        List<BookingSlotResponse.SlotDetail> slotDetails = menuSlots.stream()
                .map(slot -> {
                    Integer currentBookings = bookingSlotQueryRepository
                            .countActiveReservationsBySlot(slot.getId());
                    return BookingSlotResponse.SlotDetail.of(slot, currentBookings);
                })
                .collect(Collectors.toList());

        log.info("메뉴별 슬롯 조회 완료: menuId={}, count={}", menuId, slotDetails.size());

        return BookingSlotResponse.SlotList.of(businessId, startDate, endDate, slotDetails);
    }

    /**
     * 오늘 이후 활성 슬롯 조회
     */
    public BookingSlotResponse.SlotList getUpcomingSlots(UUID businessId) {
        log.info("향후 슬롯 조회 시작: businessId={}", businessId);

        // 1. 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 2. 향후 활성 슬롯 조회
        List<BookingSlot> slots = bookingSlotQueryRepository
                .findUpcomingActiveSlotsByBusinessId(businessId);

        // 3. DTO 변환
        List<BookingSlotResponse.SlotDetail> slotDetails = slots.stream()
                .map(slot -> {
                    Integer currentBookings = bookingSlotQueryRepository
                            .countActiveReservationsBySlot(slot.getId());
                    return BookingSlotResponse.SlotDetail.of(slot, currentBookings);
                })
                .collect(Collectors.toList());

        log.info("향후 슬롯 조회 완료: businessId={}, count={}", businessId, slotDetails.size());

        LocalDate today = LocalDate.now();
        return BookingSlotResponse.SlotList.of(businessId, today, today.plusMonths(3), slotDetails);
    }

    // === Private 메서드 ===

    // 기간 유효성 검증
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_INVALID_TIME);
        }
    }
}