package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.menu.dto.MenuRequest;
import timefit.menu.dto.MenuResponse;
import timefit.menu.dto.MenuListResponse;

import java.util.UUID;

/**
 * MenuService Facade 으로 분리.
 * - 조회 작업 -> MenuQueryService
 * - CUD 작업 -> MenuCommandService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuQueryService menuQueryService;
    private final MenuCommandService menuCommandService;

    /**
     * 메뉴 목록 조회 (업체별)
     * 권한: 불필요 (공개 API)
     */
    public MenuListResponse getMenuList(UUID businessId) {
        return menuQueryService.getMenuList(businessId);
    }

    /**
     * 메뉴 상세 조회
     * 권한: 불필요 (공개 API)
     */
    public MenuResponse getMenu(UUID businessId, UUID menuId) {
        return menuQueryService.getMenu(businessId, menuId);
    }

    /**
     * 메뉴 목록 조회 (검색/필터링)
     * 권한: 불필요 (공개 API)
     */
    public MenuListResponse getMenuListWithFilters(
            UUID businessId,
            String serviceName,
            UUID businessCategoryId,
            Integer minPrice,
            Integer maxPrice,
            Boolean isActive) {

        return menuQueryService.getMenuListWithFilters(
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);
    }

    /**
     * 메뉴 생성
     * 권한: OWNER, MANAGER
     */
    @Transactional
    public MenuResponse createMenu(UUID businessId, MenuRequest.CreateUpdateMenu request, UUID currentUserId) {
        return menuCommandService.createMenu(businessId, request, currentUserId);
    }

    /**
     * 메뉴 수정
     * 권한: OWNER, MANAGER
     */
    @Transactional
    public MenuResponse updateMenu(UUID businessId, UUID menuId, MenuRequest.CreateUpdateMenu request, UUID currentUserId) {
        return menuCommandService.updateMenu(businessId, menuId, request, currentUserId);
    }

    /**
     * 메뉴 활성/비활성 토글
     * 권한: OWNER, MANAGER
     */
    @Transactional
    public MenuResponse toggleMenuActive(UUID businessId, UUID menuId, UUID currentUserId) {
        return menuCommandService.toggleMenuActive(businessId, menuId, currentUserId);
    }

    /**
     * 메뉴 삭제 (비활성화)
     * 권한: OWNER, MANAGER
     */
    @Transactional
    public void deleteMenu(UUID businessId, UUID menuId, UUID currentUserId) {
        menuCommandService.deleteMenu(businessId, menuId, currentUserId);
    }
}