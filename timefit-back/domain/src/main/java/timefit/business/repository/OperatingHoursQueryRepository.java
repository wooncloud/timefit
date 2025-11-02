package timefit.business.repository;

import timefit.business.entity.OperatingHours;

import java.util.List;
import java.util.UUID;

public interface OperatingHoursQueryRepository {

    // 업체의 영업일만 조회 (휴무일 제외)
    List<OperatingHours> findOpenDaysByBusinessId(UUID businessId);

    // 업체의 휴무일만 조회
    List<OperatingHours> findClosedDaysByBusinessId(UUID businessId);
}