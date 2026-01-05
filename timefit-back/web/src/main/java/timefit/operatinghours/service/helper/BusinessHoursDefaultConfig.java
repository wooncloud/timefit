package timefit.operatinghours.service.helper;

import timefit.business.entity.Business;
import timefit.business.entity.BusinessHours;
import timefit.common.entity.DayOfWeek;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BusinessHours 디폴트 설정 유틸리티
 * - 업체 생성 시 기본 영업시간 자동 설정
 * - 리셋 시 디폴트값으로 복원
 */
public class BusinessHoursDefaultConfig {

    // 디폴트 영업 시작 시간
    private static final LocalTime DEFAULT_OPEN_TIME = LocalTime.of(9, 0);
    // 디폴트 영업 종료 시간
    private static final LocalTime DEFAULT_CLOSE_TIME = LocalTime.of(18, 0);


    /**
     * 디폴트 영업시간 생성
     * - 월~금: 09:00 ~ 18:00
     * - 토~일: 휴무
     */
    public static List<BusinessHours> createDefaultBusinessHours(Business business) {
        List<BusinessHours> defaultHours = new ArrayList<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            if (isWeekday(day)) {
                // 월~금: 영업일
                defaultHours.add(BusinessHours.createOpenDay(
                        business,
                        day,
                        DEFAULT_OPEN_TIME,
                        DEFAULT_CLOSE_TIME
                ));
            } else {
                // 토~일: 휴무
                defaultHours.add(BusinessHours.createClosedDay(business, day));
            }
        }

        return defaultHours;
    }

    // 평일 여부 확인
    private static boolean isWeekday(DayOfWeek day) {
        // 월(1) ~ 금(5)
        return day.getValue() >= 1 && day.getValue() <= 5;
    }

    // 디폴트 오픈 시간
    public static LocalTime getDefaultOpenTime() {
        return DEFAULT_OPEN_TIME;
    }

    // 디폴트 마감 시간
    public static LocalTime getDefaultCloseTime() {
        return DEFAULT_CLOSE_TIME;
    }
}