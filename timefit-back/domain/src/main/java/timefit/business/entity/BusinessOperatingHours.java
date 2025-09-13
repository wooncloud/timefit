package timefit.business.entity;

import timefit.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "business_operating_hours")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessOperatingHours extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @NotNull
    @Enumerated(EnumType.ORDINAL)  // DB에 0,1,2... 값으로 저장
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed = false;


    /**
     * 영업시간 생성 (정적 팩터리 메서드)
     */
    public static BusinessOperatingHours createOperatingHours(Business business, DayOfWeek dayOfWeek,
                                                                LocalTime openTime, LocalTime closeTime, Boolean isClosed) {
        BusinessOperatingHours hours = new BusinessOperatingHours();
        hours.business = business;
        hours.dayOfWeek = dayOfWeek;
        hours.openTime = openTime;
        hours.closeTime = closeTime;
        hours.isClosed = isClosed != null ? isClosed : false;
        return hours;
    }

    /**
     * 영업시간 수정
     */
    public void updateOperatingHours(LocalTime openTime, LocalTime closeTime, Boolean isClosed) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isClosed = isClosed;
    }

    /**
     * 휴무일로 설정
     */
    public void setClosed() {
        this.isClosed = true;
        this.openTime = null;
        this.closeTime = null;
    }

    /**
     * 영업일로 설정
     */
    public void setOpen(LocalTime openTime, LocalTime closeTime) {
        this.isClosed = false;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}