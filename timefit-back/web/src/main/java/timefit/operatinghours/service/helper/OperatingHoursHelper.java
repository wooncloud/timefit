package timefit.operatinghours.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.entity.Business;
import timefit.business.entity.OperatingHours;
import timefit.business.repository.OperatingHoursRepository;
import timefit.common.entity.DayOfWeek;
import timefit.operatinghours.dto.OperatingHoursRequestDto;
import timefit.operatinghours.service.util.OperatingHoursConverter;
import timefit.operatinghours.service.validator.OperatingHoursReservationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * OperatingHours 관련 비즈니스 로직 Helper
 * - OperatingHours 재생성 (미래 예약 보호 검증 포함)
 * - 특정 요일 OperatingHours 토글
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperatingHoursHelper {

    private final OperatingHoursRepository operatingHoursRepository;
    private final OperatingHoursReservationValidator reservationValidator;

    /**
     * OperatingHours 재생성
     *
     * 개수가 가변적이므로 DELETE + INSERT 방식 사용
     *
     * 미래 예약 보호 검증 추가
     * - 각 요일별로 bookingTimeRanges가 있으면 미래 예약 확인
     * - 진행 중인 예약이 있으면 Exception
     *
     * CommandService의 private recreateOperatingHours() 로직 이동
     *
     * @param business 업체 엔티티
     * @param request 영업시간 설정 요청
     * @return 새로 생성된 OperatingHours 리스트
     * @throws timefit.exception.business.BusinessException 미래 예약이 있는 경우
     */
    public List<OperatingHours> recreateOperatingHours(
            Business business,
            OperatingHoursRequestDto.SetOperatingHours request) {

        log.debug("OperatingHours 재생성 시작: businessId={}", business.getId());

        // 미래 예약 보호 검증
        // deleteByBusinessId() 실행 전에 검증해야 함!
        for (OperatingHoursRequestDto.DaySchedule schedule : request.schedules()) {
            DayOfWeek dayOfWeek = DayOfWeek.fromValue(schedule.dayOfWeek());

            // 예약 가능 시간대가 있는 경우만 검증
            if (schedule.bookingTimeRanges() != null &&
                    !schedule.bookingTimeRanges().isEmpty()) {

                log.debug("미래 예약 검증 실행: dayOfWeek={}", dayOfWeek);
                reservationValidator.validateNoFutureReservations(
                        business.getId(),
                        dayOfWeek
                );
            }
        }

        log.debug("미래 예약 검증 통과: 모든 요일 예약 없음");

        // 1. 기존 데이터 삭제
        operatingHoursRepository.deleteByBusinessId(business.getId());
        log.debug("기존 OperatingHours 삭제 완료: businessId={}", business.getId());

        // 2. 새 데이터 생성
        List<OperatingHours> newOperatingHours = new ArrayList<>();

        for (OperatingHoursRequestDto.DaySchedule schedule : request.schedules()) {
            DayOfWeek dayOfWeek = DayOfWeek.fromValue(schedule.dayOfWeek());

            // 예약 가능 시간대가 있는 경우만 생성
            if (schedule.bookingTimeRanges() != null &&
                    !schedule.bookingTimeRanges().isEmpty()) {

                List<OperatingHours> dayOperatingHours =
                        OperatingHoursConverter.convertToOperatingHours(
                                business,
                                schedule.bookingTimeRanges(),
                                dayOfWeek
                        );

                newOperatingHours.addAll(operatingHoursRepository.saveAll(dayOperatingHours));

                log.debug("OperatingHours 생성: dayOfWeek={}, count={}",
                        dayOfWeek, dayOperatingHours.size());
            }
        }

        log.debug("OperatingHours 재생성 완료: totalCount={}", newOperatingHours.size());
        return newOperatingHours;
    }

    /**
     * 특정 요일의 모든 OperatingHours 토글
     *
     * 해당 요일의 모든 sequence를 찾아서 isClosed 상태 변경
     * (휴게시간으로 여러 개가 있을 수 있음)
     *
     * @param businessId 업체 ID
     * @param dayOfWeek 요일
     */
    public void toggleOperatingHoursForDay(UUID businessId, DayOfWeek dayOfWeek) {

        log.debug("OperatingHours 토글 시작: businessId={}, dayOfWeek={}",
                businessId, dayOfWeek);

        List<OperatingHours> operatingHoursList =
                operatingHoursRepository
                        .findByBusinessIdAndDayOfWeekOrderBySequenceAsc(businessId, dayOfWeek);

        if (operatingHoursList.isEmpty()) {
            log.debug("OperatingHours 없음: 토글 불필요");
            return;
        }

        // 모든 OperatingHours 토글
        operatingHoursList.forEach(OperatingHours::toggle);

        log.debug("OperatingHours 토글 완료: count={}", operatingHoursList.size());
    }
}