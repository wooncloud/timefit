package timefit.operatinghours.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.operatinghours.dto.OperatingHoursRequestDto;

import java.time.LocalTime;

/**
 * OperatingHours 도메인 공통 검증 로직
 * - 시간 순서 검증
 * - 영업 시간 범위 검증
 * - Request 기본 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperatingHoursValidator {

    /**
     * 시간 순서 검증 (closeTime > openTime)
     *
     *
     * @param openTime 시작 시간
     * @param closeTime 종료 시간
     * @throws BusinessException 종료 시간이 시작 시간보다 이르거나 같은 경우
     */
    public void validateTimeOrder(LocalTime openTime, LocalTime closeTime) {
        if (closeTime.isBefore(openTime) || closeTime.equals(openTime)) {
            log.warn("시간 순서 검증 실패: openTime={}, closeTime={}", openTime, closeTime);
            throw new BusinessException(
                    BusinessErrorCode.INVALID_OPERATING_HOURS,
                    String.format(
                            "영업 종료 시간(%s)은 시작 시간(%s)보다 늦어야 합니다.",
                            closeTime, openTime
                    )
            );
        }
    }

    /**
     * 영업 시간 범위 검증 (OperatingHours ⊆ BusinessHours)
     *
     *
     * @param opStart 예약 시간대 시작
     * @param opEnd 예약 시간대 종료
     * @param bhStart 영업 시작 시간
     * @param bhEnd 영업 종료 시간
     * @throws BusinessException 예약 시간대가 영업 시간 범위를 벗어난 경우
     */
    public void validateOperatingHoursWithinBusinessHours(
            LocalTime opStart, LocalTime opEnd,
            LocalTime bhStart, LocalTime bhEnd) {

        if (opStart.isBefore(bhStart)) {
            log.warn("예약 시간대 범위 초과: 시작 시간 - opStart={}, bhStart={}", opStart, bhStart);
            throw new BusinessException(
                    BusinessErrorCode.OPERATING_HOURS_OUT_OF_RANGE,
                    String.format(
                            "예약 시간대 시작(%s)이 영업 시작 시간(%s)보다 이릅니다.",
                            opStart, bhStart
                    )
            );
        }

        if (opEnd.isAfter(bhEnd)) {
            log.warn("예약 시간대 범위 초과: 종료 시간 - opEnd={}, bhEnd={}", opEnd, bhEnd);
            throw new BusinessException(
                    BusinessErrorCode.OPERATING_HOURS_OUT_OF_RANGE,
                    String.format(
                            "예약 시간대 종료(%s)가 영업 종료 시간(%s)보다 늦습니다.",
                            opEnd, bhEnd
                    )
            );
        }
    }

    /**
     * SetOperatingHours Request 전체 검증
     * - 휴무일 아닌 경우 시간 필수
     * - 시간 순서 검증
     * - 예약 시간대 범위 검증
     *
     * @param request 영업시간 설정 요청
     * @throws BusinessException 검증 실패 시
     */
    public void validateSetOperatingHoursRequest(
            OperatingHoursRequestDto.SetOperatingHours request) {

        log.debug("영업시간 설정 요청 검증 시작: schedules={}", request.schedules().size());

        for (OperatingHoursRequestDto.DaySchedule schedule : request.schedules()) {

            // 휴무일이 아닌 경우
            if (!Boolean.TRUE.equals(schedule.isClosed())) {

                // openTime, closeTime null 체크
                if (schedule.openTime() == null || schedule.closeTime() == null) {
                    log.warn("영업일 시간 누락: dayOfWeek={}", schedule.dayOfWeek());
                    throw new BusinessException(
                            BusinessErrorCode.INVALID_OPERATING_HOURS,
                            String.format(
                                    "영업일(요일: %d)인 경우 영업 시작/종료 시간은 필수입니다.",
                                    schedule.dayOfWeek()
                            )
                    );
                }

                LocalTime openTime = LocalTime.parse(schedule.openTime());
                LocalTime closeTime = LocalTime.parse(schedule.closeTime());

                // 시간 순서 검증
                validateTimeOrder(openTime, closeTime);

                // bookingTimeRanges 검증
                if (schedule.bookingTimeRanges() != null &&
                        !schedule.bookingTimeRanges().isEmpty()) {

                    for (OperatingHoursRequestDto.TimeRange range : schedule.bookingTimeRanges()) {

                        LocalTime rangeStart = LocalTime.parse(range.startTime());
                        LocalTime rangeEnd = LocalTime.parse(range.endTime());

                        // 시간 순서 검증
                        validateTimeOrder(rangeStart, rangeEnd);

                        // 영업 시간 범위 내 검증
                        validateOperatingHoursWithinBusinessHours(
                                rangeStart, rangeEnd, openTime, closeTime
                        );
                    }
                }
            } else {
                // 휴무일인 경우: 예약 시간대가 있으면 안 됨
                if (schedule.bookingTimeRanges() != null &&
                        !schedule.bookingTimeRanges().isEmpty()) {
                    log.warn("휴무일에 예약 시간대 존재: dayOfWeek={}", schedule.dayOfWeek());
                    throw new BusinessException(
                            BusinessErrorCode.INVALID_OPERATING_HOURS,
                            String.format(
                                    "휴무일(요일: %d)에는 예약 가능 시간대를 설정할 수 없습니다.",
                                    schedule.dayOfWeek()
                            )
                    );
                }
            }
        }

        log.debug("영업시간 설정 요청 검증 완료");
    }
}