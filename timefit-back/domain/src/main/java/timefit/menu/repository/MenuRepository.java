package timefit.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.util.List;
import java.util.UUID;


@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    // 특정 업체의 특정 주문 유형 메뉴 조회
    List<Menu> findByBusinessIdAndOrderType(UUID businessId, OrderType orderType);


    // 예약형 메뉴들만 조회
    default List<Menu> findReservationBasedMenusByBusinessId(UUID businessId) {
        return findByBusinessIdAndOrderType(businessId, OrderType.RESERVATION_BASED);
    }

    // 주문형 메뉴들만 조회
    default List<Menu> findOnDemandBasedMenusByBusinessId(UUID businessId) {
        return findByBusinessIdAndOrderType(businessId, OrderType.ONDEMAND_BASED);
    }

    /**
     * Menu 중복 체크
     * @param businessId 업체 ID
     * @param serviceName 서비스명
     * @return true: 중복, false: 중복 아님
     */
    boolean existsByBusinessIdAndServiceName(UUID businessId, String serviceName);

    // Business의 활성 메뉴 존재 여부
    boolean existsByBusinessIdAndIsActiveTrue(UUID businessId);

    long countByBusinessCategoryIdAndIsActiveTrue(UUID categoryId);
}