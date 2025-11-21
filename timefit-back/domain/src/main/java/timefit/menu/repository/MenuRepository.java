package timefit.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.util.List;
import java.util.UUID;


@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    /**
     * 특정 업체의 특정 주문 유형 메뉴 조회
     */
    List<Menu> findByBusinessIdAndOrderType(UUID businessId, OrderType orderType);


    // 예약형 메뉴들만 조회
    default List<Menu> findReservationBasedMenusByBusinessId(UUID businessId) {
        return findByBusinessIdAndOrderType(businessId, OrderType.RESERVATION_BASED);
    }

    // 주문형 메뉴들만 조회
    default List<Menu> findOnDemandBasedMenusByBusinessId(UUID businessId) {
        return findByBusinessIdAndOrderType(businessId, OrderType.ONDEMAND_BASED);
    }

    long countByBusinessCategoryIdAndIsActiveTrue(UUID categoryId);
}