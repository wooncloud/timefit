package timefit.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.util.List;
import java.util.UUID;


@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    List<Menu> findByBusinessIdOrderByServiceNameAsc(UUID businessId);

    List<Menu> findByBusinessIdAndOrderType(UUID businessId, OrderType orderType);

    // 카테고리별 메뉴 조회
    List<Menu> findByBusinessIdAndCategory(UUID businessId, BusinessTypeCode category);

    // 예약형 메뉴들만 조회
    default List<Menu> findReservationBasedMenusByBusinessId(UUID businessId) {
        return findByBusinessIdAndOrderType(businessId, OrderType.RESERVATION_BASED);
    }

    // 주문형 메뉴들만 조회
    default List<Menu> findOnDemandBasedMenusByBusinessId(UUID businessId) {
        return findByBusinessIdAndOrderType(businessId, OrderType.ONDEMAND_BASED);
    }
}