package timefit.menu.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.service.validator.BookingSlotValidator;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.menu.dto.MenuRequest;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;
import timefit.menu.repository.MenuRepository;

import java.util.UUID;

/**
 * Menu 도메인 검증 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuValidator {

    private final MenuRepository menuRepository;
    private final BookingSlotValidator bookingSlotValidator;

    /**
     * Menu 존재 여부 검증 및 조회
     *
     * @param menuId 검증할 메뉴 ID
     * @return 조회된 Menu 엔티티
     * @throws MenuException 메뉴가 존재하지 않을 경우
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
     * @param businessId 업체 ID
     * @throws MenuException Menu가 해당 Business에 속하지 않을 경우
     */
    public void validateMenuBelongsToBusiness(Menu menu, UUID businessId) {
        if (!menu.getBusiness().getId().equals(businessId)) {
            log.warn("메뉴가 해당 업체에 속하지 않음: menuId={}, businessId={}",
                    menu.getId(), businessId);
            throw new MenuException(MenuErrorCode.MENU_ACCESS_DENIED);
        }
    }

    /**
     * Menu가 활성 상태인지 검증
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
     * @param menuId 검증할 메뉴 ID
     * @param businessId 업체 ID
     * @return 조회된 Menu 엔티티
     * @throws MenuException 메뉴가 없거나 해당 업체에 속하지 않을 경우
     */
    public Menu validateMenuOfBusiness(UUID menuId, UUID businessId) {
        Menu menu = validateMenuExists(menuId);
        validateMenuBelongsToBusiness(menu, businessId);
        return menu;
    }

    /**
     * Menu 존재, Business 소속, 활성 상태 모두 검증
     *
     * @param menuId 검증할 메뉴 ID
     * @param businessId 업체 ID
     * @return 조회된 활성 상태의 Menu 엔티티
     */
    public Menu validateActiveMenuOfBusiness(UUID menuId, UUID businessId) {
        Menu menu = validateMenuOfBusiness(menuId, businessId);
        validateMenuActive(menu);
        return menu;
    }



    //   ---------- Menu , BookingSlot 생성 관련

    /**
     * Menu 생성 요청 검증
     * - RESERVATION_BASED일 때 durationMinutes 필수
     * - autoGenerateSlots=true일 때 slotSettings 필수
     * - BookingSlot 설정 검증은 BookingSlotValidator로 위임
     */
    public void validateMenuCreateRequest(MenuRequest.CreateUpdateMenu request) {
        // 1. RESERVATION_BASED 검증 (Menu 도메인 책임)
        if (OrderType.RESERVATION_BASED.equals(request.orderType())) {
            if (request.durationMinutes() == null || request.durationMinutes() <= 0) {
                throw new MenuException(
                        MenuErrorCode.DURATION_REQUIRED_FOR_RESERVATION,
                        "예약형 서비스는 소요 시간(durationMinutes)이 필수입니다"
                );
            }
        }

        // 2. BookingSlot 자동 생성 설정 검증
        if (Boolean.TRUE.equals(request.autoGenerateSlots())) {
            if (request.slotSettings() == null) {
                throw new MenuException(
                        MenuErrorCode.INVALID_SLOT_SETTINGS,
                        "슬롯 자동 생성 시 슬롯 설정(slotSettings)이 필요합니다"
                );
            }

            bookingSlotValidator.validateSlotSettings(request.slotSettings());
        }
    }

    /**
     * Menu 수정 요청 검증
     * - RESERVATION_BASED로 변경 시 durationMinutes 필수
     * - autoGenerateSlots=true일 때 slotSettings 필수
     */
    public void validateMenuUpdateRequest(MenuRequest.CreateUpdateMenu request) {
        // 1. RESERVATION_BASED 검증 (변경하는 경우만)
        if (request.orderType() != null &&
                OrderType.RESERVATION_BASED.equals(request.orderType())) {
            if (request.durationMinutes() == null || request.durationMinutes() <= 0) {
                throw new MenuException(
                        MenuErrorCode.DURATION_REQUIRED_FOR_RESERVATION,
                        "예약형 서비스는 소요 시간(durationMinutes)이 필수입니다"
                );
            }
        }

        // 2. BookingSlot 자동 생성 설정 검증
        if (Boolean.TRUE.equals(request.autoGenerateSlots())) {
            if (request.slotSettings() == null) {
                throw new MenuException(
                        MenuErrorCode.INVALID_SLOT_SETTINGS,
                        "슬롯 자동 생성 시 슬롯 설정(slotSettings)이 필요합니다"
                );
            }

            // BookingSlot 관련 검증은 BookingSlotValidator로 위임
            bookingSlotValidator.validateSlotSettings(request.slotSettings());
        }
    }
}