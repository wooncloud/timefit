package timefit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.booking.entity.BookingSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingSlotRepository extends JpaRepository<BookingSlot, UUID> {

    /**
     * 특정 날짜, 시간의 슬롯 존재 여부 확인 (중복 체크용)
     * @deprecated business_id 기준은 더 이상 사용하지 않음.
     */
    @Deprecated
    boolean existsByBusinessIdAndSlotDateAndStartTime(
            UUID businessId,
            LocalDate slotDate, LocalTime startTime
    );

    // 특정 날짜, 시간의 슬롯 조회
    Optional<BookingSlot> findByBusinessIdAndSlotDateAndStartTimeAndEndTime(
            UUID businessId, LocalDate slotDate,
            LocalTime startTime, LocalTime endTime
    );

    // 특정 날짜의 모든 슬롯 조회 (시간 순서)
    List<BookingSlot> findByBusinessIdAndSlotDateOrderByStartTimeAsc(
            UUID businessId, LocalDate slotDate
    );

    // 특정 메뉴의 모든 슬롯 조회
    List<BookingSlot> findByBusinessIdAndMenuId(
            UUID businessId, UUID menuId
    );

    // 특정 날짜 이전의 모든 슬롯 조회 (과거 슬롯)
    List<BookingSlot> findBySlotDateBefore(LocalDate date);

    // 특정 업체의 특정 날짜 이전 슬롯 조회 (과거 슬롯 일괄 삭제용)
    List<BookingSlot> findByBusinessIdAndSlotDateBefore(
            UUID businessId, LocalDate date
    );

    /**
     * 메뉴 기준 중복 슬롯 체크
     * 같은 메뉴가 같은 날짜, 같은 시작 시간에 슬롯을 가지는지 확인
     * 예시:
     * - 헤어컷 메뉴: 08:00, 09:00, 10:00 슬롯 생성 가능
     * - 파마 메뉴: 08:00, 10:00 슬롯 생성 가능 (같은 시간대에 다른 메뉴 허용)
     * - 헤어컷 메뉴: 08:00 슬롯 재생성 시도 → ❌ 중복 (같은 메뉴, 같은 시간)
     *
     * @param menuId 메뉴 ID
     * @param slotDate 슬롯 날짜
     * @param startTime 시작 시간
     * @return 중복 슬롯 존재 여부
     */
    boolean existsByMenuIdAndSlotDateAndStartTime(
            UUID menuId,
            LocalDate slotDate,
            LocalTime startTime
    );
}