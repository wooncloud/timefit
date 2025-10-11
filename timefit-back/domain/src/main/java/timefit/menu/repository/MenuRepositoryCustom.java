package timefit.menu.repository;

import timefit.menu.entity.Menu;

import java.util.List;
import java.util.UUID;

public interface MenuRepositoryCustom {

    List<Menu> findByBusinessIdOrderByServiceName(UUID businessId);

    List<Menu> findActiveServicesByBusinessId(UUID businessId);

    List<Menu> findServicesByBusinessAndCategory(UUID businessId, String category);

    List<Menu> findServicesByBusinessAndPriceRange(UUID businessId, Integer minPrice, Integer maxPrice);

    List<Menu> findServicesByBusinessAndMaxDuration(UUID businessId, Integer maxDuration);

    List<Menu> searchServicesByName(UUID businessId, String serviceName);
}