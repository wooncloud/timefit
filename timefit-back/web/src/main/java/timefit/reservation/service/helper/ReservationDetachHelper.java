package timefit.reservation.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import timefit.reservation.entity.Reservation;
import timefit.reservation.repository.ReservationRepository;

import java.util.List;
import java.util.UUID;

/**
 * 예약 이력 보존을 위한 FK detach 전담 헬퍼
 *
 * 역할:
 * - 부모 엔티티(Menu, BookingSlot) 삭제 전 연관 Reservation의 FK를 null 처리
 * - Reservation 행 자체는 삭제하지 않고 이력으로 보존
 *
 * 설계 의도:
 * - CommandService는 ReservationRepository를 직접 주입받지 않음
 * - detach 규칙(조회 + null 처리)은 이 클래스 한 곳에서만 관리
 * - ReservationRepository 의존성은 이 클래스가 단독으로 보유
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationDetachHelper {

    private final ReservationRepository reservationRepository;

    /**
     * Menu 삭제 전 호출
     * 해당 Menu를 참조하는 모든 Reservation의 menu FK를 null로 처리
     *
     * 대상: COMPLETED/CANCELLED/NO_SHOW 상태의 이력 예약
     * (PENDING/CONFIRMED은 삭제 시도 자체가 서비스 레이어에서 거부됨)
     *
     * @param menuId 삭제 대상 Menu ID
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void detachFromMenu(UUID menuId) {
        List<Reservation> reservations = reservationRepository.findByMenuId(menuId);

        if (reservations.isEmpty()) {
            log.debug("detach 대상 예약 없음: menuId={}", menuId);
            return;
        }

        reservations.forEach(Reservation::detachMenu);

        log.debug("menu FK detach 완료: menuId={}, 대상 예약 수={}",
                menuId, reservations.size());
    }

    /**
     * BookingSlot 일괄 삭제 전 호출
     * 해당 슬롯들을 참조하는 모든 Reservation의 bookingSlot FK를 null로 처리
     *
     * 대상: 과거 슬롯 특성상 COMPLETED/CANCELLED 상태가 대부분
     *
     * @param slotIds 삭제 대상 BookingSlot ID 목록
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void detachFromBookingSlots(List<UUID> slotIds) {
        if (slotIds.isEmpty()) {
            return;
        }

        List<Reservation> reservations =
                reservationRepository.findByBookingSlotIdIn(slotIds);

        if (reservations.isEmpty()) {
            log.debug("detach 대상 예약 없음: slotIds.size={}", slotIds.size());
            return;
        }

        reservations.forEach(Reservation::detachBookingSlot);

        log.debug("bookingSlot FK detach 완료: 슬롯 수={}, 대상 예약 수={}",
                slotIds.size(), reservations.size());
    }
}