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
 * 영업시간 Entity
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

    // 영업시간 수정
    public void updateOperatingHours(LocalTime openTime, LocalTime closeTime, Boolean isClosed) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isClosed = isClosed;
    }

    // 휴무일로 설정
    public void setClosed() {
        this.isClosed = true;
        this.openTime = null;
        this.closeTime = null;
    }

    // 영업일로 설정
    public void setOpen(LocalTime openTime, LocalTime closeTime) {
        this.isClosed = false;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    // 특정 시간이 영업시간 내인지 확인
    public boolean isTimeWithinOperatingHours(LocalTime time) {
        if (Boolean.TRUE.equals(isClosed) || openTime == null || closeTime == null) {
            return false;
        }
        return !time.isBefore(openTime) && !time.isAfter(closeTime);
    }

    // 시간대 겹침 확인 (같은 요일 내에서)
    public boolean isOverlapping(LocalTime otherOpenTime, LocalTime otherCloseTime) {
        if (Boolean.TRUE.equals(isClosed) || openTime == null || closeTime == null) {
            return false;
        }

        return !(closeTime.isBefore(otherOpenTime) || otherCloseTime.isBefore(openTime));
    }
}