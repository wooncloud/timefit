package timefit.booking.entity;

import timefit.business.entity.Business;
import timefit.menu.entity.Menu;
import timefit.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * BookingSlot Entity - 예약 가능한 시간 슬롯
 * - 기존 ReservationTimeSlot -> BookingSlot 으로 변경
 */
@Entity
@Table(name = "booking_slot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingSlot extends BaseEntity {

    /**
     * ONDEMAND_BASED 메뉴의 용량 상수
     * 즉시 주문형은 용량 제한이 없음을 의미
     */
    public static final int ONDEMAND_CAPACITY = 0;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @NotNull
    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * 예약 가능 인원
     * - capacity = 0: ONDEMAND (무제한)
     * - capacity = 1~N: RESERVATION (예약 선점 방식)
     */
    @NotNull
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    // RESERVATION_BASED 메뉴용 슬롯 생성
    public static BookingSlot createReservationSlot(Business business, Menu menu,
                                                    LocalDate slotDate, LocalTime startTime,
                                                    LocalTime endTime, Integer capacity) {
        validateReservationSlotFields(menu, capacity);

        BookingSlot slot = new BookingSlot();
        slot.business = business;
        slot.menu = menu;
        slot.slotDate = slotDate;
        slot.startTime = startTime;
        slot.endTime = endTime;
        slot.capacity = capacity;
        slot.isAvailable = true;
        return slot;
    }

    // ONDEMAND_BASED 메뉴용 슬롯 생성 (사실상 사용되지 않음)
    // 슬롯 없이 바로 예약 생성
    public static BookingSlot createOnDemandSlot(Business business, Menu menu,
                                                 LocalDate slotDate, LocalTime startTime,
                                                 LocalTime endTime) {
        validateOnDemandSlotFields(menu);

        BookingSlot slot = new BookingSlot();
        slot.business = business;
        slot.menu = menu;
        slot.slotDate = slotDate;
        slot.startTime = startTime;
        slot.endTime = endTime;
        slot.capacity = ONDEMAND_CAPACITY;
        slot.isAvailable = true;
        return slot;
    }

    // RESERVATION_BASED 슬롯 검증
    private static void validateReservationSlotFields(Menu menu, Integer capacity) {
        if (!menu.isReservationBased()) {
            throw new IllegalArgumentException("예약형 메뉴만 예약 슬롯을 생성할 수 있습니다");
        }
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("예약 슬롯의 인원은 최소 1명 이상이어야 합니다");
        }
    }

    // ONDEMAND_BASED 슬롯 검증
    private static void validateOnDemandSlotFields(Menu menu) {
        if (!menu.isOnDemandBased()) {
            throw new IllegalArgumentException("주문형 메뉴는 예약 슬롯을 생성할 수 없습니다");
        }
    }

    // 슬롯 활성화/비활성화
    public void updateAvailability(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    // 슬롯 시간 수정
    public void updateTime(LocalTime startTime, LocalTime endTime) {
        validateTimeOrder(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 인원수 수정
    public void updateCapacity(Integer capacity) {
        if (capacity == null || capacity < 0) {
            throw new IllegalArgumentException("인원수는 0 이상이어야 합니다");
        }
        this.capacity = capacity;
    }

    // 시간 순서 검증
    private void validateTimeOrder(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이전이어야 합니다");
        }
    }

    // 예약 가능 여부 확인
    public boolean canAcceptReservation(Integer currentReservations) {
        if (!Boolean.TRUE.equals(isAvailable)) {
            return false;
        }

        // ONDEMAND 는 항상 예약 가능
        if (capacity == ONDEMAND_CAPACITY) {
            return true;
        }

        // RESERVATION은 수용량 확인
        return currentReservations < capacity;
    }

    // 과거 슬롯인지 확인
    public boolean isPastSlot() {
        return slotDate.isBefore(LocalDate.now());
    }

    // 오늘 슬롯인지 확인
    public boolean isTodaySlot() {
        return slotDate.equals(LocalDate.now());
    }

    // 유효한 시간 설정인지 확인
    public boolean hasValidTime() {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }

    // ONDEMAND 슬롯인지 확인
    public boolean isOnDemandSlot() {
        return capacity == ONDEMAND_CAPACITY;
    }

    // RESERVATION 슬롯인지 확인
    public boolean isReservationSlot() {
        return capacity > ONDEMAND_CAPACITY;
    }

    // 슬롯 소요 시간 계산 (분)
    public int getDurationMinutes() {
        if (!hasValidTime()) {
            return 0;
        }
        return (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public void markAsUnavailable() { this.isAvailable = false; }
    public void markAsAvailable() { this.isAvailable = true; }
    public void markAsFull() { this.isAvailable = false; }
}