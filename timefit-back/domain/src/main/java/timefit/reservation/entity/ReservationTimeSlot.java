package timefit.reservation.entity;

import timefit.common.entity.BaseEntity;
import timefit.business.entity.Business;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timefit.menu.entity.Menu;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservation_time_slot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationTimeSlot extends BaseEntity {

    public static final int ORDER_BASED = 0;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    /**
     * 이 슬롯이 제공하는 서비스
     * 필수 관계: 모든 슬롯은 특정 서비스를 위한 것
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @NotNull(message = "예약 날짜는 필수입니다")
    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @NotNull(message = "시작 시간은 필수입니다")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "종료 시간은 필수입니다")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * 최대 수용 인원
     * - SLOT_BASED: 정수 (1, 2, 3...) - 이 시간대에 동시 수용 가능한 최대 인원
     * - ORDER_BASED: 0 - 무제한
     */
    @Column
    private Integer capacity;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;


    // 예약 슬롯 생성 (정적 팩터리 메서드)
    public static ReservationTimeSlot createSlot(
            Business business, Menu menu, LocalDate slotDate, LocalTime startTime, LocalTime endTime, Integer capacity) {
        ReservationTimeSlot slot = new ReservationTimeSlot();
        slot.business = business;
        slot.menu = menu;
        slot.slotDate = slotDate;
        slot.startTime = startTime;
        slot.endTime = endTime;
        slot.capacity = capacity;
        slot.isAvailable = true;
        return slot;
    }

    // -----------------------------------------------------------------------    비즈니스 메서드

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

    // 슬롯 재개방 (예약 취소로 인한 자리 생기는 경우)
    public void reopenIfNeeded(Integer currentBookings) {
        // capacity의 값이 0 이면 ORDER_BASED 이므로 항상 open
        if (this.capacity == ORDER_BASED) {
            this.isAvailable = true;
            return;
        }

        if (currentBookings < this.capacity) {
            this.isAvailable = true;
        }
    }

    // 슬롯이 예약 가능한지 확인
    public boolean canAcceptReservation(Integer currentBookings) {
        if (!this.isAvailable) {
            return false;
        }

        // capacity 가 ORDER_BASED 인가?
        if (this.capacity == ORDER_BASED) {
            return true;
        }

        return currentBookings < this.capacity;
    }

    // 슬롯 시간이 유효한지 확인
    public boolean hasValidTime() {
        return this.startTime != null && this.endTime != null && this.startTime.isBefore(this.endTime);
    }

    // ------------------------------------------------    추가된 요구사항


    // 슬롯 비활성화
    public void deactivate() {
        this.isAvailable = false;
    }

    // 슬롯 활성화
    public void activate() {
        this.isAvailable = true;
    }

    // 과거 날짜인지 확인
    public boolean isPastDate() {
        return this.slotDate.isBefore(LocalDate.now());
    }

    // SLOT_BASED 타입인지 확인
    public boolean isSlotBased() {
        return this.menu.isSlotBased();
    }

    // ORDER_BASED 타입인지 확인
    public boolean isOrderBased() {
        return this.menu.isOrderBased();
    }

    // 무제한 capacity 인지 확인 (ORDER_BASED용)
    public boolean hasUnlimitedCapacity() {
        return this.capacity == ORDER_BASED;
    }

    /**
     * 특정 시간과 겹치는지 확인
     *
     * @param otherStart 다른 슬롯의 시작 시간
     * @param otherEnd 다른 슬롯의 종료 시간
     * @return 겹침 여부
     */
    public boolean overlapsWith(LocalTime otherStart, LocalTime otherEnd) {
        // start1 < end2 AND start2 < end1
        return this.startTime.isBefore(otherEnd) && otherStart.isBefore(this.endTime);
    }

    // 다른 슬롯과 겹치는지 확인 (같은 날짜 + 시간 겹침)
    public boolean overlapsWith(ReservationTimeSlot other) {
        // 같은 날짜가 아니면 겹치지 않음
        if (!this.slotDate.equals(other.slotDate)) {
            return false;
        }

        return overlapsWith(other.startTime, other.endTime);
    }

}