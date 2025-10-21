package timefit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotRepository;
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

/**
 * BookingSlot 생성/수정/삭제 전담 서비스
 * 기존 파일: ScheduleService (CUD 부분)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingSlotCommandService {

    private final BookingSlotRepository bookingSlotRepository;
    private final BusinessValidator businessValidator;
    private final MenuValidator menuValidator;
    private final BookingSlotValidator bookingSlotValidator;

    /**
     * 단일 슬롯 생성
     */
    public BookingSlotResponse.SlotDetail createSlot(
            UUID businessId,
            BookingSlotRequest.CreateSlot request,
            UUID currentUserId) {

        log.info("슬롯 생성 시작: businessId={}, menuId={}, date={}",
                businessId, request.getMenuId(), request.getSlotDate());

        // 1. 권한 검증 (Manager 또는 Owner)
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. 메뉴 검증
        Menu menu = menuValidator.validateMenuOfBusiness(request.getMenuId(), businessId);

        // 3. 메뉴가 RESERVATION_BASED인지 확인
        if (!menu.isReservationBased()) {
            throw new BookingException(BookingErrorCode.SLOT_INVALID_MENU_TYPE);
        }

        // 4. 슬롯 중복 검증
        validateSlotNotDuplicate(businessId, request.getSlotDate(),
                request.getStartTime(), request.getEndTime());

        // 5. 시간 유효성 검증
        validateSlotTime(request.getSlotDate(), request.getStartTime(), request.getEndTime());

        // 6. BookingSlot 생성 (Entity의 정적 팩토리 사용)
        BookingSlot slot = BookingSlot.createReservationSlot(
                business,
                menu,
                request.getSlotDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getCapacity()
        );

        // 7. 저장
        BookingSlot savedSlot = bookingSlotRepository.save(slot);

        log.info("슬롯 생성 완료: slotId={}", savedSlot.getId());

        // 8. DTO 변환
        return BookingSlotResponse.SlotDetail.of(savedSlot, 0);
    }

    /**
     * 여러 슬롯 일괄 생성
     */
    public BookingSlotResponse.SlotCreationResult createMultipleSlots(
            UUID businessId,
            BookingSlotRequest.CreateMultipleSlots request,
            UUID currentUserId) {

        log.info("여러 슬롯 일괄 생성 시작: businessId={}, menuId={}, startDate={}, endDate={}",
                businessId, request.getMenuId(), request.getStartDate(), request.getEndDate());

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. 메뉴 검증
        Menu menu = menuValidator.validateMenuOfBusiness(request.getMenuId(), businessId);

        // 3. 메뉴가 RESERVATION_BASED인지 확인
        if (!menu.isReservationBased()) {
            throw new BookingException(BookingErrorCode.SLOT_INVALID_MENU_TYPE);
        }

        // 4. 날짜 범위 유효성 검증
        validateDateRange(request.getStartDate(), request.getEndDate());

        // 5. 슬롯 생성
        List<BookingSlot> createdSlots = new ArrayList<>();
        int totalRequested = 0;

        // 날짜 범위만큼 반복
        LocalDate currentDate = request.getStartDate();
        while (!currentDate.isAfter(request.getEndDate())) {

            // 각 날짜에 대해 슬롯 시간 목록만큼 반복
            for (BookingSlotRequest.SlotTime slotTime : request.getSlotTimes()) {
                totalRequested++;

                // 중복 체크 (이미 존재하는 슬롯은 건너뛰기)
                boolean exists = bookingSlotRepository.existsByBusinessIdAndSlotDateAndStartTime(
                        businessId, currentDate, slotTime.getStartTime());

                if (exists) {
                    log.warn("슬롯 중복으로 건너뜀: date={}, startTime={}",
                            currentDate, slotTime.getStartTime());
                    continue;
                }

                // 슬롯 생성
                try {
                    BookingSlot slot = BookingSlot.createReservationSlot(
                            business,
                            menu,
                            currentDate,
                            slotTime.getStartTime(),
                            slotTime.getEndTime(),
                            request.getCapacity()
                    );
                    createdSlots.add(slot);
                } catch (Exception e) {
                    log.error("슬롯 생성 실패: date={}, startTime={}, error={}",
                            currentDate, slotTime.getStartTime(), e.getMessage());
                }
            }

            currentDate = currentDate.plusDays(1);
        }

        // 6. 일괄 저장
        List<BookingSlot> savedSlots = bookingSlotRepository.saveAll(createdSlots);

        log.info("여러 슬롯 일괄 생성 완료: 요청={}, 생성={}", totalRequested, savedSlots.size());

        // 7. DTO 변환
        return BookingSlotResponse.SlotCreationResult.of(totalRequested, savedSlots);
    }

    /**
     * 슬롯 삭제 (실제 삭제)
     */
    public void deleteSlot(UUID businessId, UUID slotId, UUID currentUserId) {
        log.info("슬롯 삭제 시작: businessId={}, slotId={}", businessId, slotId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 슬롯 조회 및 Business 소속 확인
        BookingSlot slot = bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);

        // 3. 슬롯에 예약이 있는지 확인
        // TODO: 예약이 있으면 삭제 불가 로직 추가 필요
        // Integer activeBookings = bookingSlotQueryRepository.countActiveReservationsBySlot(slotId);
        // if (activeBookings > 0) {
        //     throw new ScheduleException(ScheduleErrorCode.AVAILABLE_SLOT_HAS_RESERVATIONS);
        // }

        // 4. 삭제
        bookingSlotRepository.delete(slot);

        log.info("슬롯 삭제 완료: slotId={}", slotId);
    }

    /**
     * 슬롯 비활성화 (soft delete)
     */
    public BookingSlotResponse.SlotDetail deactivateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        log.info("슬롯 비활성화 시작: businessId={}, slotId={}", businessId, slotId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 슬롯 조회 및 Business 소속 확인
        BookingSlot slot = bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);

        // 3. 비활성화
        slot.markAsUnavailable();

        // 4. 저장
        BookingSlot updatedSlot = bookingSlotRepository.save(slot);

        log.info("슬롯 비활성화 완료: slotId={}", slotId);

        // 5. DTO 변환
        return BookingSlotResponse.SlotDetail.of(updatedSlot, 0);
    }

    /**
     * 슬롯 재활성화
     */
    public BookingSlotResponse.SlotDetail activateSlot(
            UUID businessId, UUID slotId, UUID currentUserId) {

        log.info("슬롯 재활성화 시작: businessId={}, slotId={}", businessId, slotId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 슬롯 조회 및 Business 소속 확인
        BookingSlot slot = bookingSlotValidator.validateSlotOfBusiness(slotId, businessId);

        // 3. 재활성화
        slot.markAsAvailable();

        // 4. 저장
        BookingSlot updatedSlot = bookingSlotRepository.save(slot);

        log.info("슬롯 재활성화 완료: slotId={}", slotId);

        // 5. DTO 변환
        return BookingSlotResponse.SlotDetail.of(updatedSlot, 0);
    }

    /**
     * 과거 슬롯 일괄 삭제 (정리 작업)
     */
    public Integer deletePastSlots(UUID businessId, UUID currentUserId) {
        log.info("과거 슬롯 일괄 삭제 시작: businessId={}", businessId);

        // 1. 권한 검증 (Owner만 가능)
        businessValidator.validateOwnerRole(currentUserId, businessId);

        // 2. 어제 이전 슬롯 조회
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<BookingSlot> pastSlots = bookingSlotRepository.findBySlotDateBefore(yesterday);

        // 3. 해당 업체의 슬롯만 필터링
        List<BookingSlot> businessPastSlots = pastSlots.stream()
                .filter(slot -> slot.getBusiness().getId().equals(businessId))
                .toList();

        // 4. 일괄 삭제
        bookingSlotRepository.deleteAll(businessPastSlots);

        log.info("과거 슬롯 일괄 삭제 완료: businessId={}, count={}",
                businessId, businessPastSlots.size());

        return businessPastSlots.size();
    }

    // ========== Private Helper Methods ==========

    /**
     * 슬롯 중복 검증
     */
    private void validateSlotNotDuplicate(UUID businessId, LocalDate slotDate,
                                          java.time.LocalTime startTime,
                                          java.time.LocalTime endTime) {
        boolean exists = bookingSlotRepository.existsByBusinessIdAndSlotDateAndStartTime(
                businessId, slotDate, startTime);

        if (exists) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_CONFLICT);
        }
    }

    /**
     * 슬롯 시간 유효성 검증
     */
    private void validateSlotTime(LocalDate slotDate, java.time.LocalTime startTime,
                                  java.time.LocalTime endTime) {
        // 1. 과거 날짜 체크
        if (slotDate.isBefore(LocalDate.now())) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_PAST_DATE);
        }

        // 2. 시작 시간이 종료 시간보다 늦은지 체크
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_INVALID_TIME);
        }
    }

    /**
     * 날짜 범위 유효성 검증
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_DATE_RANGE_EXCEEDED);
        }

        // 최대 생성 기간 제한 (예: 3개월)
        if (startDate.plusMonths(3).isBefore(endDate)) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_DATE_RANGE_EXCEEDED);
        }
    }
}