package timefit.reservation.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import timefit.business.entity.Business;
import timefit.booking.entity.BookingSlot;
import timefit.menu.entity.Menu;
import timefit.user.entity.User;
import timefit.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * - BookingSlot 참조로 변경
 * - 스냅샷 필드 추가 (예약 시점의 가격, 소요시간 보존)
 * - ONDEMAND 와 RESERVATION 모두 지원
 */
@Entity
@Table(name = "reservation", indexes = {
        // 고객(customer) 조회용
        @Index(name = "idx_reservation_customer_date_time",
                columnList = "customer_id, reservation_date DESC, reservation_time DESC"),

        // 업체(business) 조회용
        @Index(name = "idx_reservation_business_date_time",
                columnList = "business_id, reservation_date DESC, reservation_time DESC")
})
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Business business;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Menu menu;

    /**
     * 변경: slot → bookingSlot
     * ONDEMAND_BASED일 때는 null, RESERVATION_BASED일 때는 필수
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_slot_id")  // slot_id → booking_slot_id
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BookingSlot bookingSlot;  // slot → bookingSlot

    @NotNull(message = "예약 날짜는 필수입니다")
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @NotNull(message = "예약 시간은 필수입니다")
    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    @Column(name = "reservation_number", length = 50)
    private String reservationNumber;

    /**
     * 예약 시점의 가격 스냅샷
     * 메뉴 가격이 변경되어도 예약 시점 가격 유지
     */
    @NotNull(message = "총 금액은 필수입니다")
    @Column(name = "reservation_price", nullable = false)
    private Integer reservationPrice;

    /**
     * 예약 시점의 소요 시간 스냅샷 (분)
     * 메뉴 소요시간이 변경되어도 예약 시점 소요시간 유지
     */
    @NotNull(message = "소요 시간은 필수입니다")
    @Min(value = 1, message = "소요 시간은 1분 이상이어야 합니다")
    @Column(name = "reservation_duration", nullable = false)
    private Integer reservationDuration;

    @NotNull(message = "고객명은 필수입니다")
    @Size(max = 50, message = "고객명은 50자 이하로 입력해주세요")
    @Column(name = "customer_name", nullable = false, length = 50)
    private String customerName;

    @NotNull(message = "고객 연락처는 필수입니다")
    @Size(max = 20, message = "연락처는 20자 이하로 입력해주세요")
    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // RESERVATION_BASED 예약 생성 (슬롯 기반)
    public static Reservation createReservationBased(User customer, Business business, Menu menu,
                                                     BookingSlot bookingSlot, String customerName,
                                                     String customerPhone, String notes) {
        validateReservationBasedFields(menu, bookingSlot);

        Reservation reservation = new Reservation();
        reservation.customer = customer;
        reservation.business = business;
        reservation.menu = menu;
        reservation.bookingSlot = bookingSlot;
        reservation.reservationDate = bookingSlot.getSlotDate();
        reservation.reservationTime = bookingSlot.getStartTime();
        reservation.reservationPrice = menu.getPrice();
        reservation.reservationDuration = menu.getDurationMinutes();
        reservation.status = ReservationStatus.PENDING;
        reservation.customerName = customerName;
        reservation.customerPhone = customerPhone;
        reservation.notes = notes;
        return reservation;
    }

    // ONDEMAND_BASED 예약 생성 (즉시 주문)
    public static Reservation createOnDemandBased(User customer, Business business, Menu menu,
                                                  LocalDate reservationDate, LocalTime reservationTime,
                                                  String customerName, String customerPhone, String notes) {
        validateOnDemandBasedFields(menu);

        Reservation reservation = new Reservation();
        reservation.customer = customer;
        reservation.business = business;
        reservation.menu = menu;
        reservation.bookingSlot = null;  // ONDEMAND 는 슬롯 없음
        reservation.reservationDate = reservationDate;
        reservation.reservationTime = reservationTime;
        reservation.reservationPrice = menu.getPrice();
        reservation.reservationDuration = menu.getDurationMinutes();
        reservation.status = ReservationStatus.PENDING;
        reservation.customerName = customerName;
        reservation.customerPhone = customerPhone;
        reservation.notes = notes;
        return reservation;
    }

    // RESERVATION_BASED 검증
    private static void validateReservationBasedFields(Menu menu, BookingSlot bookingSlot) {
        if (!menu.isReservationBased()) {
            throw new IllegalArgumentException("예약형 메뉴만 슬롯 기반 예약이 가능합니다");
        }
        if (bookingSlot == null) {
            throw new IllegalArgumentException("예약형은 BookingSlot이 필수입니다");
        }
    }

    // ONDEMAND_BASED 검증
    private static void validateOnDemandBasedFields(Menu menu) {
        if (!menu.isOnDemandBased()) {
            throw new IllegalArgumentException("주문형 메뉴만 즉시 예약이 가능합니다");
        }
    }

    // 예약 번호 설정
    public void updateReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    // 예약 날짜/시간 수정
    public void updateReservationDateTime(LocalDate newDate, LocalTime newTime) {
        this.reservationDate = newDate;
        this.reservationTime = newTime;
    }

    // 고객 정보 수정
    public void updateCustomerInfo(String customerName, String customerPhone) {
        if (customerName != null) {
            this.customerName = customerName;
        }
        if (customerPhone != null) {
            this.customerPhone = customerPhone;
        }
    }

    // 예약 메모 수정
    public void updateNotes(String newNotes) {
        this.notes = newNotes;
    }

    // 예약 상태 변경
    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;

        // 취소 시 취소 시간 기록
        if (ReservationStatus.CANCELLED == newStatus) {
            this.cancelledAt = LocalDateTime.now();
        }
    }

    // 예약 확정
    public void confirm() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("대기 중인 예약만 확정할 수 있습니다");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    // 예약 완료
    public void complete() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 예약만 완료할 수 있습니다");
        }
        this.status = ReservationStatus.COMPLETED;
    }

    // 예약 취소
    public void cancel() {
        if (status == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("완료된 예약은 취소할 수 없습니다");
        }
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    // 노쇼 처리
    public void markAsNoShow() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 예약만 노쇼 처리할 수 있습니다");
        }
        this.status = ReservationStatus.NO_SHOW;
    }

    // 예약형(슬롯 기반) 예약인지 확인
    public boolean isReservationBased() {
        return bookingSlot != null;
    }

    // 주문형(즉시) 예약인지 확인
    public boolean isOnDemandBased() {
        return bookingSlot == null;
    }

    // 활성 예약인지 확인 (취소/노쇼 제외)
    public boolean isActiveReservation() {
        return status != ReservationStatus.CANCELLED && status != ReservationStatus.NO_SHOW;
    }

    // 취소 가능한 상태인지 확인
    public boolean isCancellable() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
    }

    /**
     * 예약 취소 가능 시간인지 확인 (서비스 소요 시간 고려)
     * [취소 마감 시간 계산]
     * 취소 마감 = 예약 시간 - 서비스 소요 시간
     * [목적]
     * 서비스 시작 직전 취소로 인한 업체 손해 방지
     * 업체가 대체 고객을 찾을 수 있는 충분한 시간 확보
     * [예시]
     * - 예약 시간: 16:00 (오후 4시)
     * - 서비스 소요: 60분
     * - 취소 마감: 15:00 (오후 3시)
     * - 현재 14:30 → true (취소 가능)
     * - 현재 15:30 → false (취소 불가)
     *
     * @return 취소 가능한 시간인지 여부
     */
    public boolean isCancellableByTime() {
        // 1. 예약 시작 시간
        LocalDateTime reservationDateTime = reservationDate.atTime(reservationTime);

        // 2. 취소 마감 시간 = 예약 시간 - 서비스 소요 시간
        LocalDateTime cancelDeadline = reservationDateTime.minusMinutes(reservationDuration);

        // 3. 현재 시간이 취소 마감 시간 이전이어야 취소 가능
        return LocalDateTime.now().isBefore(cancelDeadline);
    }

    /**
     * 취소 마감 시간 반환
     * - 예약 시간 - 서비스 소요 시간
     * - 고객에게 "언제까지 취소 가능한지" 안내용
     * @return 취소 마감 시간
     */
    public LocalDateTime getCancelDeadline() {
        LocalDateTime reservationDateTime = reservationDate.atTime(reservationTime);
        return reservationDateTime.minusMinutes(reservationDuration);
    }

    // 과거 예약인지 확인
    public boolean isPastReservation() {
        return reservationDate.isBefore(LocalDate.now());
    }

    // 오늘 예약인지 확인
    public boolean isTodayReservation() {
        return reservationDate.equals(LocalDate.now());
    }

    // 예약 시점 가격 반환 (스냅샷)
    public Integer getSnapshotPrice() {
        return reservationPrice;
    }

    // 예약 시점 소요시간 반환 (스냅샷)
    public Integer getSnapshotDuration() {
        return reservationDuration;
    }
}