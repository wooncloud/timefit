package timefit.common.entity;

import lombok.Getter;

/**
 * 요일 Enum(DayOfWeek)
 * - Java의 기본 요일(java.time.DayOfWeek)은 ISO-8601 표준을 따라 1(MONDAY)~7(SUNDAY)로 구성됨.
 * - 반면 본 시스템은 0(SUNDAY)~6(SATURDAY)로 사용하는 규칙을 채택함.
 *
 * - 이는 DB, 프론트엔드 등 일부 외부 시스템에서 0 기반 인덱스를 사용하는 경우와의 일관성 확보를 위함.
 * - 이 Enum은 이러한 내부 규칙을 명확히 표현하며, Java 표준 요일과의 매핑 기능을 제공하는 목적을 지님.
 */

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

    /**
     * int 값 → DayOfWeek enum 변환
     * 외부 API(0~6)와 Java 표준(1~7) 모두 지원
     *
     * @param value 요일 값 (0~7)
     * @return DayOfWeek enum
     * @throws IllegalArgumentException 음수 입력 시
     */
    public static DayOfWeek fromValue(int value) {

        //  7 → 0 변환
        int normalizedValue = value % 7;

        for (DayOfWeek day : values()) {
            if (day.value == normalizedValue) {
                return day;
            }
        }
        throw new IllegalArgumentException(value + "은 유효한 값이 아닙니다.");
    }
}