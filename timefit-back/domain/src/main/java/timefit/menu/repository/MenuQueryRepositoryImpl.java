package timefit.menu.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.QMenu;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MenuQueryRepositoryImpl implements MenuQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QMenu menu = QMenu.menu;

    @Override
    public List<Menu> findActiveMenusByBusinessId(UUID businessId) {
        return queryFactory
                .selectFrom(menu)
                .where(
                        menu.business.id.eq(businessId)
                                .and(menu.isActive.eq(true))
                )
                .orderBy(menu.serviceName.asc())
                .fetch();
    }

    @Override
    public List<Menu> searchMenusByName(UUID businessId, String serviceName) {
        return queryFactory
                .selectFrom(menu)
                .where(
                        menu.business.id.eq(businessId)
                                .and(StringUtils.hasText(serviceName) ?
                                        menu.serviceName.containsIgnoreCase(serviceName) : null)
                )
                .orderBy(menu.serviceName.asc())
                .fetch();
    }

    @Override
    public List<Menu> findMenusByPriceRange(UUID businessId, Integer minPrice, Integer maxPrice) {
        return queryFactory
                .selectFrom(menu)
                .where(
                        menu.business.id.eq(businessId)
                                .and(menu.price.between(minPrice, maxPrice))
                )
                .orderBy(menu.price.asc())
                .fetch();
    }

    @Override
    public List<Menu> findMenusByCategory(UUID businessId, BusinessTypeCode category) {
        return queryFactory
                .selectFrom(menu)
                .where(
                        menu.business.id.eq(businessId)
                                .and(category != null ? menu.category.eq(category) : null)
                )
                .orderBy(menu.serviceName.asc())
                .fetch();
    }
}