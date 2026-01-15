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
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;
import timefit.menu.entity.Menu;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingSlotCreationHelper {

    private final BookingSlotRepository bookingSlotRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final BookingSlotGenerationUtil slotGenerationUtil;

    /**
     * BookingSlot 일괄 생성
     * [처리 흐름]
     * 1. 날짜별 슬롯 생성 (OperatingHours 기반)
     * 2. 중복 체크 (menu_id + date + startTime 기준) - 일괄 조회 최적화
     * 3. 일괄 저장 (Batch Insert)
     * [중복 체크 정책]
     * - 같은 메뉴의 같은 시간대 슬롯만 중복으로 판단
     * - 다른 메뉴는 같은 시간대에 슬롯 생성 가능
     * [변경 사항 perf]
     * - 중복 체크: 390번 SELECT → 1번 SELECT
     * - 저장: Batch Insert (1~2번)
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

        log.info("BookingSlot 생성 시작: businessId={}, menuId={}, scheduleCount={}",
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

        // 2. 중복 체크 - 일괄 조회 (1번의 SELECT)
        checkDuplicates(menu.getId(), createdSlots);

        // 3. 일괄 저장 (Batch Insert, 중복 없음이 보장됨)
        bookingSlotRepository.saveAll(createdSlots);

        log.info("BookingSlot 생성 완료: menuId={}, 생성 개수={}",
                menu.getId(), createdSlots.size());

        // 4. 결과 반환 (생략 없음 - All or Nothing)
        return new BookingSlotResponse.CreationResult(
                createdSlots.size(),
                createdSlots.size(),
                0  // all or nothing이기 때문에 skip은 0 고정
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
        int dayOfWeekValue = date.getDayOfWeek().getValue();
        DayOfWeek dayOfWeek = DayOfWeek.fromValue(dayOfWeekValue);

        // 2. OperatingHours 조회 및 필터링
        List<OperatingHours> operatingHours = operatingHoursRepository
                .findByBusinessIdAndDayOfWeekOrderBySequenceAsc(business.getId(), dayOfWeek)
                .stream()
                .filter(oh -> !oh.getIsClosed())
                .collect(Collectors.toList());

        // 3. 운영시간 없으면 빈 리스트 반환
        if (operatingHours.isEmpty()) {
            log.debug("운영시간 없음 또는 휴무일: businessId={}, date={}", business.getId(), date);
            return new ArrayList<>();
        }

        // 4. 슬롯 생성
        return slotGenerationUtil.generateSlotsForDay(
                business, menu, date, operatingHours, timeRanges, intervalMinutes
        );
    }

    /**
     * 중복 체크 - 일괄 조회 최적화
     * [처리 흐름]
     * 1. 날짜 범위 추출 (min, max)
     * 2. 기존 슬롯 일괄 조회 (1번의 SELECT)
     * 3. 메모리에서 중복 체크 (Set 사용)
     *
     * [중복 판단 기준]
     * - menu_id + slot_date + start_time
     *
     * @param menuId 메뉴 ID
     * @param slots 생성할 슬롯 목록
     * @throws BookingException 중복 슬롯 발견 시
     */
    private void checkDuplicates(UUID menuId, List<BookingSlot> slots) {
        if (slots.isEmpty()) {
            return;
        }

        // 1. 날짜 범위 추출
        LocalDate minDate = slots.stream()
                .map(BookingSlot::getSlotDate)
                .min(LocalDate::compareTo)
                .orElseThrow();

        LocalDate maxDate = slots.stream()
                .map(BookingSlot::getSlotDate)
                .max(LocalDate::compareTo)
                .orElseThrow();

        // 2. 기존 슬롯 일괄 조회 (1번의 SELECT)
        List<BookingSlot> existingSlots = bookingSlotRepository
                .findByMenuIdAndSlotDateBetween(menuId, minDate, maxDate);

        // 3. 메모리에서 중복 체크 (Set 사용)
        Set<String> existingKeys = existingSlots.stream()
                .map(slot -> makeKey(slot.getSlotDate(), slot.getStartTime()))
                .collect(Collectors.toSet());

        // 4. 중복 검사
        for (BookingSlot slot : slots) {
            String key = makeKey(slot.getSlotDate(), slot.getStartTime());
            if (existingKeys.contains(key)) {
                log.error("중복 슬롯 발견 - 전체 롤백: menuId={}, date={}, startTime={}",
                        menuId, slot.getSlotDate(), slot.getStartTime());
                throw new BookingException(BookingErrorCode.AVAILABLE_SLOT_CONFLICT);
            }
        }

        log.debug("중복 체크 완료: menuId={}, 기존 슬롯={}, 신규 슬롯={}",
                menuId, existingSlots.size(), slots.size());
    }

    /**
     * 중복 체크용 키 생성
     * Format: "YYYY-MM-DD_HH:mm:ss"
     *
     * @param date 슬롯 날짜
     * @param time 시작 시간
     * @return 중복 체크용 키
     */
    private String makeKey(LocalDate date, LocalTime time) {
        return date.toString() + "_" + time.toString();
    }
}