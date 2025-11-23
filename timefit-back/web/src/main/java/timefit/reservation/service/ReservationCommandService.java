package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotRepository;
import timefit.booking.service.validator.BookingSlotValidator;
import timefit.business.entity.Business;
import timefit.business.repository.BusinessRepository;
import timefit.business.service.validator.BusinessValidator;
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
import timefit.reservation.service.util.ReservationConverter;
import timefit.reservation.service.validator.ReservationValidator;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.UUID;

/**
 * Reservation Command Service
 * - CUD 전담 (Create, Update, Delete)
 * - Converter를 통한 변환 로직 분리
 * - Validator를 통한 검증 로직 분리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandService {

    // Repository
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final MenuRepository menuRepository;
    private final BookingSlotRepository bookingSlotRepository;

    // Validator
    private final ReservationValidator reservationValidator;
    private final BusinessValidator businessValidator;
    private final BookingSlotValidator bookingSlotValidator;

    // Util
    private final ReservationNumberUtil reservationNumberUtil;
    private final ReservationConverter converter;

    // ========== 예약 생성 ==========

    /**
     * 슬롯 기반 예약 생성 (RESERVATION_BASED)
     */
    public ReservationResponseDto.CustomerReservation createReservationBased(
            ReservationRequestDto.CreateReservation request, UUID customerId) {

        log.info("슬롯 기반 예약 생성: customerId={}, bookingSlotId={}",
                customerId, request.bookingSlotId());

        // 1. Entity 조회
        User customer = getUserEntity(customerId);
        Business business = getBusinessEntity(request.businessId());
        Menu menu = getMenuEntity(request.menuId());
        BookingSlot bookingSlot = getBookingSlotEntity(request.bookingSlotId());

        // 2. 검증
        validateMenuBelongsToBusiness(menu, business.getId());
        validateBookingSlotBelongsToBusiness(bookingSlot, business.getId());
        bookingSlotValidator.validateBookableSlot(bookingSlot.getId(), business.getId());

        // 3. Entity 생성 (정적 팩토리)
        Reservation reservation = Reservation.createReservationBased(
                customer,
                business,
                menu,
                bookingSlot,
                request.customerName(),
                request.customerPhone(),
                request.notes()
        );

        // 4. 저장 및 예약 번호 생성
        Reservation saved = reservationRepository.save(reservation);
        saved.updateReservationNumber(reservationNumberUtil.generate());

        log.info("슬롯 기반 예약 생성 완료: reservationId={}, number={}",
                saved.getId(), saved.getReservationNumber());

        // 5. DTO 변환
        return converter.toCustomerReservation(saved);
    }

    /**
     * 즉시 주문 예약 생성 (ONDEMAND_BASED)
     */
    public ReservationResponseDto.CustomerReservation createOnDemandBased(
            ReservationRequestDto.CreateReservation request, UUID customerId) {

        log.info("즉시 주문 예약 생성: customerId={}, date={}",
                customerId, request.reservationDate());

        // 1. Entity 조회
        User customer = getUserEntity(customerId);
        Business business = getBusinessEntity(request.businessId());
        Menu menu = getMenuEntity(request.menuId());

        // 2. 검증
        validateMenuBelongsToBusiness(menu, business.getId());
        reservationValidator.validateNotPastDate(request.reservationDate());

        // 3. Entity 생성
        Reservation reservation = Reservation.createOnDemandBased(
                customer,
                business,
                menu,
                request.reservationDate(),
                request.reservationTime(),
                request.customerName(),
                request.customerPhone(),
                request.notes()
        );

        // 4. 저장 및 예약 번호 생성
        Reservation saved = reservationRepository.save(reservation);
        saved.updateReservationNumber(reservationNumberUtil.generate());

        log.info("즉시 주문 예약 생성 완료: reservationId={}, number={}",
                saved.getId(), saved.getReservationNumber());

        // 5. DTO 변환
        return converter.toCustomerReservation(saved);
    }

    // ========== 예약 수정 (고객) ==========

    /**
     * 예약 수정
     */
    public ReservationResponseDto.CustomerReservation updateReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.UpdateReservation request) {

        log.info("예약 수정: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 조회 및 검증
        Reservation reservation = reservationValidator.validateExists(reservationId);
        reservationValidator.validateOwner(reservation, customerId);

        // 2. 수정 가능 여부 확인
        if (!reservation.isCancellable()) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_MODIFIABLE);
        }

        // 3. 수정 처리
        if (request.reservationDate() != null && request.reservationTime() != null) {
            reservationValidator.validateNotPastDate(request.reservationDate());
            reservation.updateReservationDateTime(request.reservationDate(), request.reservationTime());
        }

        if (request.customerName() != null || request.customerPhone() != null) {
            reservation.updateCustomerInfo(request.customerName(), request.customerPhone());
        }

        if (request.notes() != null) {
            reservation.updateNotes(request.notes());
        }

        log.info("예약 수정 완료: reservationId={}", reservationId);

        // 4. DTO 변환
        return converter.toCustomerReservation(reservation);
    }

    // ========== 예약 취소 (고객) ==========

    /**
     * 예약 취소
     */
    public ReservationResponseDto.ReservationActionResult cancelReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.CancelReservation request) {

        log.info("예약 취소: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 검증
        Reservation reservation = reservationValidator.validateForCancel(reservationId, customerId);

        // 2. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 3. 취소 처리
        reservation.cancel();

        log.info("예약 취소 완료: reservationId={}", reservationId);

        // 4. DTO 변환
        String message = request.reason() != null && !request.reason().trim().isEmpty()
                ? "취소되었습니다: " + request.reason()
                : "취소되었습니다";

        return converter.toCancelActionResult(reservation, previousStatus, message);
    }

    // ========== 예약 승인/거절 (업체) ==========

    /**
     * 예약 승인
     */
    public ReservationResponseDto.ReservationActionResult approveReservation(
            UUID businessId, UUID reservationId, UUID currentUserId) {

        log.info("예약 승인: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 검증
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_INVALID_STATUS);
        }

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 승인 처리
        reservation.confirm();

        log.info("예약 승인 완료: reservationId={}", reservationId);

        // 5. DTO 변환
        return converter.toActionResult(reservation, previousStatus, "승인되었습니다");
    }

    /**
     * 예약 거절
     */
    public ReservationResponseDto.ReservationActionResult rejectReservation(
            UUID businessId, UUID reservationId, UUID currentUserId, String reason) {

        log.info("예약 거절: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 검증
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_INVALID_STATUS);
        }

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 거절 처리 (취소와 동일)
        reservation.cancel();

        log.info("예약 거절 완료: reservationId={}", reservationId);

        // 5. DTO 변환
        String message = reason != null && !reason.trim().isEmpty()
                ? "거절되었습니다: " + reason
                : "거절되었습니다";

        return converter.toActionResult(reservation, previousStatus, message);
    }

    // ========== 예약 완료/노쇼 (업체) ==========

    /**
     * 예약 완료 처리
     */
    public ReservationResponseDto.ReservationActionResult completeReservation(
            UUID businessId, UUID reservationId, UUID currentUserId, String notes) {

        log.info("예약 완료 처리: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 검증
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 완료 처리
        reservation.complete();

        log.info("예약 완료 처리 완료: reservationId={}", reservationId);

        // 5. DTO 변환
        String message = "서비스가 완료되었습니다";
        if (notes != null && !notes.trim().isEmpty()) {
            message += " - " + notes;
        }

        return converter.toActionResult(reservation, previousStatus, message);
    }

    /**
     * 노쇼 처리
     */
    public ReservationResponseDto.ReservationActionResult markAsNoShow(
            UUID businessId, UUID reservationId, UUID currentUserId, String notes) {

        log.info("노쇼 처리: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 검증
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 노쇼 처리
        reservation.markAsNoShow();

        log.info("노쇼 처리 완료: reservationId={}", reservationId);

        // 5. DTO 변환
        String message = "고객이 나타나지 않았습니다";
        if (notes != null && !notes.trim().isEmpty()) {
            message += " - " + notes;
        }

        return converter.toActionResult(reservation, previousStatus, message);
    }

    // ========== Private 헬퍼 메서드 ==========

    private User getUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
    }

    private Business getBusinessEntity(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

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

    private void validateMenuBelongsToBusiness(Menu menu, UUID businessId) {
        if (!menu.getBusiness().getId().equals(businessId)) {
            throw new MenuException(MenuErrorCode.MENU_NOT_FOUND);
        }
    }

    private void validateBookingSlotBelongsToBusiness(BookingSlot bookingSlot, UUID businessId) {
        if (!bookingSlot.getBusiness().getId().equals(businessId)) {
            throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_NOT_FOUND);
        }
    }
}