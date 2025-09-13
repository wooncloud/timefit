package timefit.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import timefit.reservation.entity.ReservationTimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationTimeSlotRepository extends JpaRepository<ReservationTimeSlot, UUID>, ReservationTimeSlotRepositoryCustom {

    // 기본 JPA 메서드들
    Optional<ReservationTimeSlot> findByBusinessIdAndSlotDateAndStartTimeAndEndTime(
            UUID businessId, LocalDate slotDate, LocalTime startTime, LocalTime endTime);

    // 중복 슬롯 체크 (같은 업체, 날짜, 시작시간)
    boolean existsByBusinessIdAndSlotDateAndStartTime(UUID businessId, LocalDate slotDate, LocalTime startTime);

    // 업체별 특정 날짜 슬롯 개수
    @Query("SELECT COUNT(s) FROM ReservationTimeSlot s WHERE s.business.id = :businessId AND s.slotDate = :slotDate")
    Long countByBusinessIdAndSlotDate(@Param("businessId") UUID businessId, @Param("slotDate") LocalDate slotDate);

    // 업체의 특정 기간 슬롯 조회
    @Query("SELECT s FROM ReservationTimeSlot s WHERE s.business.id = :businessId AND s.slotDate BETWEEN :startDate AND :endDate ORDER BY s.slotDate ASC, s.startTime ASC")
    List<ReservationTimeSlot> findByBusinessIdAndDateRange(@Param("businessId") UUID businessId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 과거 슬롯 정리용 조회
    @Query("SELECT s FROM ReservationTimeSlot s WHERE s.slotDate < :date")
    List<ReservationTimeSlot> findPastSlots(@Param("date") LocalDate date);

    // 업체의 오늘 이후 활성 슬롯 조회
    @Query("SELECT s FROM ReservationTimeSlot s WHERE s.business.id = :businessId AND s.slotDate >= CURRENT_DATE AND s.isAvailable = true ORDER BY s.slotDate ASC, s.startTime ASC")
    List<ReservationTimeSlot> findUpcomingActiveSlotsByBusinessId(@Param("businessId") UUID businessId);
}