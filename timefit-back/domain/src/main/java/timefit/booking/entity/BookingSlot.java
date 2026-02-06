package timefit.booking.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

@Entity
@Table(name = "booking_slot", indexes = {
        // 업체 (business) 기반 조회용
        @Index(name = "idx_booking_slot_business_date_time",
                columnList = "business_id, slot_date, start_time"),

        // 메뉴 (menu) 기반 조회용
        @Index(name = "idx_booking_slot_menu_date_time",
                columnList = "menu_id, slot_date, start_time")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingSlot extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;


    /**
     * RESERVATION_BASED 메뉴용 슬롯 생성
     *
     * @param business 업체
     * @param menu 메뉴 (RESERVATION_BASED만 가능)
     * @param slotDate 슬롯 날짜
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 생성된 BookingSlot
     */
    public static BookingSlot create(
            Business business, Menu menu, LocalDate slotDate, LocalTime startTime, LocalTime endTime) {

        validateSlotFields(menu, startTime, endTime);

        BookingSlot slot = new BookingSlot();
        slot.business = business;
        slot.menu = menu;
        slot.slotDate = slotDate;
        slot.startTime = startTime;
        slot.endTime = endTime;
        slot.isAvailable = true;

        return slot;
    }

    // 슬롯 필드 검증
    private static void validateSlotFields(Menu menu, LocalTime startTime, LocalTime endTime) {
        if (!menu.isReservationBased()) {
            throw new IllegalArgumentException("예약형 메뉴만 슬롯을 생성할 수 있습니다");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("시작 시간과 종료 시간은 필수입니다");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이전이어야 합니다");
        }
    }

    /**
     * 예약 가능 여부 확인
     * @return 예약 가능 여부
     */
    public boolean isActiveForReservation() {
        return this.isAvailable;
    }

    // 슬롯 비활성화
    public void markAsUnavailable() {
        this.isAvailable = false;
    }

    // 슬롯 활성화
    public void markAsAvailable() {
        this.isAvailable = true;
    }

    // 유효한 시간 여부 확인
    public boolean hasValidTime() {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }

    // 슬롯 소요 시간 계산 (분)
    public int getDurationMinutes() {
        if (!hasValidTime()) {
            return 0;
        }
        return (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }
}