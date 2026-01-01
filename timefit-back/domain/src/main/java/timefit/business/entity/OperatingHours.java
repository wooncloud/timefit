package timefit.business.entity;

import timefit.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timefit.common.entity.DayOfWeek;

import java.time.LocalTime;

/**
 * 예약 슬롯 시간 관리 Entity
 * - 기존 BusinessOperatingHours -> OperatingHours로 변경
 * - sequence 필드 추가로 같은 요일 여러 시간대 지원 (런치 브레이크 등)
 */
@Entity
@Table(name = "operating_hours")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperatingHours extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed = false;

    /**
     * 같은 요일 여러 시간대 지원을 위한 순서
     * 예: 월요일 09:00-12:00 (sequence=0), 14:00-18:00 (sequence=1)
     */
    @Column(name = "sequence", nullable = false)
    private Integer sequence = 0;

    // 영업시간 생성 (정적 팩터리 메서드)
    public static OperatingHours createOperatingHours(Business business, DayOfWeek dayOfWeek,
                                                      LocalTime openTime, LocalTime closeTime,
                                                      Boolean isClosed, Integer sequence) {
        OperatingHours hours = new OperatingHours();
        hours.business = business;
        hours.dayOfWeek = dayOfWeek;
        hours.openTime = openTime;
        hours.closeTime = closeTime;
        hours.isClosed = isClosed != null ? isClosed : false;
        hours.sequence = sequence != null ? sequence : 0;
        return hours;
    }

    // 단일 시간대 영업시간 생성 (기본 sequence=0)
    public static OperatingHours createSinglePeriod(Business business, DayOfWeek dayOfWeek,
                                                    LocalTime openTime, LocalTime closeTime,
                                                    Boolean isClosed) {
        return createOperatingHours(business, dayOfWeek, openTime, closeTime, isClosed, 0);
    }

    // 휴무일 생성
    public static OperatingHours createClosedDay(Business business, DayOfWeek dayOfWeek) {
        return createOperatingHours(business, dayOfWeek, null, null, true, 0);
    }

    // 휴무 상태 토글
    // 휴무로 변경 시 시간 정보는 유지 (재활성화를 위해)
    public void toggle() {
        this.isClosed = !this.isClosed;
    }
}