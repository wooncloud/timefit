package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.service.validator.BusinessValidator;
import timefit.menu.dto.MenuListResponse;
import timefit.menu.dto.MenuResponse;
import timefit.menu.entity.Menu;
import timefit.menu.repository.MenuQueryRepository;
import timefit.menu.service.validator.MenuValidator;

import java.util.List;
import java.util.UUID;

/**
 * Menu 조회 전담 서비스
 * - MenuResponse, MenuListResponse가 BusinessCategory 정보 포함
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuQueryService {

    private final MenuQueryRepository menuQueryRepository;
    private final BusinessValidator businessValidator;
    private final MenuValidator menuValidator;

    /**
     * 메뉴 목록 조회 (업체별)
     *
     * @param businessId 업체 ID
     * @return 활성화된 메뉴 목록
     */
    public MenuListResponse getMenuList(UUID businessId) {
        log.info("메뉴 목록 조회 시작: businessId={}", businessId);

        businessValidator.validateBusinessExists(businessId);
        List<Menu> menus = menuQueryRepository.findActiveMenusByBusinessId(businessId);

        log.info("메뉴 목록 조회 완료: businessId={}, count={}", businessId, menus.size());
        return MenuListResponse.of(menus);
    }

    /**
     * 메뉴 목록 조회 (검색/필터링)
     *
     * @param businessId 업체 ID
     * @param serviceName 서비스명 검색 (부분 일치)
     * @param businessCategoryId 카테고리 필터
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @param isActive 활성 상태 (null 이면 전체)
     * @return 필터링된 메뉴 목록
     */
    public MenuListResponse getMenuListWithFilters(
            UUID businessId,
            String serviceName,
            UUID businessCategoryId,
            Integer minPrice,
            Integer maxPrice,
            Boolean isActive) {

        log.info("메뉴 목록 필터링 조회: businessId={}, serviceName={}, categoryId={}, price={}-{}, isActive={}",
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);

        // 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 검색
        List<Menu> menus = menuQueryRepository.findMenusWithFilters(
                businessId, serviceName, businessCategoryId,
                minPrice, maxPrice, isActive
        );

        log.info("메뉴 목록 필터링 완료: businessId={}, count={}", businessId, menus.size());
        return MenuListResponse.of(menus);
    }

    /**
     * 메뉴 상세 조회
     */
    public MenuResponse getMenu(UUID businessId, UUID menuId) {
        log.info("메뉴 상세 조회 시작: businessId={}, menuId={}", businessId, menuId);

        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        log.info("메뉴 상세 조회 완료: menuId={}, serviceName={}",
                menu.getId(), menu.getServiceName());

        return MenuResponse.from(menu);
    }
}