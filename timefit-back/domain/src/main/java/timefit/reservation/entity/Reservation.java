package timefit.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timefit.business.entity.Business;
import timefit.common.entity.BaseEntity;
import timefit.service.entity.Service;
import timefit.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id")
    private ReservationTimeSlot slot;

    @NotNull(message = "예약 날짜는 필수입니다")
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @NotNull(message = "예약 시간은 필수입니다")
    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    @Column(name = "reservation_number", length = 50)
    private String reservationNumber;

    @NotNull(message = "소요 시간은 필수입니다")
    @Min(value = 1, message = "소요 시간은 1분 이상이어야 합니다")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull(message = "총 금액은 필수입니다")
    @Min(value = 0, message = "총 금액은 0원 이상이어야 합니다")
    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDING;

    @NotBlank(message = "고객명은 필수입니다")
    @Size(max = 50, message = "고객명은 50자 이하로 입력해주세요")
    @Column(name = "customer_name", nullable = false, length = 50)
    private String customerName;

    @NotBlank(message = "고객 연락처는 필수입니다")
    @Size(max = 20, message = "연락처는 20자 이하로 입력해주세요")
    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // 정적 팩토리 메써드
    public static Reservation createReservation(User customer, Business business, ReservationTimeSlot slot,
                                                LocalDate reservationDate, LocalTime reservationTime,
                                                Integer durationMinutes, Integer totalPrice,
                                                String customerName, String customerPhone, String notes) {
        Reservation reservation = new Reservation();
        reservation.customer = customer;
        reservation.business = business;
        reservation.slot = slot;
        reservation.reservationDate = reservationDate;
        reservation.reservationTime = reservationTime;
        reservation.durationMinutes = durationMinutes;
        reservation.totalPrice = totalPrice;
        reservation.status = ReservationStatus.PENDING;
        reservation.customerName = customerName;
        reservation.customerPhone = customerPhone;
        reservation.notes = notes;
        return reservation;
    }

    public void updateReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    // 예약 날짜/시간 수정
    public void updateReservationDateTime(LocalDate newDate, LocalTime newTime) {
        this.reservationDate = newDate;
        this.reservationTime = newTime;
    }

    // 예약 메모 수정
    public void updateNotes(String newNotes) {
        this.notes = newNotes;
    }

}
