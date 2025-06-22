package timefit.business.repository;

import timefit.business.entity.BusinessOperatingHours;
import timefit.business.entity.DayOfWeek;

import java.util.List;
import java.util.UUID;

public interface BusinessOperatingHoursRepositoryCustom {

    List<BusinessOperatingHours> findByBusinessIdOrderByDayOfWeek(UUID businessId);

    List<BusinessOperatingHours> findOpenDaysByBusinessId(UUID businessId);

    List<BusinessOperatingHours> findClosedDaysByBusinessId(UUID businessId);
}