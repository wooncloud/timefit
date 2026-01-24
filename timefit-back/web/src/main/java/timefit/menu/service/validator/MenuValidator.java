package timefit.menu.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.service.validator.BookingSlotValidator;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;
import timefit.menu.repository.MenuRepository;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepository;

import java.util.List;
import java.util.UUID;

/**
 * Menu 기본 검증자
 * - Menu 존재 여부 검증
 * - Menu 소속 검증
 * - Menu 활성 상태 검증
 * - Menu 생성/수정 요청 검증
 * - OrderType 변경 불가 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuValidator {

    private final MenuRepository menuRepository;
    private final BookingSlotValidator bookingSlotValidator;
    private final ReservationRepository reservationRepository;

    /**
     * Menu 존재 여부 검증 및 조회
     *
     * @param menuId 검증할 Menu ID
     * @return 조회된 Menu 엔티티
     * @throws MenuException Menu가 존재하지 않는 경우
     */
    public Menu validateMenuExists(UUID menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 메뉴 ID: {}", menuId);
                    return new MenuException(MenuErrorCode.MENU_NOT_FOUND);
                });
    }

    /**
     * Menu가 특정 Business에 속하는지 검증
     *
     * @param menu 검증할 Menu 엔티티
     * @param businessId 확인할 Business ID
     * @throws MenuException Menu가 해당 Business에 속하지 않는 경우
     */
    public void validateMenuBelongsToBusiness(Menu menu, UUID businessId) {
        if (!menu.getBusiness().getId().equals(businessId)) {
            log.warn("메뉴가 해당 업체에 속하지 않음: menuId={}, businessId={}",
                    menu.getId(), businessId);
            throw new MenuException(MenuErrorCode.MENU_ACCESS_DENIED);
        }
    }

    /**
     * Menu 중복 생성 방지
     * @param businessId 업체 ID
     * @param serviceName 서비스명
     * @throws MenuException 중복 메뉴 발견 시
     */
    public void validateMenuNotDuplicate(UUID businessId, String serviceName) {
        boolean exists = menuRepository.existsByBusinessIdAndServiceName(businessId, serviceName);

        if (exists) {
            log.error("중복 메뉴 생성 시도: businessId={}, serviceName={}",
                    businessId, serviceName);
            throw new MenuException(MenuErrorCode.MENU_ALREADY_EXISTS);
        }

        log.debug("메뉴 중복 체크 통과: businessId={}, serviceName={}",
                businessId, serviceName);
    }

    /**
     * Menu가 활성 상태인지 검증 (Reservation 에서 사용 해야함)
     *
     * @param menu 검증할 Menu 엔티티
     * @throws MenuException 비활성 상태일 경우
     */
    public void validateMenuActive(Menu menu) {
        if (!menu.getIsActive()) {
            log.warn("비활성 상태의 메뉴: menuId={}", menu.getId());
            throw new MenuException(MenuErrorCode.MENU_NOT_ACTIVE);
        }
    }

    /**
     * Menu 존재 및 Business 소속 동시 검증 (가장 많이 사용)
     *
     * @param menuId 검증할 Menu ID
     * @param businessId 확인할 Business ID
     * @return 조회된 Menu 엔티티
     * @throws MenuException Menu가 존재하지 않거나 해당 Business에 속하지 않는 경우
     */
    public Menu validateMenuOfBusiness(UUID menuId, UUID businessId) {
        Menu menu = validateMenuExists(menuId);
        validateMenuBelongsToBusiness(menu, businessId);
        return menu;
    }

    /**
     * Menu 생성 요청 검증
     * - RESERVATION_BASED일 때 durationMinutes 필수
     * - autoGenerateSlots=true일 때 slotSettings 필수
     *
     * @param request 생성 요청 DTO
     * @throws MenuException 검증 실패 시
     */
    public void validateMenuCreateRequest(MenuRequestDto.CreateUpdateMenu request) {
        // 1. RESERVATION_BASED 검증
        if (OrderType.RESERVATION_BASED.equals(request.orderType())) {
            if (request.durationMinutes() == null || request.durationMinutes() <= 0) {
                throw new MenuException(MenuErrorCode.DURATION_REQUIRED_FOR_RESERVATION);
            }
        }

        // 2. BookingSlot 자동 생성 설정 검증
        if (Boolean.TRUE.equals(request.autoGenerateSlots())) {
            if (request.slotSettings() == null) {
                throw new MenuException(MenuErrorCode.INVALID_SLOT_SETTINGS);
            }

            bookingSlotValidator.validateSlotSettings(request.slotSettings());
        }
    }

    /**
     * Menu 수정 요청 검증
     * 1. OrderType 변경 불가
     * 2. RESERVATION_BASED로 변경 시 durationMinutes 필수
     * 3. autoGenerateSlots=true일 때 slotSettings 필수
     *
     * @param request 수정 요청 DTO
     * @param menu 수정할 Menu 엔티티
     * @throws MenuException 검증 실패 시
     */
    public void validateMenuUpdateRequest(MenuRequestDto.CreateUpdateMenu request, Menu menu) {
        // 1. OrderType 변경 불가 검증
        validateOrderTypeNotChanged(request, menu);

        // 2. RESERVATION_BASED 검증 (변경하는 경우만)
        if (request.orderType() != null &&
                OrderType.RESERVATION_BASED.equals(request.orderType())) {
            if (request.durationMinutes() == null || request.durationMinutes() <= 0) {
                throw new MenuException(MenuErrorCode.DURATION_REQUIRED_FOR_RESERVATION);
            }
        }

        // 3. BookingSlot 자동 생성 설정 검증
        if (Boolean.TRUE.equals(request.autoGenerateSlots())) {
            if (request.slotSettings() == null) {
                throw new MenuException(MenuErrorCode.INVALID_SLOT_SETTINGS);
            }

            bookingSlotValidator.validateSlotSettings(request.slotSettings());
        }
    }

    /**
     * Menu에 활성 예약이 없는지 검증
     * - 활성 예약: PENDING, CONFIRMED
     * - 활성 예약이 있으면 삭제 불가
     */
    public void validateNoActiveReservations(UUID menuId) {
        boolean hasActiveReservations = reservationRepository
                .existsByMenuIdAndStatusIn(
                        menuId, List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
                );

        if (hasActiveReservations) {
            throw new MenuException(MenuErrorCode.MENU_HAS_ACTIVE_RESERVATIONS);
        }
    }

    /**
     * OrderType 변경 불가 검증
     * - RESERVATION_BASED ↔ ONDEMAND_BASED 변경 시 기존 데이터 불일치
     * - BookingSlot, Reservation 등 연관 데이터 처리 복잡
     *
     * @param request 수정 요청 DTO
     * @param menu 수정할 Menu 엔티티
     * @throws MenuException OrderType을 변경하려 할 경우
     */
    private void validateOrderTypeNotChanged(MenuRequestDto.CreateUpdateMenu request, Menu menu) {
        if (request.orderType() != null && !request.orderType().equals(menu.getOrderType())) {
            log.warn("OrderType 변경 시도 (불가): menuId={}, current={}, requested={}",
                    menu.getId(), menu.getOrderType(), request.orderType());

            throw new MenuException(MenuErrorCode.CANNOT_CHANGE_ORDER_TYPE);
        }
    }
}