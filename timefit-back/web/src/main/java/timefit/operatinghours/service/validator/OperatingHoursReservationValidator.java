package timefit.operatinghours.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.common.entity.DayOfWeek;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.reservation.entity.Reservation;
import timefit.reservation.repository.ReservationQueryRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OperatingHoursReservationValidator {

    private final ReservationQueryRepository reservationQueryRepository;

    /**
     * 미래 예약 보호
     * OperatingHours 변경 시 해당 요일의 미래 예약 확인
     * - 예약 날짜 >= 오늘
     * - 예약 상태: PENDING, CONFIRMED
     * - 진행 중인 예약이 있으면 영업시간 변경 불가 (변경할려면 먼저 고객과 협의 후 예약 취소/변경 으로 결정함.)
     *
     * @param businessId 업체 ID
     * @param dayOfWeek 변경할 요일
     * @throws BusinessException 진행 중인 예약이 있는 경우
     */
    public void validateNoFutureReservations(
            UUID businessId,
            DayOfWeek dayOfWeek) {

        log.debug("미래 예약 검증 시작: businessId={}, dayOfWeek={}", businessId, dayOfWeek);

        // 1. 해당 요일의 미래 예약 조회
        List<Reservation> futureReservations =
                reservationQueryRepository.findFutureReservationsByBusinessAndDayOfWeek(
                        businessId,
                        dayOfWeek,
                        LocalDate.now()
                );

        // 2. 예약 존재 시 Exception
        if (!futureReservations.isEmpty()) {

            // 가장 빠른 예약 찾기 (날짜 -> 시간 순)
            Reservation earliest = futureReservations.stream()
                    .min(Comparator.comparing(Reservation::getReservationDate)
                            .thenComparing(Reservation::getReservationTime))
                    .orElseThrow();

            log.warn("미래 예약 존재로 영업시간 변경 불가: businessId={}, dayOfWeek={}, " +
                            "reservationCount={}, earliestDate={}, earliestTime={}",
                    businessId, dayOfWeek, futureReservations.size(),
                    earliest.getReservationDate(), earliest.getReservationTime());

            throw new BusinessException(
                    BusinessErrorCode.OPERATING_HOURS_HAS_FUTURE_RESERVATIONS,
                    String.format(
                            "해당 시간대에 %d건의 예약이 예정되어 있습니다. " +
                                    "(가장 빠른 예약: %s %s) " +
                                    "먼저 고객과 협의 후 예약을 취소하거나 변경해주세요.",
                            futureReservations.size(),
                            earliest.getReservationDate(),
                            earliest.getReservationTime()
                    )
            );
        }

        log.debug("미래 예약 검증 완료: 진행 중인 예약 없음");
    }

    /**
     * 여러 요일에 대한 미래 예약 일괄 검증
     *
     * @param businessId 업체 ID
     * @param daysOfWeek 검증할 요일 목록
     * @throws BusinessException 하나라도 예약이 있는 경우
     */
    public void validateNoFutureReservationsForDays(
            UUID businessId,
            List<DayOfWeek> daysOfWeek) {

        log.debug("여러 요일 미래 예약 검증 시작: businessId={}, days={}",
                businessId, daysOfWeek.size());

        for (DayOfWeek dayOfWeek : daysOfWeek) {
            validateNoFutureReservations(businessId, dayOfWeek);
        }

        log.debug("여러 요일 미래 예약 검증 완료");
    }
}