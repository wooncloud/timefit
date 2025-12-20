package timefit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotRepository;
import timefit.booking.service.helper.BookingSlotCreationHelper;
import timefit.booking.service.validator.BookingSlotValidator;
import timefit.business.entity.Business;
import timefit.business.service.validator.BusinessValidator;
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;
import timefit.menu.entity.Menu;
import timefit.menu.service.validator.MenuValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingSlotCommandService {

    private final BusinessValidator businessValidator;
    private final MenuValidator menuValidator;
    private final BookingSlotValidator bookingSlotValidator;
    private final BookingSlotRepository bookingSlotRepository;
    private final BookingSlotCreationHelper bookingSlotCreationHelper;

    /**
     * BookingSlot 생성
     * 1. 권한 검증
     * 2. Menu 검증
     * 3. Helper 호출
     * @param businessId 업체 ID
     * @param request 슬롯 생성 요청 DTO
     * @param currentUserId 현재 사용자 ID
     * @return 생성 결과
     */
    public BookingSlotResponse.CreationResult createSlots(
            UUID businessId,
            BookingSlotRequest.BookingSlot request,
            UUID currentUserId) {

        log.info("슬롯 생성 시작: businessId={}, menuId={}", businessId, request.menuId());

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 검증
        Menu menu = menuValidator.validateMenuOfBusiness(request.menuId(), businessId);

        // 3. Menu 타입 검증 (RESERVATION_BASED만 가능)
        if (!menu.isReservationBased()) {
            throw new BookingException(BookingErrorCode.SLOT_INVALID_MENU_TYPE);
        }

        // 4. Helper 호출 (비즈니스 로직)
        return bookingSlotCreationHelper.createSlots(
                business,
                menu,
                request.schedules(),
                request.slotIntervalMinutes()
        );
    }

    /**
     * 슬롯 삭제
     * 1. 권한 검증 및 슬롯 조회
     * 2. 활성 예약 체크
     * 3. 삭제
     * @param businessId 업체 ID
     * @param slotId 슬롯 ID
     * @param currentUserId 현재 사용자 ID
     */
    public void deleteSlot(UUID businessId, UUID slotId, UUID currentUserId) {
        log.info("슬롯 삭제 시작: slotId={}", slotId);

        // 1. 권한 검증 및 슬롯 조회
        BookingSlot slot = validateAndGetSlot(businessId, slotId, currentUserId);

        // 2. 활성 예약 체크
        bookingSlotValidator.validateNoActiveReservations(slotId);

        // 3. 삭제
        bookingSlotRepository.delete(slot);

        log.info("슬롯 삭제 완료: slotId={}", slotId);
    }

    /**
     * 슬롯 비활성화
     * 1. 권한 검증 및 슬롯 조회
     * 2. 비활성화
     * 3. 저장 및 반환
     * @param businessId 업체 ID
     * @param slotId 슬롯 ID
     * @param currentUserId 현재 사용자 ID
     * @return 비활성화된 슬롯 정보
     */
    public BookingSlotResponse.BookingSlot deactivateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        log.info("슬롯 비활성화 시작: slotId={}", slotId);

        // 1. 권한 검증 및 슬롯 조회
        BookingSlot slot = validateAndGetSlot(businessId, slotId, currentUserId);

        // 2. 비활성화
        slot.markAsUnavailable();

        // 3. 저장 및 반환
        return saveAndConvertToResponse(slot, "비활성화");
    }

    /**
     * 슬롯 재활성화
     * 1. 권한 검증 및 슬롯 조회
     * 2. 활성화
     * 3. 저장 및 반환
     *
     * @param businessId 업체 ID
     * @param slotId 슬롯 ID
     * @param currentUserId 현재 사용자 ID
     * @return 활성화된 슬롯 정보
     */
    public BookingSlotResponse.BookingSlot activateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        log.info("슬롯 재활성화 시작: slotId={}", slotId);

        // 1. 권한 검증 및 슬롯 조회
        BookingSlot slot = validateAndGetSlot(businessId, slotId, currentUserId);

        // 2. 활성화
        slot.markAsAvailable();

        // 3. 저장 및 반환
        return saveAndConvertToResponse(slot, "재활성화");
    }

    /**
     * 과거 슬롯 일괄 삭제
     * 1. 권한 검증
     * 2. 오늘 이전 날짜의 슬롯 조회
     * 3. 일괄 삭제
     *
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @return 삭제된 슬롯 개수
     */
    public Integer deletePastSlots(UUID businessId, UUID currentUserId) {
        log.info("과거 슬롯 일괄 삭제 시작: businessId={}", businessId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 오늘 이전 날짜의 슬롯 조회
        LocalDate today = LocalDate.now();
        List<BookingSlot> pastSlots = bookingSlotRepository
                .findByBusinessIdAndSlotDateBefore(businessId, today);

        // 3. 일괄 삭제
        int count = pastSlots.size();
        bookingSlotRepository.deleteAll(pastSlots);

        log.info("과거 슬롯 삭제 완료: businessId={}, count={}", businessId, count);

        return count;
    }

    /**
     * Menu 재생성을 위한 슬롯 일괄 삭제
     * 1. 해당 Menu의 모든 슬롯 조회
     * 2. 활성 예약 체크 (하나라도 있으면 예외 발생)
     * 3. 일괄 삭제
     * [트랜잭션]
     * - MANDATORY: Menu 수정과 같은 트랜잭션
     * [중요]
     * - Reservation은 스냅샷이므로 슬롯 삭제해도 예약 기록 유지
     * - 단, 활성 예약이 있는 슬롯은 삭제 불가
     *
     * @param businessId 업체 ID
     * @param menuId Menu ID
     * @throws timefit.exception.booking.BookingException 활성 예약 존재 시
     */
    public void deleteSlotsForMenu(UUID businessId, UUID menuId) {
        log.info("Menu용 슬롯 삭제 시작: businessId={}, menuId={}", businessId, menuId);

        // 1. 해당 Menu의 모든 슬롯 조회
        List<BookingSlot> existingSlots = bookingSlotRepository
                .findByBusinessIdAndMenuId(businessId, menuId);

        if (existingSlots.isEmpty()) {
            log.debug("삭제할 슬롯 없음: menuId={}", menuId);
            return;
        }

        log.debug("삭제 대상 슬롯: {} 개", existingSlots.size());

        // 2. 활성 예약 체크 (하나라도 있으면 예외 발생)
        for (BookingSlot slot : existingSlots) {
            // 2-1) 활성 예약 검증 (있으면 BookingException 발생)
            bookingSlotValidator.validateNoActiveReservations(slot.getId());
        }

        // 3. 일괄 삭제
        bookingSlotRepository.deleteAll(existingSlots);

        log.info("Menu용 슬롯 삭제 완료: menuId={}, 삭제 개수={}", menuId, existingSlots.size());
    }

    // ===== Private Helper Methods =====

    /**
     * 권한 검증 및 슬롯 조회
     *
     * @param businessId 업체 ID
     * @param slotId 슬롯 ID
     * @param currentUserId 현재 사용자 ID
     * @return 조회된 BookingSlot
     */
    private BookingSlot validateAndGetSlot(UUID businessId, UUID slotId, UUID currentUserId) {
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        return bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);
    }

    /**
     * 슬롯 저장 및 응답 DTO 변환
     *
     * @param slot 저장할 BookingSlot
     * @param action 작업 이름 (로그용)
     * @return 응답 DTO
     */
    private BookingSlotResponse.BookingSlot saveAndConvertToResponse(BookingSlot slot, String action) {
        BookingSlot updated = bookingSlotRepository.save(slot);
        log.info("슬롯 {} 완료: slotId={}", action, slot.getId());
        return BookingSlotResponse.BookingSlot.of(updated);
    }
}