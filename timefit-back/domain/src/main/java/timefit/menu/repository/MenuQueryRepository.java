package timefit.menu.repository;

import timefit.business.entity.BusinessTypeCode;
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

    // 카테고리별 메뉴 조회 (업체 내)
    // 수정: String → BusinessTypeCode enum
    List<Menu> findMenusByCategory(UUID businessId, BusinessTypeCode category);
}