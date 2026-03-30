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
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import timefit.operatinghours.dto.OperatingHoursRequestDto;

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
            DayOfWeek dayOfWeek,
            List<OperatingHoursRequestDto.TimeRange> newRanges) {

        log.debug("미래 예약 검증 시작: businessId={}, dayOfWeek={}", businessId, dayOfWeek);

        // 1. 해당 요일의 미래 예약 조회
        List<Reservation> futureReservations =
                reservationQueryRepository.findFutureReservationsByBusinessAndDayOfWeek(
                        businessId,
                        dayOfWeek,
                        LocalDate.now()
                );

        if (futureReservations.isEmpty()) {
            log.debug("미래 예약 검증 완료: 예약 없음");
            return;
        }

        // 2. 새 OH 범위 밖으로 밀려나는 예약만 필터링
        List<Reservation> conflicting = futureReservations.stream()
                .filter(r -> newRanges.stream().noneMatch(range -> {
                    LocalTime start = LocalTime.parse(range.startTime());
                    LocalTime end = LocalTime.parse(range.endTime());
                    return !r.getReservationTime().isBefore(start)
                            && !r.getReservationTime().isAfter(end);
                }))
                .collect(Collectors.toList());

        if (conflicting.isEmpty()) {
            log.debug("미래 예약 검증 완료: 범위 밖 예약 없음");
            return;
        }

        // 3. 밀려나는 예약 존재 시 Exception
        Reservation earliest = conflicting.stream()
                .min(Comparator.comparing(Reservation::getReservationDate)
                        .thenComparing(Reservation::getReservationTime))
                .orElseThrow();

        log.warn("범위 축소로 예약 충돌: businessId={}, dayOfWeek={}, " +
                        "conflictCount={}, earliestDate={}, earliestTime={}",
                businessId, dayOfWeek, conflicting.size(),
                earliest.getReservationDate(), earliest.getReservationTime());

        throw new BusinessException(
                BusinessErrorCode.OPERATING_HOURS_HAS_FUTURE_RESERVATIONS,
                String.format(
                        "변경된 시간대 밖에 %d건의 예약이 있습니다. " +
                                "(가장 빠른 예약: %s %s) " +
                                "먼저 고객과 협의 후 예약을 취소하거나 변경해주세요.",
                        conflicting.size(),
                        earliest.getReservationDate(),
                        earliest.getReservationTime()
                )
        );
    }
}