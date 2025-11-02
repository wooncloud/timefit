package timefit.business.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.QBusiness;
import timefit.business.entity.QBusinessCategory;
import timefit.business.entity.QUserBusinessRole;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BusinessRepositoryImpl implements BusinessRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QBusiness business = QBusiness.business;
    private final QBusinessCategory businessCategory = QBusinessCategory.businessCategory;
    private final QUserBusinessRole userBusinessRole = QUserBusinessRole.userBusinessRole;

    @Override
    public Page<Business> findByKeyword(String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(keyword)) {
            builder.or(business.businessName.containsIgnoreCase(keyword))
                    .or(business.address.containsIgnoreCase(keyword));
        }

        List<Business> businesses = queryFactory
                .selectFrom(business)
                .where(builder)
                .orderBy(business.businessName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(business.count())
                .from(business)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(businesses, pageable, total != null ? total : 0);
    }

    @Override
    public Page<Business> findByBusinessNameContaining(String businessName, Pageable pageable) {
        BooleanExpression condition = StringUtils.hasText(businessName)
                ? business.businessName.containsIgnoreCase(businessName)
                : null;

        List<Business> businesses = queryFactory
                .selectFrom(business)
                .where(condition)
                .orderBy(business.businessName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(business.count())
                .from(business)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(businesses, pageable, total != null ? total : 0);
    }

    /**
     * - 기존: business.businessTypes.contains(businessTypeCode)
     * - 변경: JOIN businessCategory WHERE businessCategory.businessType = ?
     */
    @Override
    public Page<Business> findByBusinessType(BusinessTypeCode businessTypeCode, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (businessTypeCode != null) {
            builder.and(businessCategory.businessType.eq(businessTypeCode))
                    .and(businessCategory.isActive.isTrue());
        }

        // BusinessCategory와 JOIN 하여 중복 제거 (distinct)
        List<Business> businesses = queryFactory
                .selectFrom(business)
                .distinct()
                .leftJoin(businessCategory).on(businessCategory.business.eq(business))
                .where(builder)
                .orderBy(business.businessName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(business.countDistinct())
                .from(business)
                .leftJoin(businessCategory).on(businessCategory.business.eq(business))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(businesses, pageable, total != null ? total : 0);
    }

    @Override
    public Page<Business> findByRegion(String region, Pageable pageable) {
        BooleanExpression condition = StringUtils.hasText(region)
                ? business.address.containsIgnoreCase(region)
                : null;

        List<Business> businesses = queryFactory
                .selectFrom(business)
                .where(condition)
                .orderBy(business.businessName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(business.count())
                .from(business)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(businesses, pageable, total != null ? total : 0);
    }

    /**
     * BusinessCategory JOIN 으로 복합 검색
     */
    @Override
    public Page<Business> findByBusinessNameAndType(String businessName, BusinessTypeCode businessTypeCode, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(businessName)) {
            builder.and(business.businessName.containsIgnoreCase(businessName));
        }

        if (businessTypeCode != null) {
            builder.and(businessCategory.businessType.eq(businessTypeCode))
                    .and(businessCategory.isActive.isTrue());
        }

        List<Business> businesses = queryFactory
                .selectFrom(business)
                .distinct()
                .leftJoin(businessCategory).on(businessCategory.business.eq(business))
                .where(builder)
                .orderBy(business.businessName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(business.countDistinct())
                .from(business)
                .leftJoin(businessCategory).on(businessCategory.business.eq(business))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(businesses, pageable, total != null ? total : 0);
    }

    /**
     * BusinessCategory JOIN 으로 복합 검색
     */
    @Override
    public Page<Business> findByBusinessTypeAndRegion(BusinessTypeCode businessTypeCode, String region, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (businessTypeCode != null) {
            builder.and(businessCategory.businessType.eq(businessTypeCode))
                    .and(businessCategory.isActive.isTrue());
        }

        if (StringUtils.hasText(region)) {
            builder.and(business.address.containsIgnoreCase(region));
        }

        List<Business> businesses = queryFactory
                .selectFrom(business)
                .distinct()
                .leftJoin(businessCategory).on(businessCategory.business.eq(business))
                .where(builder)
                .orderBy(business.businessName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(business.countDistinct())
                .from(business)
                .leftJoin(businessCategory).on(businessCategory.business.eq(business))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(businesses, pageable, total != null ? total : 0);
    }

    /**
     * BusinessCategory JOIN 으로 통합 검색
     */
    @Override
    public Page<Business> searchBusinesses(String keyword, BusinessTypeCode businessTypeCode, String region, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(business.isActive.isTrue());

        // 키워드 검색 (업체명 또는 주소에 포함)
        if (StringUtils.hasText(keyword)) {
            whereClause.and(
                    business.businessName.containsIgnoreCase(keyword)
                            .or(business.address.containsIgnoreCase(keyword))
            );
        }

        // 업종 검색 - BusinessCategory JOIN 으로 변경
        if (businessTypeCode != null) {
            whereClause.and(businessCategory.businessType.eq(businessTypeCode))
                    .and(businessCategory.isActive.isTrue());
        }

        // 지역 검색 (주소에 포함)
        if (StringUtils.hasText(region)) {
            whereClause.and(business.address.containsIgnoreCase(region));
        }

        // 페이징 쿼리 실행 (distinct로 중복 제거)
        List<Business> results = queryFactory
                .selectFrom(business)
                .distinct()
                .leftJoin(businessCategory).on(businessCategory.business.eq(business))
                .where(whereClause)
                .orderBy(business.businessName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(business.countDistinct())
                .from(business)
                .leftJoin(businessCategory).on(businessCategory.business.eq(business))
                .where(whereClause)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }

    /**
     * BusinessCategory JOIN 으로 업종별 카운트
     */
    @Override
    public long countByBusinessType(BusinessTypeCode businessTypeCode) {
        if (businessTypeCode == null) {
            return 0;
        }

        Long count = queryFactory
                .select(business.countDistinct())
                .from(business)
                .leftJoin(businessCategory).on(businessCategory.business.eq(business))
                .where(
                        businessCategory.businessType.eq(businessTypeCode)
                                .and(businessCategory.isActive.isTrue())
                )
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public long countByRegion(String region) {
        Long count = queryFactory
                .select(business.count())
                .from(business)
                .where(StringUtils.hasText(region)
                        ? business.address.containsIgnoreCase(region)
                        : null)
                .fetchOne();
        return count != null ? count : 0;
    }

    @Override
    public List<Business> findRecommendedBusinesses(int limit) {
        return queryFactory
                .selectFrom(business)
                .where(business.isActive.isTrue())
                .orderBy(business.businessName.asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Business> findBusinessesByUserId(UUID userId) {
        return queryFactory
                .select(business)
                .from(userBusinessRole)
                .join(userBusinessRole.business, business)
                .where(
                        userBusinessRole.user.id.eq(userId)
                                .and(userBusinessRole.isActive.eq(true))
                )
                .orderBy(business.businessName.asc())
                .fetch();
    }
}