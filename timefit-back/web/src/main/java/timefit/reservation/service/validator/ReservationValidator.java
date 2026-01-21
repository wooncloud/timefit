package timefit.reservation.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepository;
import timefit.reservation.repository.ReservationQueryRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Reservation 검증 전담 클래스
 * 역할:
 * - 예약 존재 여부, 소유권, 취소 가능 여부 등 검증
 * - 예약 타입 판별 및 검증 (RESERVATION_BASED / ONDEMAND_BASED)
 * - Entity 메서드 활용 (isCancellable 등)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationRepository reservationRepository;
    private final ReservationQueryRepository reservationQueryRepository;

    private static final int MAX_SEARCH_YEARS = 5;

    /**
     * 예약 존재 여부 검증 및 조회
     *
     * @param reservationId 검증할 예약 ID
     * @return 조회된 Reservation 엔티티
     * @throws ReservationException 예약이 존재하지 않을 경우
     */
    public Reservation validateExists(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("예약 없음: reservationId={}", reservationId);
                    return new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
                });
    }

    /**
     * 예약이 특정 업체에 속하는지 검증
     *
     * @param reservation 검증할 예약 엔티티
     * @param businessId 업체 ID
     * @throws ReservationException 예약이 해당 업체에 속하지 않을 경우
     */
    public void validateBelongsToBusiness(Reservation reservation, UUID businessId) {
        if (!reservation.getBusiness().getId().equals(businessId)) {
            log.warn("예약이 해당 업체에 속하지 않음: reservationId={}, businessId={}",
                    reservation.getId(), businessId);
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }
    }

    /**
     * 예약 소유자인지 검증 (고객 본인 확인)
     *
     * @param reservation 검증할 예약 엔티티
     * @param customerId 고객(사용자) ID
     * @throws ReservationException 예약 소유자가 아닐 경우
     */
    public void validateOwner(Reservation reservation, UUID customerId) {
        if (!reservation.getCustomer().getId().equals(customerId)) {
            log.warn("예약 소유자 아님: reservationId={}, customerId={}",
                    reservation.getId(), customerId);
            throw new ReservationException(ReservationErrorCode.NOT_RESERVATION_OWNER);
        }
    }

    /**
     *
     * 예약 취소 가능 여부 검증
     * Entity의 isCancellable() 메서드 사용
     * @param reservation 검증할 예약 엔티티
     * @throws ReservationException 취소 불가능한 상태일 경우
     */
    public void validateCancellable(Reservation reservation) {
        if (!reservation.isCancellable()) {
            log.warn("취소 불가능한 예약: reservationId={}, status={}",
                    reservation.getId(), reservation.getStatus());
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_CANCELLABLE);
        }
    }

    /**
     * 과거 날짜 예약 방지 검증
     *
     * @param reservationDate 예약 날짜
     * @throws ReservationException 과거 날짜일 경우
     */
    public void validateNotPastDate(LocalDate reservationDate) {
        if (reservationDate.isBefore(LocalDate.now())) {
            log.warn("과거 날짜 예약 시도: {}", reservationDate);
            throw new ReservationException(ReservationErrorCode.RESERVATION_PAST_DATE);
        }
    }

    /**
     * 예약 존재 및 업체 소속 동시 검증
     *
     * @param reservationId 예약 ID
     * @param businessId 업체 ID
     * @return 조회 및 검증된 Reservation 엔티티
     * @throws ReservationException 예약이 존재하지 않거나 업체에 속하지 않을 경우
     */
    public Reservation validateOfBusiness(UUID reservationId, UUID businessId) {
        Reservation reservation = validateExists(reservationId);
        validateBelongsToBusiness(reservation, businessId);
        return reservation;
    }

    /**
     * 예약 소유자 확인 및 취소 가능 여부 동시 검증
     *
     * @param reservationId 예약 ID
     * @param customerId 고객(사용자) ID
     * @return 조회 및 검증된 Reservation 엔티티
     * @throws ReservationException 소유자가 아니거나 취소 불가능한 경우
     */
    public Reservation validateForCancel(UUID reservationId, UUID customerId) {
        Reservation reservation = validateExists(reservationId);
        validateOwner(reservation, customerId);
        validateCancellable(reservation);
        return reservation;
    }

    /**
     * 날짜 범위 검증 (5년 상한선)
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @throws ReservationException 날짜 범위가 5년을 초과하는 경우
     */
    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return; // null인 경우는 Service 에서 디폴트 처리
        }

        // 시작일이 종료일보다 미래인 경우
        if (startDate.isAfter(endDate)) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_DATE_FORMAT,
                    "시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }

        // 5년 상한선 검증
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > (MAX_SEARCH_YEARS * 365)) {
            throw new ReservationException(
                    ReservationErrorCode.INVALID_DATE_FORMAT,
                    "검색 가능한 최대 기간은 " + MAX_SEARCH_YEARS + "년입니다.");
        }

        log.debug("날짜 범위 검증 통과: {} ~ {} ({} 일)", startDate, endDate, daysBetween);
    }

    /**
     * 예약 시점 시간대 충돌 체크
     *
     * 같은 업체, 같은 날짜, 시간대가 겹치는 활성 예약이 있는지 확인
     *
     * [검증 시나리오]
     * 1. 파마 (08:00-10:00) 이미 예약됨
     * 2. 헤어컷 (08:00-09:00) 예약 시도
     * 3. 시간대 겹침 → 예외 발생
     *
     * [활성 예약 정의]
     * - PENDING (확정 대기) 또는 CONFIRMED (확정됨) 상태
     * - CANCELLED, NO_SHOW, COMPLETED는 제외
     *
     * [겹침 판정 기준]
     * 두 시간대 [A시작, A종료]와 [B시작, B종료]가 겹침
     * ↔ NOT (A종료 ≤ B시작 OR B종료 ≤ A시작)
     *
     * @param businessId 업체 ID
     * @param date 예약 날짜
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @param excludeMenuId 제외할 메뉴 ID (같은 메뉴 예약은 허용, null 가능)
     * @throws ReservationException 시간대 충돌 시
     */
    public void validateTimeSlotConflict(
            UUID businessId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            UUID excludeMenuId) {

        log.debug("시간대 충돌 체크 시작: businessId={}, date={}, {}~{}",
                businessId, date, startTime, endTime);

        // 1. 해당 업체의 해당 날짜 활성 예약 조회 (QueryDSL)
        List<Reservation> activeReservations =
                reservationQueryRepository.findActiveReservationsByBusinessAndDate(businessId, date);

        if (activeReservations.isEmpty()) {
            log.debug("활성 예약 없음 - 충돌 없음");
            return;
        }

        // 2. 각 예약과 시간대 겹침 체크
        for (Reservation reservation : activeReservations) {
            // 같은 메뉴는 제외 (자기 자신 체크 방지)
            if (excludeMenuId != null &&
                    reservation.getMenu().getId().equals(excludeMenuId)) {
                continue;
            }

            // 예약 시간대 계산
            LocalTime resStart = reservation.getReservationTime();
            LocalTime resEnd = resStart.plusMinutes(reservation.getReservationDuration());

            // 시간대 겹침 체크
            if (isTimeOverlap(resStart, resEnd, startTime, endTime)) {
                log.warn("시간대 충돌 발생: 기존={}~{}, 신규={}~{}, 기존메뉴={}, 기존예약번호={}",
                        resStart, resEnd, startTime, endTime,
                        reservation.getMenu().getServiceName(),
                        reservation.getReservationNumber());

                throw new ReservationException(
                        ReservationErrorCode.RESERVATION_TIME_SLOT_CONFLICT,
                        String.format("해당 시간대(%s~%s)에 이미 다른 예약(%s, %s~%s)이 있습니다",
                                startTime, endTime,
                                reservation.getMenu().getServiceName(),
                                resStart, resEnd)
                );
            }
        }

        log.debug("시간대 충돌 없음: 예약 가능");
    }

    /**
     * 예약 타입 판별 (RESERVATION_BASED vs ONDEMAND_BASED)
     *
     * @param bookingSlotId 슬롯 ID (RESERVATION_BASED일 때 필수)
     * @param reservationDate 예약 날짜 (ONDEMAND_BASED일 때 필수)
     * @param reservationTime 예약 시간 (ONDEMAND_BASED일 때 필수)
     * @return true: RESERVATION_BASED, false: ONDEMAND_BASED
     * @throws ReservationException 유효하지 않은 예약 타입일 경우
     */
    public boolean isReservationBased(UUID bookingSlotId, LocalDate reservationDate, LocalTime reservationTime) {
        // RESERVATION_BASED 체크
        if (bookingSlotId != null) {
            return true;
        }

        // ONDEMAND_BASED 체크
        if (reservationDate != null && reservationTime != null) {
            return false;
        }

        // 둘 다 아닌 경우
        log.warn("유효하지 않은 예약 타입: bookingSlotId={}, reservationDate={}, reservationTime={}",
                bookingSlotId, reservationDate, reservationTime);
        throw new ReservationException(
                ReservationErrorCode.INVALID_RESERVATION_TYPE);
    }

    /**
     * Menu가 특정 Business에 속하는지 검증
     *
     * @param menu 검증할 메뉴
     * @param businessId 업체 ID
     * @throws MenuException Menu가 해당 Business에 속하지 않을 경우
     */
    public void validateMenuBelongsToBusiness(timefit.menu.entity.Menu menu, UUID businessId) {
        if (!menu.getBusiness().getId().equals(businessId)) {
            log.warn("메뉴가 해당 업체에 속하지 않음: menuId={}, businessId={}",
                    menu.getId(), businessId);
            throw new timefit.exception.menu.MenuException(timefit.exception.menu.MenuErrorCode.MENU_NOT_FOUND);
        }
    }

    /**
     * 예약이 승인/거절 가능한 상태인지 검증
     * - PENDING 상태만 승인/거절 가능
     * - 다른 상태(CONFIRMED, CANCELLED 등)는 불가
     *
     * @param reservation 검증할 예약
     * @throws ReservationException PENDING 상태가 아닐 경우
     */
    public void validateStatusForApproval(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            log.warn("승인/거절 불가능한 상태: reservationId={}, status={}",
                    reservation.getId(), reservation.getStatus());
            throw new ReservationException(ReservationErrorCode.RESERVATION_INVALID_STATUS);
        }
    }

    /**
     * 시간대 겹침 판정
     *
     * - 정각 경계는 허용 (12:00 종료, 12:00 시작 = 겹치지 않음)
     * - 1분이라도 겹치면 충돌
     *
     * 예시:
     * - [09:00-12:00]와 [12:00-13:00] → 겹치지 않음 (정각 경계)
     * - [09:00-12:00]와 [11:59-13:00] → 겹침 (1분 겹침)
     *
     * @param start1 첫 번째 시작 시간
     * @param end1 첫 번째 종료 시간
     * @param start2 두 번째 시작 시간
     * @param end2 두 번째 종료 시간
     * @return true: 겹침, false: 겹치지 않음
     */
    private boolean isTimeOverlap(
            LocalTime start1, LocalTime end1,
            LocalTime start2, LocalTime end2) {

        // 겹치지 않는 조건:
        // 1. 첫 번째 종료 ≤ 두 번째 시작 (정각 경계 허용)
        // 2. 두 번째 종료 ≤ 첫 번째 시작 (정각 경계 허용)
        boolean notOverlap = end1.isBefore(start2) || end1.equals(start2) ||
                end2.isBefore(start1) || end2.equals(start1);

        // 겹치는 조건: NOT (겹치지 않는 조건)
        return !notOverlap;
    }
}