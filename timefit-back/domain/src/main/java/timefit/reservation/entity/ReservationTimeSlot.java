package timefit.reservation.entity;

import timefit.common.entity.BaseEntity;
import timefit.business.entity.Business;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservation_time_slot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationTimeSlot extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @NotNull(message = "예약 날짜는 필수입니다")
    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @NotNull(message = "시작 시간은 필수입니다")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "종료 시간은 필수입니다")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull(message = "수용 인원은 필수입니다")
    @Min(value = 1, message = "수용 인원은 1명 이상이어야 합니다")
    @Column(nullable = false)
    private Integer capacity = 1;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
}