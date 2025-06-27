package timefit.service.repository;

import timefit.service.entity.Service;

import java.util.List;
import java.util.UUID;

public interface ServiceRepositoryCustom {

    List<Service> findByBusinessIdOrderByServiceName(UUID businessId);

    List<Service> findActiveServicesByBusinessId(UUID businessId);

    List<Service> findServicesByBusinessAndCategory(UUID businessId, String category);

    List<Service> findServicesByBusinessAndPriceRange(UUID businessId, Integer minPrice, Integer maxPrice);

    List<Service> findServicesByBusinessAndMaxDuration(UUID businessId, Integer maxDuration);

    List<Service> searchServicesByName(UUID businessId, String serviceName);
}