package timefit.menu.repository;

import timefit.menu.entity.Menu;

import java.util.List;
import java.util.UUID;

public interface MenuQueryRepository {

    // 업체의 활성 메뉴만 조회
    List<Menu> findActiveMenusByBusinessId(UUID businessId);

    // 메뉴명으로 검색 (업체 내, 대소문자 무시)
    List<Menu> searchMenusByName(UUID businessId, String serviceName);

    // 가격 범위로 검색 (업체 내)
    List<Menu> findMenusByPriceRange(UUID businessId, Integer minPrice, Integer maxPrice);

    /**
     * BusinessCategory별 메뉴 조회
     * - 특정 업체의 특정 카테고리에 속한 메뉴 조회
     *
     * @param businessId 업체 ID
     * @param businessCategoryId BusinessCategory ID
     * @return 해당 카테고리의 메뉴 목록
     */
    List<Menu> findMenusByBusinessCategory(UUID businessId, UUID businessCategoryId);
}