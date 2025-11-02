package timefit.business.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import timefit.business.entity.OperatingHours;
import timefit.business.entity.QOperatingHours;

import java.util.List;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class OperatingHoursQueryRepositoryImpl implements OperatingHoursQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QOperatingHours operatingHours = QOperatingHours.operatingHours;

    @Override
    public List<OperatingHours> findOpenDaysByBusinessId(UUID businessId) {
        return queryFactory
                .selectFrom(operatingHours)
                .where(
                        operatingHours.business.id.eq(businessId)
                                .and(operatingHours.isClosed.eq(false))
                )
                .orderBy(operatingHours.dayOfWeek.asc(), operatingHours.sequence.asc())
                .fetch();
    }

    @Override
    public List<OperatingHours> findClosedDaysByBusinessId(UUID businessId) {
        return queryFactory
                .selectFrom(operatingHours)
                .where(
                        operatingHours.business.id.eq(businessId)
                                .and(operatingHours.isClosed.eq(true))
                )
                .orderBy(operatingHours.dayOfWeek.asc())
                .fetch();
    }
}