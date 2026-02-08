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

    /**
     * 고객의 전체 예약 수 조회 (삭제되지 않은 예약만)
     * @param customerId 고객 ID
     * @return 예약 수
     */
    long countByCustomerId(UUID customerId);
}