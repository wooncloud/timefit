package timefit.business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timefit.common.entity.BaseEntity;
import timefit.common.entity.DayOfWeek;

import java.time.LocalTime;

/**
 * 업체 영업시간 Entity
 * - 요일별 총 영업시간 (고객에게 표시)
 * - 예: 월요일 09:00 ~ 22:00
 */
@Entity
@Table(
        name = "business_hours",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_business_hours_business_day",
                        columnNames = {"business_id", "day_of_week"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessHours extends BaseEntity {

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

    // ========== 정적 팩토리 메서드 ==========

    // 영업일 생성
    public static BusinessHours createOpenDay(
            Business business,
            DayOfWeek dayOfWeek,
            LocalTime openTime,
            LocalTime closeTime) {

        BusinessHours hours = new BusinessHours();
        hours.business = business;
        hours.dayOfWeek = dayOfWeek;
        hours.openTime = openTime;
        hours.closeTime = closeTime;
        hours.isClosed = false;
        return hours;
    }

    // 휴무일 생성
    public static BusinessHours createClosedDay(
            Business business,
            DayOfWeek dayOfWeek) {

        BusinessHours hours = new BusinessHours();
        hours.business = business;
        hours.dayOfWeek = dayOfWeek;
        hours.openTime = null;
        hours.closeTime = null;
        hours.isClosed = true;
        return hours;
    }

    // -------------- 비즈니스 메써드

    // 영업시간 수정
    public void updateHours(LocalTime openTime, LocalTime closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isClosed = false;
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
    public boolean isTimeWithinBusinessHours(LocalTime time) {
        if (Boolean.TRUE.equals(isClosed) || openTime == null || closeTime == null) {
            return false;
        }
        return !time.isBefore(openTime) && !time.isAfter(closeTime);
    }

    // 시간대가 영업시간 범위 내인지 확인
    public boolean isTimeRangeWithinBusinessHours(LocalTime startTime, LocalTime endTime) {
        if (Boolean.TRUE.equals(isClosed) || openTime == null || closeTime == null) {
            return false;
        }
        return !startTime.isBefore(openTime) && !endTime.isAfter(closeTime);
    }
}