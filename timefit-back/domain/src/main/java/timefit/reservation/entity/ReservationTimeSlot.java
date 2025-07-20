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


    // 예약 슬롯 생성 (정적 팩터리 메서드)
    public static ReservationTimeSlot createSlot(Business business, LocalDate slotDate,
                                                    LocalTime startTime, LocalTime endTime, Integer capacity) {
        ReservationTimeSlot slot = new ReservationTimeSlot();
        slot.business = business;
        slot.slotDate = slotDate;
        slot.startTime = startTime;
        slot.endTime = endTime;
        slot.capacity = capacity != null ? capacity : 1;
        slot.isAvailable = true;
        return slot;
    }

    // 슬롯 시간 수정
    public void updateTime(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 슬롯 수용 인원 수정
    public void updateCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    // 슬롯 예약 가능 여부 수정
    public void updateAvailability(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    // 슬롯 마감 처리 (예약이 다 찬 경우)
    public void markAsFull() {
        this.isAvailable = false;
    }

    // 슬롯 재개방 (예약 취소로 인한 자리 생김)
    public void reopenIfNeeded(Integer currentBookings) {
        if (currentBookings < this.capacity) {
            this.isAvailable = true;
        }
    }

    // 슬롯이 예약 가능한지 확인
    public boolean canAcceptReservation(Integer currentBookings) {
        return this.isAvailable && currentBookings < this.capacity;
    }

    // 슬롯 시간이 유효한지 확인
    public boolean hasValidTime() {
        return this.startTime != null && this.endTime != null && this.startTime.isBefore(this.endTime);
    }

}