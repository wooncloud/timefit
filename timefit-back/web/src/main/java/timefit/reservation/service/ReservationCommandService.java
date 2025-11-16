package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotQueryRepository;
import timefit.booking.repository.BookingSlotRepository;
import timefit.booking.service.validator.BookingSlotValidator;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.common.entity.BusinessRole;
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.menu.entity.Menu;
import timefit.menu.repository.MenuRepository;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepository;
import timefit.reservation.service.util.ReservationNumberUtil;
import timefit.reservation.service.validator.ReservationValidator;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.UUID;

/**
 * Reservation CUD 전담 서비스
 *
 * 담당 기능:
 * - 예약 생성 (RESERVATION_BASED / ONDEMAND_BASED)
 * - 예약 수정
 * - 예약 취소
 * - 예약 승인/거절 (업체)
 * - 예약 완료/노쇼 (업체)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final MenuRepository menuRepository;
    private final BookingSlotRepository bookingSlotRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final ReservationValidator reservationValidator;
    private final ReservationNumberUtil reservationNumberUtil;

    // ✅ BookingSlot 검증을 위한 의존성 추가
    private final BookingSlotValidator bookingSlotValidator;
    private final BookingSlotQueryRepository bookingSlotQueryRepository;

    // ========== 예약 생성 ==========

    /**
     * RESERVATION_BASED 예약 생성 (슬롯 기반)
     */
    public ReservationResponseDto.CustomerReservation createReservationBased(
            ReservationRequestDto.CreateReservation request, UUID customerId) {

        log.info("슬롯 기반 예약 생성 시작: customerId={}, businessId={}, menuId={}, bookingSlotId={}",
                customerId, request.getBusinessId(), request.getMenuId(), request.getBookingSlotId());

        // 1. 엔티티 조회
        User customer = getUserEntity(customerId);
        Business business = getBusinessEntity(request.getBusinessId());
        Menu menu = getMenuEntity(request.getMenuId());
        BookingSlot bookingSlot = getBookingSlotEntity(request.getBookingSlotId());

        // 2. 검증
        validateMenuBelongsToBusiness(menu, business.getId());
        validateBookingSlotBelongsToBusiness(bookingSlot, business.getId());

        // ✅ isAvailable 체크 (업체의 수동 비활성화)
        if (!bookingSlot.getIsAvailable()) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_NOT_AVAILABLE);
        }

        // ✅ BookingSlot 통합 검증 (실제 예약 수 체크)
        Integer currentBookings = bookingSlotQueryRepository
                .countActiveReservationsBySlot(bookingSlot.getId());
        bookingSlotValidator.validateBookableSlot(
                bookingSlot.getId(),
                business.getId(),
                currentBookings
        );

        // 3. Entity 생성 (정적 팩토리)
        Reservation reservation = Reservation.createReservationBased(
                customer,
                business,
                menu,
                bookingSlot,
                request.getCustomerName(),
                request.getCustomerPhone(),
                request.getNotes()
        );

        // 4. 저장
        Reservation savedReservation = reservationRepository.save(reservation);

        // 5. 예약 번호 생성 및 설정 (저장 후)
        String reservationNumber = reservationNumberUtil.generate();
        savedReservation.updateReservationNumber(reservationNumber);

        log.info("슬롯 기반 예약 생성 완료: reservationId={}, reservationNumber={}",
                savedReservation.getId(), reservationNumber);

        // 6. DTO 변환
        return ReservationResponseDto.CustomerReservation.from(savedReservation);
    }

    /**
     * ONDEMAND_BASED 예약 생성 (즉시 주문)
     */
    public ReservationResponseDto.CustomerReservation createOnDemandBased(
            ReservationRequestDto.CreateReservation request, UUID customerId) {

        log.info("즉시 주문 예약 생성 시작: customerId={}, businessId={}, menuId={}",
                customerId, request.getBusinessId(), request.getMenuId());

        // 1. 엔티티 조회
        User customer = getUserEntity(customerId);
        Business business = getBusinessEntity(request.getBusinessId());
        Menu menu = getMenuEntity(request.getMenuId());

        // 2. 검증
        validateMenuBelongsToBusiness(menu, business.getId());
        reservationValidator.validateNotPastDate(request.getReservationDate());

        // 3. Entity 생성 (정적 팩토리)
        Reservation reservation = Reservation.createOnDemandBased(
                customer,
                business,
                menu,
                request.getReservationDate(),
                request.getReservationTime(),
                request.getCustomerName(),
                request.getCustomerPhone(),
                request.getNotes()
        );

        // 4. 저장
        Reservation savedReservation = reservationRepository.save(reservation);

        // 5. 예약 번호 생성 및 설정 (저장 후)
        String reservationNumber = reservationNumberUtil.generate();
        savedReservation.updateReservationNumber(reservationNumber);

        log.info("즉시 주문 예약 생성 완료: reservationId={}, reservationNumber={}",
                savedReservation.getId(), reservationNumber);

        // 6. DTO 변환
        return ReservationResponseDto.CustomerReservation.from(savedReservation);
    }

    // ========== 예약 수정 ==========

    /**
     * 예약 수정 (고객)
     */
    public ReservationResponseDto.CustomerReservation updateReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.UpdateReservation request) {

        log.info("예약 수정 시작: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 예약 조회 및 소유자 확인
        Reservation reservation = reservationValidator.validateExists(reservationId);
        reservationValidator.validateOwner(reservation, customerId);

        // 2. 수정 가능 여부 확인 (Entity 메서드 사용)
        if (!reservation.isCancellable()) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_MODIFIABLE);
        }

        // 3. 날짜/시간 수정
        if (request.getReservationDate() != null && request.getReservationTime() != null) {
            reservationValidator.validateNotPastDate(request.getReservationDate());
            reservation.updateReservationDateTime(request.getReservationDate(), request.getReservationTime());
        }

        // 4. 고객 정보 수정
        if (request.getCustomerName() != null || request.getCustomerPhone() != null) {
            reservation.updateCustomerInfo(request.getCustomerName(), request.getCustomerPhone());
        }

        // 5. 메모 수정
        if (request.getNotes() != null) {
            reservation.updateNotes(request.getNotes());
        }

        log.info("예약 수정 완료: reservationId={}", reservationId);

        // 6. DTO 변환
        return ReservationResponseDto.CustomerReservation.from(reservation);
    }

    // ========== 예약 취소 ==========

    /**
     * 예약 취소 (고객)
     */
    public ReservationResponseDto.ReservationActionResult cancelReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.CancelReservation request) {

        log.info("예약 취소 시작: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 예약 조회 및 취소 가능 검증 (Validator 사용)
        Reservation reservation = reservationValidator.validateForCancel(reservationId, customerId);

        // 2. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 3. 취소 처리 (Entity 메서드)
        reservation.cancel();

        log.info("예약 취소 완료: reservationId={}", reservationId);

        // 4. DTO 변환
        String message = request.getReason() != null && !request.getReason().trim().isEmpty()
                ? "취소되었습니다: " + request.getReason()
                : "취소되었습니다";

        return ReservationResponseDto.ReservationActionResult.ofCancel(
                reservation, previousStatus, message);
    }

    // ========== 예약 승인/거절 (업체) ==========

    /**
     * 예약 승인 (업체)
     */
    public ReservationResponseDto.ReservationActionResult approveReservation(
            UUID businessId, UUID reservationId, UUID currentUserId) {

        log.info("예약 승인 시작: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 확인
        validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 조회 및 업체 소속 확인
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        // 3. 승인 가능 상태 확인
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_INVALID_STATUS);
        }

        // 4. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 5. 승인 처리 (Entity 메서드)
        reservation.confirm();

        log.info("예약 승인 완료: reservationId={}", reservationId);

        // 6. DTO 변환
        return ReservationResponseDto.ReservationActionResult.of(
                reservation, previousStatus, "승인되었습니다");
    }

    /**
     * 예약 거절 (업체)
     */
    public ReservationResponseDto.ReservationActionResult rejectReservation(
            UUID businessId, UUID reservationId, UUID currentUserId, String reason) {

        log.info("예약 거절 시작: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 확인
        validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 조회 및 업체 소속 확인
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        // 3. 거절 가능 상태 확인
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_INVALID_STATUS);
        }

        // 4. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 5. 거절 처리 (취소와 동일)
        reservation.cancel();

        log.info("예약 거절 완료: reservationId={}", reservationId);

        // 6. DTO 변환
        String message = reason != null && !reason.trim().isEmpty()
                ? "거절되었습니다: " + reason
                : "거절되었습니다";

        return ReservationResponseDto.ReservationActionResult.ofCancel(
                reservation, previousStatus, message);
    }

    // ========== 예약 완료/노쇼 (업체) ==========

    /**
     * 예약 완료 처리 (업체)
     */
    public ReservationResponseDto.ReservationActionResult completeReservation(
            UUID businessId, UUID reservationId, UUID currentUserId, String notes) {

        log.info("예약 완료 처리 시작: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 확인
        validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 조회 및 업체 소속 확인
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 완료 처리 (Entity 메서드)
        reservation.complete();

        // 5. 메모 추가
        if (notes != null && !notes.trim().isEmpty()) {
            reservation.updateNotes(notes);
        }

        log.info("예약 완료 처리 완료: reservationId={}", reservationId);

        // 6. DTO 변환
        return ReservationResponseDto.ReservationActionResult.of(
                reservation, previousStatus, "서비스가 완료되었습니다");
    }

    /**
     * 노쇼 처리 (업체)
     */
    public ReservationResponseDto.ReservationActionResult markAsNoShow(
            UUID businessId, UUID reservationId, UUID currentUserId, String notes) {

        log.info("노쇼 처리 시작: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 확인
        validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 조회 및 업체 소속 확인
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 노쇼 처리 (Entity 메서드)
        reservation.markAsNoShow();

        // 5. 메모 추가
        if (notes != null && !notes.trim().isEmpty()) {
            reservation.updateNotes(notes);
        }

        log.info("노쇼 처리 완료: reservationId={}", reservationId);

        // 6. DTO 변환
        return ReservationResponseDto.ReservationActionResult.of(
                reservation, previousStatus, "고객이 나타나지 않았습니다");
    }

    // ========== Private Helper Methods ==========

    /**
     * User 엔티티 조회
     */
    private User getUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.NOT_RESERVATION_OWNER));
    }

    /**
     * Business 엔티티 조회
     */
    private Business getBusinessEntity(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    /**
     * Menu 엔티티 조회
     */
    private Menu getMenuEntity(UUID menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));
    }

    /**
     * BookingSlot 엔티티 조회
     */
    private BookingSlot getBookingSlotEntity(UUID slotId) {
        return bookingSlotRepository.findById(slotId)
                .orElseThrow(() -> new BookingException(BookingErrorCode.AVAILABLE_SLOT_NOT_FOUND));
    }

    /**
     * Menu가 Business에 속하는지 검증
     */
    private void validateMenuBelongsToBusiness(Menu menu, UUID businessId) {
        if (!menu.getBusiness().getId().equals(businessId)) {
            throw new MenuException(MenuErrorCode.MENU_NOT_FOUND);
        }
    }

    /**
     * BookingSlot이 Business에 속하는지 검증
     */
    private void validateBookingSlotBelongsToBusiness(BookingSlot bookingSlot, UUID businessId) {
        if (!bookingSlot.getBusiness().getId().equals(businessId)) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_NOT_FOUND);
        }
    }

    /**
     * MANAGER 또는 OWNER 권한 검증
     */
    private void validateManagerOrOwnerRole(UUID userId, UUID businessId) {
        UserBusinessRole role = userBusinessRoleRepository
                .findByUserIdAndBusinessIdAndIsActive(userId, businessId, true)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_BUSINESS_MEMBER));

        if (role.getRole() != BusinessRole.OWNER && role.getRole() != BusinessRole.MANAGER) {
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }
    }
}