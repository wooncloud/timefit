package timefit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotQueryRepository;
import timefit.booking.repository.BookingSlotRepository;
import timefit.booking.service.util.BookingSlotDtoConverter;
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
    private final BookingSlotDtoConverter dtoConverter;

    // 특정 날짜의 슬롯 조회
    public BookingSlotResponse.BookingSlotList getSlotsByDate(UUID businessId, LocalDate date) {
        log.info("특정 날짜 슬롯 조회 시작: businessId={}, date={}", businessId, date);

        businessValidator.validateBusinessExists(businessId);

        List<BookingSlot> slots = bookingSlotRepository
                .findByBusinessIdAndSlotDateOrderByStartTimeAsc(businessId, date);

        List<BookingSlotResponse.BookingSlot> slotDetails = dtoConverter.convertToResponseList(slots);

        log.info("특정 날짜 슬롯 조회 완료: businessId={}, date={}, count={}",
                businessId, date, slotDetails.size());

        return BookingSlotResponse.BookingSlotList.of(businessId, date, date, slotDetails);
    }

    // 기간별 슬롯 조회
    public BookingSlotResponse.BookingSlotList getSlotsByDateRange(
            UUID businessId, LocalDate startDate, LocalDate endDate) {

        log.info("기간별 슬롯 조회 시작: businessId={}, startDate={}, endDate={}",
                businessId, startDate, endDate);

        businessValidator.validateBusinessExists(businessId);
        validateDateRange(startDate, endDate);

        List<BookingSlot> slots = bookingSlotQueryRepository
                .findByBusinessIdAndDateRange(businessId, startDate, endDate);

        List<BookingSlotResponse.BookingSlot> slotDetails = dtoConverter.convertToResponseList(slots);

        log.info("기간별 슬롯 조회 완료: businessId={}, count={}", businessId, slotDetails.size());

        return BookingSlotResponse.BookingSlotList.of(businessId, startDate, endDate, slotDetails);
    }

    // 메뉴별 슬롯 조회
    public BookingSlotResponse.BookingSlotList getSlotsByMenu(
            UUID businessId, UUID menuId, LocalDate startDate, LocalDate endDate) {

        log.info("메뉴별 슬롯 조회 시작: businessId={}, menuId={}", businessId, menuId);

        businessValidator.validateBusinessExists(businessId);
        validateDateRange(startDate, endDate);

        List<BookingSlot> allSlots = bookingSlotQueryRepository
                .findByBusinessIdAndDateRange(businessId, startDate, endDate);

        List<BookingSlot> menuSlots = filterByMenu(allSlots, menuId);
        List<BookingSlotResponse.BookingSlot> slotDetails = dtoConverter.convertToResponseList(menuSlots);

        log.info("메뉴별 슬롯 조회 완료: menuId={}, count={}", menuId, slotDetails.size());

        return BookingSlotResponse.BookingSlotList.of(businessId, startDate, endDate, slotDetails);
    }

    // 향후 활성 슬롯 조회
    public BookingSlotResponse.BookingSlotList getUpcomingSlots(UUID businessId) {
        log.info("향후 슬롯 조회 시작: businessId={}", businessId);

        businessValidator.validateBusinessExists(businessId);

        List<BookingSlot> slots = bookingSlotQueryRepository
                .findUpcomingActiveSlotsByBusinessId(businessId);

        List<BookingSlotResponse.BookingSlot> slotDetails = dtoConverter.convertToResponseList(slots);

        log.info("향후 슬롯 조회 완료: businessId={}, count={}", businessId, slotDetails.size());

        LocalDate today = LocalDate.now();
        return BookingSlotResponse.BookingSlotList.of(businessId, today, today.plusMonths(3), slotDetails);
    }


    // 기간 유효성 검증
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_INVALID_TIME);
        }
    }

    // 특정 메뉴 필터링
    private List<BookingSlot> filterByMenu(List<BookingSlot> slots, UUID menuId) {
        return slots.stream()
                .filter(slot -> slot.getMenu().getId().equals(menuId))
                .collect(Collectors.toList());
    }
}