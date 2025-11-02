package timefit.common.entity;

import lombok.Getter;

@Getter
public enum DayOfWeek {
    SUNDAY(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6);

    private final int value;

    DayOfWeek(int value) {
        this.value = value;
    }
    // custom SUNDAY is 0 & java.time sunday is 7. 따라서 getValue()%7 으로 계산해야 옳음.
    public boolean matches(java.time.DayOfWeek standardDay) {
        return this.value == standardDay.getValue() % 7;
    }

    public static DayOfWeek fromValue(int value) {
        for (DayOfWeek day : values()) {
            if (day.value == value) {
                return day;
            }
        }
        throw new IllegalArgumentException(value + "은 유효한 값이 아닙니다.");
    }
}