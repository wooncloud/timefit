package timefit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.dto.BookingSlotResponse;

import java.time.LocalDate;
import java.util.UUID;

/**
 * BookingSlot Facade Service
 * - 단순 위임만 수행
 * - 트랜잭션 경계 설정
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingSlotService {

    private final BookingSlotCommandService bookingSlotCommandService;
    private final BookingSlotQueryService bookingSlotQueryService;

    // === Command (CUD) ===

    /**
     * 슬롯 생성
     */
    @Transactional
    public BookingSlotResponse.CreationResult createSlots(
            UUID businessId,
            BookingSlotRequest.BookingSlot request,
            UUID currentUserId) {
        return bookingSlotCommandService.createSlots(businessId, request, currentUserId);
    }

    /**
     * 슬롯 삭제
     */
    @Transactional
    public void deleteSlot(UUID businessId, UUID slotId, UUID currentUserId) {
        bookingSlotCommandService.deleteSlot(businessId, slotId, currentUserId);
    }

    /**
     * 슬롯 비활성화
     */
    @Transactional
    public BookingSlotResponse.BookingSlot deactivateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {
        return bookingSlotCommandService.deactivateSlot(businessId, slotId, currentUserId);
    }

    /**
     * 슬롯 재활성화
     */
    @Transactional
    public BookingSlotResponse.BookingSlot activateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {
        return bookingSlotCommandService.activateSlot(businessId, slotId, currentUserId);
    }

    /**
     * 과거 슬롯 일괄 삭제
     */
    @Transactional
    public Integer deletePastSlots(UUID businessId, UUID currentUserId) {
        return bookingSlotCommandService.deletePastSlots(businessId, currentUserId);
    }

    // === Query (Read) ===

    /**
     * 특정 날짜의 슬롯 조회
     */
    @Transactional(readOnly = true)
    public BookingSlotResponse.BookingSlotList getSlotsByDate(
            UUID businessId, LocalDate date) {
        return bookingSlotQueryService.getSlotsByDate(businessId, date);
    }

    /**
     * 날짜 범위의 슬롯 조회
     */
    @Transactional(readOnly = true)
    public BookingSlotResponse.BookingSlotList getSlotsByDateRange(
            UUID businessId, LocalDate startDate, LocalDate endDate) {
        return bookingSlotQueryService.getSlotsByDateRange(businessId, startDate, endDate);
    }

    /**
     * 메뉴별 슬롯 조회
     */
    @Transactional(readOnly = true)
    public BookingSlotResponse.BookingSlotList getSlotsByMenu(
            UUID businessId, UUID menuId, LocalDate startDate, LocalDate endDate) {
        return bookingSlotQueryService.getSlotsByMenu(businessId, menuId, startDate, endDate);
    }

    /**
     * 향후 활성 슬롯 조회
     */
    @Transactional(readOnly = true)
    public BookingSlotResponse.BookingSlotList getUpcomingSlots(UUID businessId) {
        return bookingSlotQueryService.getUpcomingSlots(businessId);
    }
}