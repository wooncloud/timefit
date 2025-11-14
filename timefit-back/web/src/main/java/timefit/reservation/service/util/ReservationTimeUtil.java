package timefit.reservation.service.util;

import java.time.LocalDate;

public class ReservationTimeUtil {

    private static final int DEFAULT_SEARCH_DAYS = 30;

    private ReservationTimeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 시작 날짜 결정 (디폴트: 30일 전)
     */
    public static LocalDate determineStartDate(LocalDate startDate) {
        return startDate != null ? startDate : LocalDate.now().minusDays(DEFAULT_SEARCH_DAYS);
    }

    /**
     * 종료 날짜 결정 (디폴트: 오늘)
     */
    public static LocalDate determineEndDate(LocalDate endDate) {
        return endDate != null ? endDate : LocalDate.now();
    }
}