package timefit.reservation.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Reservation 시간 관련 유틸리티
 * - 날짜 기본값 결정 (determineStartDate, determineEndDate)
 * - 날짜 파싱 (parseDate, parseDateRange)
 * - DateRange 값 객체 제공
 */
public class ReservationTimeUtil {

    private static final int DEFAULT_SEARCH_DAYS = 30;
    private static final int DEFAULT_PAST_MONTHS = 3;

    private ReservationTimeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 시작 날짜 결정 (디폴트: 30일 전)
     *
     * @param startDate 시작 날짜 (nullable)
     * @return 시작 날짜 (null이면 30일 전)
     */
    public static LocalDate determineStartDate(LocalDate startDate) {
        return startDate != null ? startDate : LocalDate.now().minusDays(DEFAULT_SEARCH_DAYS);
    }

    /**
     * 종료 날짜 결정 (디폴트: 오늘)
     *
     * @param endDate 종료 날짜 (nullable)
     * @return 종료 날짜 (null이면 오늘)
     */
    public static LocalDate determineEndDate(LocalDate endDate) {
        return endDate != null ? endDate : LocalDate.now();
    }

    /**
     * 날짜 문자열 파싱
     *
     * @param dateStr 날짜 문자열 (yyyy-MM-dd)
     * @return LocalDate 객체 (null이면 null 반환)
     * @throws DateTimeParseException 날짜 형식이 올바르지 않을 경우
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        return LocalDate.parse(dateStr);
    }

    /**
     * 날짜 범위 파싱
     *
     * @param startDateStr 시작 날짜 문자열 (nullable)
     * @param endDateStr 종료 날짜 문자열 (nullable)
     * @return DateRange 객체 (null은 null로 유지)
     * @throws DateTimeParseException 날짜 형식이 올바르지 않을 경우
     */
    public static DateRange parseDateRange(String startDateStr, String endDateStr) {
        LocalDate start = parseDate(startDateStr);
        LocalDate end = parseDate(endDateStr);
        return new DateRange(start, end);
    }

    /**
     * 날짜 범위 값 객체
     * 불변 객체로 시작/종료 날짜를 함께 관리
     * 기본값 설정 메서드 제공
     */
    public record DateRange(LocalDate start, LocalDate end) {

        /**
         * 기본값 적용
         *
         * 기본값:
         * - start: 3개월 전
         * - end: 오늘
         *
         * @return 기본값이 적용된 DateRange
         */
        public DateRange setDateRangeDefaults() {
            LocalDate defaultStart = start != null
                    ? start
                    : LocalDate.now().minusMonths(DEFAULT_PAST_MONTHS);
            LocalDate defaultEnd = end != null
                    ? end
                    : LocalDate.now();

            return new DateRange(defaultStart, defaultEnd);
        }

        /**
         * 둘 다 null인지 확인
         *
         * @return true: 둘 다 null, false: 하나라도 값 있음
         */
        public boolean isEmpty() {
            return start == null && end == null;
        }

        /**
         * 둘 다 값이 있는지 확인
         *
         * @return true: 둘 다 값 있음, false: 하나라도 null
         */
        public boolean isComplete() {
            return start != null && end != null;
        }
    }
}