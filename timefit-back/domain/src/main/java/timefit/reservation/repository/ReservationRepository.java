package timefit.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    // Menu의 활성 예약 존재 여부
    boolean existsByMenuIdAndStatusIn(UUID menuId, List<ReservationStatus> statuses);

    // Business의 활성 예약 존재 여부
    boolean existsByBusinessIdAndStatusIn(UUID businessId, List<ReservationStatus> statuses);

    // 고객의 전체 예약 수 조회
    long countByCustomerId(UUID customerId);

    /**
     * Menu 삭제 전 detach 대상 조회
     * - 활성 예약(PENDING/CONFIRMED)은 삭제 시도 자체가 서비스 레이어에서 거부됨
     * - 여기서 조회되는 건 COMPLETED/CANCELLED/NO_SHOW 상태의 이력 예약만
     */
    List<Reservation> findByMenuId(UUID menuId);

    /**
     * 슬롯 일괄 삭제 전 detach 대상 조회
     * - 슬롯 ID 목록에 해당하는 모든 Reservation 조회
     * - 과거 슬롯의 경우 COMPLETED/CANCELLED 상태가 대부분
     */
    List<Reservation> findByBookingSlotIdIn(List<UUID> slotIds);
}