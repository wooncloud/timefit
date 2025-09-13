package timefit.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.BusinessOperatingHours;
import timefit.business.entity.DayOfWeek;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessOperatingHoursRepository extends JpaRepository<BusinessOperatingHours, UUID>, BusinessOperatingHoursRepositoryCustom {

    // 기본 JPA 메서드들
    Optional<BusinessOperatingHours> findByBusinessIdAndDayOfWeek(UUID businessId, DayOfWeek dayOfWeek);

    boolean existsByBusinessId(UUID businessId);
}