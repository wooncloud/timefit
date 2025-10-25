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

    // 특정 날짜, 시간의 슬롯 존재 여부 확인 (중복 체크용)
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
}