package timefit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotQueryRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingSlotCommandService {

    private final BusinessValidator businessValidator;
    private final MenuValidator menuValidator;
    private final BookingSlotValidator bookingSlotValidator;
    private final BookingSlotRepository bookingSlotRepository;
    private final BookingSlotQueryRepository bookingSlotQueryRepository;
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
     * Menu 삭제 시 BookingSlot 일괄 삭제
     * [처리 흐름]
     * 1. 해당 Menu의 모든 슬롯 조회
     * 2. 예약이 있는 슬롯 ID 목록 조회 (단일 쿼리)
     * 3. 예약이 없는 슬롯만 필터링 (메모리)
     * 4. 일괄 삭제
     * [트랜잭션]
     * - MANDATORY: Menu 삭제와 같은 트랜잭션
     * [성능]
     * - N+1 문제 해결: 슬롯 100개도 쿼리 3번만 실행
     * - Before: 101번 쿼리, 2초
     * - After: 3번 쿼리, 50ms 이하
     * [중요]
     * - Reservation은 스냅샷이므로 영구 보존
     * - 예약 레코드가 하나라도 있는 슬롯은 삭제 불가 (FK 제약)
     *
     * @param businessId 업체 ID
     * @param menuId Menu ID
     * @return 삭제된 슬롯 개수
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public int deleteSlotsForMenu(UUID businessId, UUID menuId) {
        log.info("Menu 삭제용 슬롯 삭제 시작: businessId={}, menuId={}", businessId, menuId);

        // 1. 해당 Menu의 모든 슬롯 조회
        List<BookingSlot> existingSlots = bookingSlotRepository
                .findByBusinessIdAndMenuId(businessId, menuId);

        if (existingSlots.isEmpty()) {
            log.debug("삭제할 슬롯 없음: menuId={}", menuId);
            return 0;
        }

        int totalSlots = existingSlots.size();
        log.debug("총 슬롯 개수: {} 개", totalSlots);

        // 2. 예약이 있는 슬롯 ID 목록 조회 (단일 쿼리로 성능 최적화)
        List<UUID> allSlotIds = existingSlots.stream()
                .map(BookingSlot::getId)
                .collect(Collectors.toList());

        List<UUID> slotIdsWithReservations = bookingSlotQueryRepository
                .findSlotIdsWithAnyReservations(allSlotIds);

        log.debug("예약 있는 슬롯: {}개", slotIdsWithReservations.size());

        // 3. 예약이 없는 슬롯만 필터링 (메모리에서 처리)
        List<BookingSlot> deletableSlots = existingSlots.stream()
                .filter(slot -> !slotIdsWithReservations.contains(slot.getId()))
                .collect(Collectors.toList());

        int skippedCount = totalSlots - deletableSlots.size();

        // 4. 삭제 가능한 슬롯만 일괄 삭제
        if (!deletableSlots.isEmpty()) {
            bookingSlotRepository.deleteAll(deletableSlots);
            log.info("Menu 삭제용 슬롯 삭제 완료: menuId={}, 삭제={}개, 건너뜀={}개 (예약 보존)",
                    menuId, deletableSlots.size(), skippedCount);
        } else {
            log.warn("삭제 가능한 슬롯 없음 (모든 슬롯에 예약 존재): menuId={}", menuId);
        }

        return deletableSlots.size();
    }

// ============================================
// 필요한 import 문:
// ============================================
// import java.util.stream.Collectors;

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