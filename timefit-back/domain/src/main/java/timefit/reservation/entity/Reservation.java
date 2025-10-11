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
import timefit.menu.entity.Menu;
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
    @JoinColumn(name = "menu_id")
    private Menu menu;

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

    // 예약 취소
    public void cancelReservation(String reason) {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예약입니다");
        }

        if (this.status == ReservationStatus.COMPLETED || this.status == ReservationStatus.NO_SHOW) {
            throw new IllegalStateException("완료되거나 노쇼 처리된 예약은 취소할 수 없습니다");
        }

        this.status = ReservationStatus.CANCELLED;
        this.notes = (this.notes != null ? this.notes + "\n" : "") + "[취소사유] " + reason;
    }

    /**
     * 예약 상태 변경 (승인/거절)
     */
    public void updateStatus(ReservationStatus newStatus, String reason) {
        // 상태 변경 검증
        if (newStatus == null) {
            throw new IllegalArgumentException("변경할 상태는 필수입니다");
        }

        // 동일한 상태로 변경 방지
        if (this.status == newStatus) {
            throw new IllegalStateException("이미 " + newStatus + " 상태입니다");
        }

        // 상태 변경
        this.status = newStatus;

        // 사유가 있으면 메모에 추가
        if (reason != null && !reason.trim().isEmpty()) {
            String statusMessage = getStatusMessage(newStatus);
            this.notes = (this.notes != null ? this.notes + "\n" : "") +
                    "[" + statusMessage + "] " + reason;
        }
    }

    /**
     * 예약 완료/노쇼 처리
     */
    public void completeReservation(ReservationStatus completionStatus, String completionNotes) {
        // 완료 상태 검증
        if (completionStatus != ReservationStatus.COMPLETED &&
                completionStatus != ReservationStatus.NO_SHOW) {
            throw new IllegalArgumentException("완료 상태는 COMPLETED 또는 NO_SHOW만 가능합니다");
        }

        // 현재 상태 검증
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 예약만 완료 처리할 수 있습니다");
        }

        // 상태 변경
        this.status = completionStatus;

        // 완료 메모 추가 (기존 getStatusMessage 메서드 재사용)
        if (completionNotes != null && !completionNotes.trim().isEmpty()) {
            String statusMessage = getStatusMessage(completionStatus);
            this.notes = (this.notes != null ? this.notes + "\n" : "") +
                    "[" + statusMessage + "] " + completionNotes;
        }
    }

    /**
     * 상태별 메시지 생성
     */
    private String getStatusMessage(ReservationStatus status) {
        switch (status) {
            case CONFIRMED:
                return "승인";
            case CANCELLED:
                return "거절";
            case COMPLETED:
                return "완료";
            case NO_SHOW:
                return "노쇼";
            default:
                return "상태변경";
        }
    }

}
