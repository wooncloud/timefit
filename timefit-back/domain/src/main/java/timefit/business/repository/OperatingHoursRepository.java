package timefit.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.OperatingHours;
import timefit.common.entity.DayOfWeek;

import java.util.List;
import java.util.UUID;

@Repository
public interface OperatingHoursRepository extends JpaRepository<OperatingHours, UUID> {

    /**
     * 특정 업체의 특정 요일에 대한 모든 영업시간을 sequence 순서로 조회
     * 휴게시간 지원:
     * 같은 요일에 여러 영업시간대가 있을 수 있음 (예: 09:00-12:00, 13:00-18:00)
     *
     * @param businessId 업체 ID
     * @param dayOfWeek 요일
     * @return 해당 요일의 모든 영업시간 (sequence 오름차순)
     */
    List<OperatingHours> findByBusinessIdAndDayOfWeekOrderBySequenceAsc(
            UUID businessId,
            DayOfWeek dayOfWeek
    );

    // 업체의 모든 영업시간 조회 (요일 순서)
    List<OperatingHours> findByBusinessIdOrderByDayOfWeekAsc(UUID businessId);

    // 업체의 모든 영업시간 삭제
    void deleteByBusinessId(UUID businessId);
}