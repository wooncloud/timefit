package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.service.validator.BusinessValidator;
import timefit.menu.dto.MenuResponseDto;
import timefit.menu.entity.Menu;
import timefit.menu.repository.MenuQueryRepository;
import timefit.menu.service.validator.MenuValidator;

import java.util.List;
import java.util.UUID;

// Menu 조회 전담 서비스
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuQueryService {

    private final MenuQueryRepository menuQueryRepository;
    private final BusinessValidator businessValidator;
    private final MenuValidator menuValidator;

    // 메뉴 목록 조회 (업체별)
    public MenuResponseDto.MenuList getMenuList(UUID businessId) {
        log.info("메뉴 목록 조회 시작: businessId={}", businessId);

        businessValidator.validateBusinessExists(businessId);
        List<Menu> menus = menuQueryRepository.findActiveMenusByBusinessId(businessId);

        log.info("메뉴 목록 조회 완료: businessId={}, count={}", businessId, menus.size());
        return MenuResponseDto.MenuList.of(menus);
    }

    // 메뉴 목록 조회 (검색/필터링)
    public MenuResponseDto.MenuList getMenuListWithFilters(
            UUID businessId,
            String serviceName,
            UUID businessCategoryId,
            Integer minPrice,
            Integer maxPrice,
            Boolean isActive) {

        log.info("메뉴 목록 필터링 조회: businessId={}, serviceName={}, categoryId={}, price={}-{}, isActive={}",
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);

        businessValidator.validateBusinessExists(businessId);

        List<Menu> menus = menuQueryRepository.findMenusWithFilters(
                businessId, serviceName, businessCategoryId,
                minPrice, maxPrice, isActive
        );

        log.info("메뉴 목록 필터링 완료: businessId={}, count={}", businessId, menus.size());
        return MenuResponseDto.MenuList.of(menus);
    }

    // 메뉴 상세 조회
    public MenuResponseDto.Menu getMenu(UUID businessId, UUID menuId) {
        log.info("메뉴 상세 조회 시작: businessId={}, menuId={}", businessId, menuId);

        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        log.info("메뉴 상세 조회 완료: menuId={}, serviceName={}",
                menu.getId(), menu.getServiceName());

        return MenuResponseDto.Menu.from(menu);
    }
}