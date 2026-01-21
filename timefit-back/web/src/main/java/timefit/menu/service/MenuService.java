package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.dto.MenuResponseDto;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuQueryService menuQueryService;
    private final MenuCommandService menuCommandService;

    // 메뉴 목록 조회 (업체별)
    public MenuResponseDto.MenuList getMenuList(UUID businessId) {
        return menuQueryService.getMenuList(businessId);
    }

    // 메뉴 상세 조회
    public MenuResponseDto.Menu getMenu(UUID businessId, UUID menuId) {
        return menuQueryService.getMenu(businessId, menuId);
    }

    // 메뉴 목록 조회 (검색/필터링)
    public MenuResponseDto.MenuList getMenuListWithFilters(
            UUID businessId,
            String serviceName,
            UUID businessCategoryId,
            Integer minPrice,
            Integer maxPrice,
            Boolean isActive) {

        return menuQueryService.getMenuListWithFilters(
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);
    }

    // 메뉴 생성
    @Transactional
    public MenuResponseDto.Menu createMenu(
            UUID businessId,
            MenuRequestDto.CreateUpdateMenu request,
            UUID currentUserId) {

        return menuCommandService.createMenu(businessId, request, currentUserId);
    }

    // 메뉴 수정
    @Transactional
    public MenuResponseDto.Menu updateMenu(
            UUID businessId,
            UUID menuId,
            MenuRequestDto.CreateUpdateMenu request,
            UUID currentUserId) {

        return menuCommandService.updateMenu(businessId, menuId, request, currentUserId);
    }

    // 메뉴 활성/비활성 토글
    @Transactional
    public MenuResponseDto.Menu toggleMenuActive(
            UUID businessId,
            UUID menuId,
            UUID currentUserId) {

        return menuCommandService.toggleMenuActive(businessId, menuId, currentUserId);
    }

    // 메뉴 삭제
    @Transactional
    public MenuResponseDto.DeleteResult deleteMenu(UUID businessId, UUID menuId, UUID currentUserId) {
        return menuCommandService.deleteMenu(businessId, menuId, currentUserId);
    }
}