package timefit.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.OperatingHours;
import timefit.business.entity.DayOfWeek;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperatingHoursRepository extends JpaRepository<OperatingHours, UUID> {

    Optional<OperatingHours> findByBusinessIdAndDayOfWeek(UUID businessId, DayOfWeek dayOfWeek);

    List<OperatingHours> findByBusinessIdOrderByDayOfWeekAsc(UUID businessId);

    boolean existsByBusinessId(UUID businessId);

    void deleteByBusinessId(UUID businessId);
}