package timefit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.dto.BookingSlotResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * BookingSlot Facade 서비스
 * - 단순 위임 및 트랜잭션 경계 설정
 * 기존 파일: ScheduleService.java
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 기본값: 읽기 전용
public class BookingSlotService {

    private final BookingSlotQueryService bookingSlotQueryService;
    private final BookingSlotCommandService bookingSlotCommandService;

    // ========== 조회 (Query) ==========

    /**
     * 특정 날짜의 예약 가능한 슬롯 조회
     */
    public BookingSlotResponse.SlotList getAvailableSlots(UUID businessId, LocalDate slotDate) {
        return bookingSlotQueryService.getAvailableSlots(businessId, slotDate);
    }

    /**
     * 기간별 슬롯 조회
     */
    public BookingSlotResponse.SlotList getSlotsByDateRange(
            UUID businessId, LocalDate startDate, LocalDate endDate) {
        return bookingSlotQueryService.getSlotsByDateRange(businessId, startDate, endDate);
    }

    /**
     * 날짜별로 그룹핑된 슬롯 조회 (캘린더용)
     */
    public List<BookingSlotResponse.DailySlots> getDailySlotsGrouped(
            UUID businessId, LocalDate startDate, LocalDate endDate) {
        return bookingSlotQueryService.getDailySlotsGrouped(businessId, startDate, endDate);
    }

    /**
     * 특정 메뉴의 슬롯 조회
     */
    public BookingSlotResponse.SlotList getSlotsByMenu(
            UUID businessId, UUID menuId, LocalDate startDate, LocalDate endDate) {
        return bookingSlotQueryService.getSlotsByMenu(businessId, menuId, startDate, endDate);
    }

    /**
     * 오늘 이후 활성 슬롯 조회
     */
    public BookingSlotResponse.SlotList getUpcomingSlots(UUID businessId) {
        return bookingSlotQueryService.getUpcomingSlots(businessId);
    }

    // ========== 생성/수정/삭제 (Command) ==========

    /**
     * 단일 슬롯 생성
     */
    @Transactional  // 쓰기 트랜잭션
    public BookingSlotResponse.SlotDetail createSlot(
            UUID businessId,
            BookingSlotRequest.CreateSlot request,
            UUID currentUserId) {
        return bookingSlotCommandService.createSlot(businessId, request, currentUserId);
    }

    /**
     * 여러 슬롯 일괄 생성
     */
    @Transactional  // 쓰기 트랜잭션
    public BookingSlotResponse.SlotCreationResult createMultipleSlots(
            UUID businessId,
            BookingSlotRequest.CreateMultipleSlots request,
            UUID currentUserId) {
        return bookingSlotCommandService.createMultipleSlots(businessId, request, currentUserId);
    }

    /**
     * 슬롯 삭제
     */
    @Transactional  // 쓰기 트랜잭션
    public void deleteSlot(UUID businessId, UUID slotId, UUID currentUserId) {
        bookingSlotCommandService.deleteSlot(businessId, slotId, currentUserId);
    }

    /**
     * 슬롯 비활성화
     */
    @Transactional  // 쓰기 트랜잭션
    public BookingSlotResponse.SlotDetail deactivateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {
        return bookingSlotCommandService.deactivateSlot(businessId, slotId, currentUserId);
    }

    /**
     * 슬롯 재활성화
     */
    @Transactional  // 쓰기 트랜잭션
    public BookingSlotResponse.SlotDetail activateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {
        return bookingSlotCommandService.activateSlot(businessId, slotId, currentUserId);
    }

    /**
     * 과거 슬롯 일괄 삭제
     */
    @Transactional  // 쓰기 트랜잭션
    public Integer deletePastSlots(UUID businessId, UUID currentUserId) {
        return bookingSlotCommandService.deletePastSlots(businessId, currentUserId);
    }
}