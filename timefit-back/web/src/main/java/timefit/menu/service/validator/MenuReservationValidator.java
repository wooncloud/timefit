package timefit.menu.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Menu 예약 관련 검증자
 * - Menu 삭제/비활성화 시 미래 예약 검증
 * - Menu 서비스 제공 시간(durationMinutes) 변경 시 미래 예약 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuReservationValidator {

    private final ReservationRepository reservationRepository;

    /**
     * Menu에 미래 활성 예약이 존재하지 않는지 검증 (duration 살펴보지 않음)
     * - Menu 삭제 전
     * - Menu 비활성화 전
     *
     * @param menuId 검증할 Menu ID
     * @throws MenuException 미래 예약이 있는 경우
     */
    public void validateActiveReservations(UUID menuId) {
        List<Reservation> futureReservations = getFutureActiveReservations(menuId);

        if (!futureReservations.isEmpty()) {
            log.warn("메뉴 비활성화 불가 - 메뉴에 미래 활성 예약 존재: menuId={}, count={}",
                    menuId, futureReservations.size());

            throw new MenuException(MenuErrorCode.CANNOT_DEACTIVATE_MENU_WITH_RESERVATIONS);
        }
    }

    /**
     * durationMinutes 변경 시 미래 예약 검증
     * - BookingSlot은 durationMinutes 기준으로 생성됨
     * - durationMinutes 변경 시 기존 슬롯과 예약이 불일치. 데이터 정합성 교정 목적.
     * - 데이터 일관성 보장을 위해 미래 예약 없을 때만 변경 허용
     *
     * @param menuId 검증할 Menu ID
     * @param newDurationMinutes 새로운 소요시간 (분)
     * @param currentDurationMinutes 현재 소요시간 (분)
     * @throws MenuException 미래 예약이 있는데 durationMinutes를 변경하려 할 경우
     */
    public void validateActiveReservationsWithDurationMinutes(
            UUID menuId,
            Integer newDurationMinutes,
            Integer currentDurationMinutes) {

        // durationMinutes 변경이 없으면 검증 불필요
        if (newDurationMinutes == null || newDurationMinutes.equals(currentDurationMinutes)) {
            return;
        }

        List<Reservation> futureReservations = getFutureActiveReservations(menuId);

        if (!futureReservations.isEmpty()) {
            log.warn("서비스 시간 변경 불가 - 미래 예약 존재: menuId={}, " +
                            "currentDuration={}분, newDuration={}분, count={}",
                    menuId, currentDurationMinutes, newDurationMinutes,
                    futureReservations.size());

            throw new MenuException(MenuErrorCode.CANNOT_CHANGE_DURATION_WITH_RESERVATIONS);
        }
    }

    /**
     * 미래 활성 예약 조회
     * - 오늘 이후 날짜
     * - CANCELLED, NO_SHOW 제외
     *
     * @param menuId 조회할 Menu ID
     * @return 미래 활성 예약 목록
     */
    private List<Reservation> getFutureActiveReservations(UUID menuId) {
        LocalDate today = LocalDate.now();

        // TODO: 처음 부터 findAll 하는게 마음에 들지 않음. user 기준 잡고 필터링 해야함.
        return reservationRepository.findAll()
                .stream()
                // 1) 해당 menuId에 연결된 예약만 필터링
                .filter(r -> r.getMenu().getId().equals(menuId))
                // 2) 오늘 이후(오늘 포함) 날짜의 예약만 필터링
                .filter(r -> !r.getReservationDate().isBefore(today))
                // 3) 취소(CANCELLED) 또는 노쇼(NO_SHOW) 상태는 제외 → 활성 예약만 남김
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED
                        && r.getStatus() != ReservationStatus.NO_SHOW)
                .toList();
    }
}