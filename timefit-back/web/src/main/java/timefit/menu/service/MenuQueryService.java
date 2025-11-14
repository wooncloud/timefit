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

        // 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 활성화된 메뉴만 조회
        List<Menu> menus = menuQueryRepository.findActiveMenusByBusinessId(businessId);

        log.info("메뉴 목록 조회 완료: businessId={}, count={}", businessId, menus.size());

        return MenuListResponse.of(menus);
    }

    /**
     * 메뉴 상세 조회
     *
     * @param businessId 업체 ID
     * @param menuId 메뉴 ID
     * @return 메뉴 상세 정보
     */
    public MenuResponse getMenu(UUID businessId, UUID menuId) {
        log.info("메뉴 상세 조회 시작: businessId={}, menuId={}", businessId, menuId);

        // Menu 조회 및 검증
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        log.info("메뉴 상세 조회 완료: menuId={}, serviceName={}",
                menu.getId(), menu.getServiceName());

        return MenuResponse.from(menu);
    }
}