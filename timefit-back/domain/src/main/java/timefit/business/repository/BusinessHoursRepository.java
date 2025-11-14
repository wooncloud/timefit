package timefit.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.BusinessHours;
import timefit.common.entity.DayOfWeek;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHours, UUID> {

    // 업체의 모든 영업시간 조회 (요일 순)
    List<BusinessHours> findByBusinessIdOrderByDayOfWeekAsc(UUID businessId);

    // 특정 요일의 영업시간 조회
    Optional<BusinessHours> findByBusinessIdAndDayOfWeek(UUID businessId, DayOfWeek dayOfWeek);

    // 업체의 모든 영업시간 삭제
    void deleteByBusinessId(UUID businessId);
}