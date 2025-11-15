package timefit.business.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
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

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BusinessRepositoryImpl implements BusinessRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QBusiness business = QBusiness.business;
    private final QBusinessCategory businessCategory = QBusinessCategory.businessCategory;

    @Override
    public Page<Business> searchBusinesses(
            String keyword,
            BusinessTypeCode businessTypeCode,
            String region,
            Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // 키워드 검색 (업체명 or 주소)
        if (StringUtils.hasText(keyword)) {
            builder.and(
                    business.businessName.containsIgnoreCase(keyword)
                            .or(business.address.containsIgnoreCase(keyword))
            );
        }

        // 업종 검색 (BusinessCategory JOIN)
        if (businessTypeCode != null) {
            builder.and(
                    JPAExpressions
                            .selectOne()
                            .from(businessCategory)
                            .where(
                                    businessCategory.business.eq(business),
                                    businessCategory.businessType.eq(businessTypeCode),
                                    businessCategory.isActive.isTrue()
                            )
                            .exists()
            );
        }

        // 지역 검색
        if (StringUtils.hasText(region)) {
            builder.and(business.address.containsIgnoreCase(region));
        }

        List<Business> businesses = queryFactory
                .selectFrom(business)
                .where(builder, business.isActive.isTrue())
                .orderBy(business.businessName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(business.count())
                .from(business)
                .where(builder, business.isActive.isTrue())
                .fetchOne();

        return new PageImpl<>(businesses, pageable, total != null ? total : 0);
    }
}