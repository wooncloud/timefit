package timefit.menu.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
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
    public List<Menu> findMenusWithFilters(
            UUID businessId,
            String serviceName,
            UUID businessCategoryId,
            Integer minPrice,
            Integer maxPrice,
            Boolean isActive) {

        return queryFactory
                .selectFrom(menu)
                .where(
                        menu.business.id.eq(businessId),
                        serviceNameContains(serviceName),
                        businessCategoryIdEq(businessCategoryId),
                        priceGoe(minPrice),
                        priceLoe(maxPrice),
                        isActiveEq(isActive)
                )
                .orderBy(menu.serviceName.asc())
                .fetch();
    }

    private BooleanExpression serviceNameContains(String serviceName) {
        return serviceName != null ?
                menu.serviceName.containsIgnoreCase(serviceName) : null;
    }

    private BooleanExpression businessCategoryIdEq(UUID categoryId) {
        return categoryId != null ?
                menu.businessCategory.id.eq(categoryId) : null;
    }

    private BooleanExpression priceGoe(Integer minPrice) {
        return minPrice != null ? menu.price.goe(minPrice) : null;
    }

    private BooleanExpression priceLoe(Integer maxPrice) {
        return maxPrice != null ? menu.price.loe(maxPrice) : null;
    }

    private BooleanExpression isActiveEq(Boolean isActive) {
        return isActive != null ? menu.isActive.eq(isActive) : null;
    }
}