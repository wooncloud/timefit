package timefit.business.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import timefit.business.entity.BusinessOperatingHours;
import timefit.business.entity.DayOfWeek;
import timefit.business.entity.QBusinessOperatingHours;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BusinessOperatingHoursRepositoryImpl implements BusinessOperatingHoursRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QBusinessOperatingHours businessOperatingHours = QBusinessOperatingHours.businessOperatingHours;

    @Override
    public List<BusinessOperatingHours> findByBusinessIdOrderByDayOfWeek(UUID businessId) {
        return queryFactory
                .selectFrom(businessOperatingHours)
                .where(businessOperatingHours.business.id.eq(businessId))
                .orderBy(businessOperatingHours.dayOfWeek.asc())
                .fetch();
    }

    @Override
    public List<BusinessOperatingHours> findOpenDaysByBusinessId(UUID businessId) {
        return queryFactory
                .selectFrom(businessOperatingHours)
                .where(
                        businessOperatingHours.business.id.eq(businessId)
                                .and(businessOperatingHours.isClosed.eq(false))
                )
                .orderBy(businessOperatingHours.dayOfWeek.asc())
                .fetch();
    }

    @Override
    public List<BusinessOperatingHours> findClosedDaysByBusinessId(UUID businessId) {
        return queryFactory
                .selectFrom(businessOperatingHours)
                .where(
                        businessOperatingHours.business.id.eq(businessId)
                                .and(businessOperatingHours.isClosed.eq(true))
                )
                .orderBy(businessOperatingHours.dayOfWeek.asc())
                .fetch();
    }
}