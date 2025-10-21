package timefit.menu.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.menu.entity.Menu;
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
}