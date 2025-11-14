package timefit.menu.repository;

import timefit.menu.entity.Menu;

import java.util.List;
import java.util.UUID;

public interface MenuQueryRepository {

    /*
    *   업체의 활성 메뉴만 조회
    *   BooleanExpression은 null 이면 자동으로 조건 무시합니다.
    *   우선 where 절에 여러 필터 조건 (category, name, price, active) 묶어서 보내는 방식으로 작성함.
    */


    List<Menu> findActiveMenusByBusinessId(UUID businessId);

    // 종합 검색 필터 (모든 조건 AND 결합)
    List<Menu> findMenusWithFilters(
            UUID businessId, String serviceName,
            UUID businessCategoryId,
            Integer minPrice, Integer maxPrice,
            Boolean isActive
    );
}