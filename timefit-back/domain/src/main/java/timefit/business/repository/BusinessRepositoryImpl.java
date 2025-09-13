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
import timefit.business.entity.QBusiness;
import timefit.business.entity.QUserBusinessRole;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BusinessRepositoryImpl implements BusinessRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QBusiness business = QBusiness.business;
    private final QUserBusinessRole userBusinessRole = QUserBusinessRole.userBusinessRole;

    @Override
    public Page<Business> findByKeyword(String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(keyword)) {
            builder.or(business.businessName.containsIgnoreCase(keyword))
                    .or(business.businessType.containsIgnoreCase(keyword))
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

    @Override
    public Page<Business> findByBusinessType(String businessType, Pageable pageable) {
        BooleanExpression condition = StringUtils.hasText(businessType)
                ? business.businessType.eq(businessType)
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

    @Override
    public Page<Business> findByBusinessNameAndType(String businessName, String businessType, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(businessName)) {
            builder.and(business.businessName.containsIgnoreCase(businessName));
        }
        if (StringUtils.hasText(businessType)) {
            builder.and(business.businessType.eq(businessType));
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
    public Page<Business> findByBusinessTypeAndRegion(String businessType, String region, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(businessType)) {
            builder.and(business.businessType.eq(businessType));
        }
        if (StringUtils.hasText(region)) {
            builder.and(business.address.containsIgnoreCase(region));
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
    public Page<Business> searchBusinesses(String keyword, String businessType, String region, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // 활성화된 업체만 조회 (공개 검색용 - 추가된 조건)
        builder.and(business.isActive.eq(true));

        // 키워드 검색 (업체명, 업종, 주소)
        if (StringUtils.hasText(keyword)) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(business.businessName.containsIgnoreCase(keyword))
                    .or(business.businessType.containsIgnoreCase(keyword))
                    .or(business.address.containsIgnoreCase(keyword));
            builder.and(keywordBuilder);
        }

        // 업종 필터
        if (StringUtils.hasText(businessType)) {
            builder.and(business.businessType.eq(businessType));
        }

        // 지역 필터
        if (StringUtils.hasText(region)) {
            builder.and(business.address.containsIgnoreCase(region));
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
    public long countByBusinessType(String businessType) {
        Long count = queryFactory
                .select(business.count())
                .from(business)
                .where(StringUtils.hasText(businessType)
                        ? business.businessType.eq(businessType)
                        : null)
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
                .orderBy(business.businessName.asc()) // 추후 평점이나 인기도 기준으로 변경 가능
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