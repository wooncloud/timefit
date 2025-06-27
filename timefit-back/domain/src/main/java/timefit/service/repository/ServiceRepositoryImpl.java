package timefit.service.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import timefit.service.entity.QService;
import timefit.service.entity.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QService service = QService.service;

    @Override
    public List<Service> findByBusinessIdOrderByServiceName(UUID businessId) {
        return queryFactory
                .selectFrom(service)
                .where(service.business.id.eq(businessId))
                .orderBy(service.serviceName.asc())
                .fetch();
    }

    @Override
    public List<Service> findActiveServicesByBusinessId(UUID businessId) {
        return queryFactory
                .selectFrom(service)
                .where(
                        service.business.id.eq(businessId)
                                .and(service.isActive.eq(true))
                )
                .orderBy(service.serviceName.asc())
                .fetch();
    }

    @Override
    public List<Service> findServicesByBusinessAndCategory(UUID businessId, String category) {
        return queryFactory
                .selectFrom(service)
                .where(
                        service.business.id.eq(businessId)
                                .and(service.category.eq(category))
                )
                .orderBy(service.serviceName.asc())
                .fetch();
    }

    @Override
    public List<Service> findServicesByBusinessAndPriceRange(UUID businessId, Integer minPrice, Integer maxPrice) {
        return queryFactory
                .selectFrom(service)
                .where(
                        service.business.id.eq(businessId)
                                .and(service.price.between(minPrice, maxPrice))
                )
                .orderBy(service.price.asc())
                .fetch();
    }

    @Override
    public List<Service> findServicesByBusinessAndMaxDuration(UUID businessId, Integer maxDuration) {
        return queryFactory
                .selectFrom(service)
                .where(
                        service.business.id.eq(businessId)
                                .and(service.durationMinutes.loe(maxDuration))
                )
                .orderBy(service.durationMinutes.asc())
                .fetch();
    }

    @Override
    public List<Service> searchServicesByName(UUID businessId, String serviceName) {
        return queryFactory
                .selectFrom(service)
                .where(
                        service.business.id.eq(businessId)
                                .and(service.serviceName.containsIgnoreCase(serviceName))
                )
                .orderBy(service.serviceName.asc())
                .fetch();
    }
}