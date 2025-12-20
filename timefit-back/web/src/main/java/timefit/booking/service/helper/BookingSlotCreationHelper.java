package timefit.booking.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotRepository;
import timefit.booking.service.dto.AvailableTimeRange;
import timefit.booking.service.dto.DailySlotSchedule;
import timefit.booking.service.util.BookingSlotGenerationUtil;
import timefit.business.entity.Business;
import timefit.business.entity.OperatingHours;
import timefit.business.repository.OperatingHoursRepository;
import timefit.common.entity.DayOfWeek;
import timefit.menu.entity.Menu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BookingSlot 생성 헬퍼
 * - BookingSlot 생성 비즈니스 로직
 * - 날짜별 슬롯 생성
 * - 중복 체크 및 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingSlotCreationHelper {

    private final BookingSlotRepository bookingSlotRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final BookingSlotGenerationUtil slotGenerationUtil;

    /**
     * BookingSlot 생성
     * 1. 모든 슬롯 생성
     * 2. 중복 체크 (하나라도 중복이면 예외 발생)
     * 3. 일괄 저장
     * 4. 결과 반환
     * [트랜잭션 보장]
     * - 중복 발견 시 즉시 예외 발생 → 전체 롤백
     * - 부분 성공 절대 불가 (All or Nothing)
     * [중복 체크]
     * - API 경로: 사용자가 같은 날짜/시간 재생성 방지
     * - Menu 경로: 같은 메뉴 재생성 시 기존 슬롯 감지
     *
     * @param business 업체
     * @param menu 메뉴
     * @param schedules 날짜별 스케줄 목록
     * @param intervalMinutes 슬롯 간격 (분)
     * @return 생성 결과 (요청 개수 = 생성 개수)
     * @throws timefit.exception.booking.BookingException 중복 슬롯 발견 시
     */
    public BookingSlotResponse.CreationResult createSlots(
            Business business,
            Menu menu,
            List<DailySlotSchedule> schedules,
            Integer intervalMinutes) {

        log.info("BookingSlot 생성 시작: businessId={}, menuId={}, 날짜 수={}",
                business.getId(), menu.getId(), schedules.size());

        List<BookingSlot> createdSlots = new ArrayList<>();

        // 1. 날짜별 루프 - 모든 슬롯 생성
        for (DailySlotSchedule schedule : schedules) {
            // 1-1) 각 날짜의 슬롯 생성
            List<BookingSlot> dailySlots = generateSlotsForDate(
                    business,
                    menu,
                    schedule.date(),
                    schedule.timeRanges(),
                    intervalMinutes
            );

            // 1-2) 생성 목록에 추가
            createdSlots.addAll(dailySlots);
        }

        // 2. 중복 체크 (하나라도 중복이면 예외 발생 → 전체 롤백)
        for (BookingSlot slot : createdSlots) {
            // 2-1) 중복 여부 확인 (businessId + slotDate + startTime)
            if (isDuplicate(business.getId(), slot)) {
                // 2-2) 중복 발견 시 즉시 예외 발생 (전체 롤백)
                log.error("중복 슬롯 발견 - 전체 롤백: date={}, startTime={}",
                        slot.getSlotDate(), slot.getStartTime());
                throw new timefit.exception.booking.BookingException(
                        timefit.exception.booking.BookingErrorCode.AVAILABLE_SLOT_CONFLICT
                );
            }
        }

        // 3. 일괄 저장 (중복 없음이 보장됨)
        bookingSlotRepository.saveAll(createdSlots);

        log.info("BookingSlot 생성 완료: businessId={}, 생성 개수={}",
                business.getId(), createdSlots.size());

        // 4. 결과 반환 (생략 없음 - All or Nothing)
        // parameter : 요청 개수 , 생성 개수 (동일) , 생략 개수 (0 고정)
        return new BookingSlotResponse.CreationResult(
                createdSlots.size(),
                createdSlots.size(),
                0                      // all or nothing 이기 때문에 skip is 0 fixed.
        );
    }

    /**
     * 특정 날짜의 슬롯 생성 (핵심 로직)
     * 1. 요일 추출
     * 2. 해당 요일의 OperatingHours 조회 및 필터링
     * 3. BookingSlotGenerationUtil 호출
     *
     * @param business 업체
     * @param menu 메뉴
     * @param date 대상 날짜
     * @param timeRanges 시간대 목록 (null이면 전체 운영시간)
     * @param intervalMinutes 슬롯 간격 (분)
     * @return 생성된 BookingSlot 목록
     */
    private List<BookingSlot> generateSlotsForDate(
            Business business,
            Menu menu,
            LocalDate date,
            List<AvailableTimeRange> timeRanges,
            Integer intervalMinutes) {

        // 1. 요일 추출
        // - LocalDate → DayOfWeek enum 변환
        int dayOfWeekValue = date.getDayOfWeek().getValue();
        DayOfWeek dayOfWeek = DayOfWeek.fromValue(dayOfWeekValue);

        // 2. OperatingHours 조회 및 필터링
        List<OperatingHours> operatingHours = operatingHoursRepository
                // 2-1) 해당 업체의 해당 요일 운영시간 조회 (sequence 순서대로)
                .findByBusinessIdAndDayOfWeekOrderBySequenceAsc(business.getId(), dayOfWeek)
                .stream()
                // 2-2) 휴무일 제외 (isClosed = false만)
                .filter(oh -> !oh.getIsClosed())
                // 2-3) 실제 운영하는 시간만 리스트로 수집
                .collect(Collectors.toList());

        // 3. 운영시간 없으면 빈 리스트 반환
        if (operatingHours.isEmpty()) {
            log.debug("운영시간 없음 또는 휴무일: businessId={}, date={}", business.getId(), date);
            return new ArrayList<>();
        }

        // 4. 슬롯 생성 (실제 생성 로직)
        // - BookingSlotGenerationUtil이 OperatingHours와 timeRanges를 조합하여 슬롯 생성
        return slotGenerationUtil.generateSlotsForDay(
                business, menu, date, operatingHours, timeRanges, intervalMinutes
        );
    }

    /**
     * 중복 체크
     * - businessId + slotDate + startTime 조합으로 중복 여부 확인
     * [중요]
     * - 같은 업체, 같은 날짜, 같은 시작 시간이면 중복으로 판단
     * - endTime은 체크하지 않음 (startTime만으로 충분)
     *
     * @param businessId 업체 ID
     * @param slot 체크할 슬롯
     * @return true: 중복, false: 중복 아님
     */
    private boolean isDuplicate(UUID businessId, BookingSlot slot) {
        return bookingSlotRepository.existsByBusinessIdAndSlotDateAndStartTime(
                businessId, slot.getSlotDate(), slot.getStartTime()
        );
    }
}