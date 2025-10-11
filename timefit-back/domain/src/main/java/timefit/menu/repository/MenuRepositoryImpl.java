package timefit.menu.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import timefit.menu.entity.Menu;
import timefit.menu.entity.QService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QService service = QService.service;

    @Override
    public List<Menu> findByBusinessIdOrderByServiceName(UUID businessId) {
        return queryFactory
                .selectFrom(service)
                .where(service.business.id.eq(businessId))
                .orderBy(service.serviceName.asc())
                .fetch();
    }

    @Override
    public List<Menu> findActiveServicesByBusinessId(UUID businessId) {
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
    public List<Menu> findServicesByBusinessAndCategory(UUID businessId, String category) {
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
    public List<Menu> findServicesByBusinessAndPriceRange(UUID businessId, Integer minPrice, Integer maxPrice) {
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
    public List<Menu> findServicesByBusinessAndMaxDuration(UUID businessId, Integer maxDuration) {
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
    public List<Menu> searchServicesByName(UUID businessId, String serviceName) {
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