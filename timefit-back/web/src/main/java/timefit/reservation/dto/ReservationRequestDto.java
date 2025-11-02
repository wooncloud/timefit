package timefit.reservation.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Reservation Request DTO
 * 예약 관련 모든 요청 DTO를 포함
 */
public class ReservationRequestDto {

    /**
     * 예약 생성 요청 (고객용)
     * RESERVATION_BASED와 ONDEMAND_BASED 모두 지원
     */
    @Getter
    public static class CreateReservation {

        @NotNull(message = "업체 ID는 필수입니다")
        private final UUID businessId;

        @NotNull(message = "메뉴 ID는 필수입니다")
        private final UUID menuId;

        // RESERVATION_BASED 예약 시 필수 (슬롯 기반)
        private final UUID bookingSlotId;

        // ONDEMAND_BASED 예약 시 필수 (즉시 예약)
        private final LocalDate reservationDate;
        private final LocalTime reservationTime;

        @NotNull(message = "서비스 시간은 필수입니다")
        @Min(value = 10, message = "최소 10분 이상이어야 합니다")
        @Max(value = 480, message = "최대 8시간을 초과할 수 없습니다")
        private final Integer durationMinutes;

        @NotNull(message = "예약 금액은 필수입니다")
        @Min(value = 0, message = "금액은 0원 이상이어야 합니다")
        private final Integer totalPrice;

        @NotBlank(message = "예약자 이름은 필수입니다")
        @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
        private final String customerName;

        @NotBlank(message = "연락처는 필수입니다")
        @Pattern(regexp = "^\\d{10,11}$", message = "올바른 전화번호 형식이 아닙니다")
        private final String customerPhone;

        @Size(max = 500, message = "메모는 500자를 초과할 수 없습니다")
        private final String notes;

        private CreateReservation(UUID businessId, UUID menuId, UUID bookingSlotId,
                                  LocalDate reservationDate, LocalTime reservationTime,
                                  Integer durationMinutes, Integer totalPrice,
                                  String customerName, String customerPhone, String notes) {
            this.businessId = businessId;
            this.menuId = menuId;
            this.bookingSlotId = bookingSlotId;
            this.reservationDate = reservationDate;
            this.reservationTime = reservationTime;
            this.durationMinutes = durationMinutes;
            this.totalPrice = totalPrice;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
            this.notes = notes;
        }

        /**
         * RESERVATION_BASED 예약인지 확인
         */
        public boolean isReservationBased() {
            return bookingSlotId != null;
        }

        /**
         * ONDEMAND_BASED 예약인지 확인
         */
        public boolean isOnDemandBased() {
            return bookingSlotId == null && reservationDate != null && reservationTime != null;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            CreateReservation that = (CreateReservation) other;
            return Objects.equals(businessId, that.businessId) &&
                    Objects.equals(menuId, that.menuId) &&
                    Objects.equals(bookingSlotId, that.bookingSlotId) &&
                    Objects.equals(reservationDate, that.reservationDate) &&
                    Objects.equals(reservationTime, that.reservationTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessId, menuId, bookingSlotId, reservationDate, reservationTime);
        }
    }

    /**
     * 예약 수정 요청 (고객용)
     */
    @Getter
    public static class UpdateReservation {

        private final LocalDate reservationDate;
        private final LocalTime reservationTime;
        private final String customerName;
        private final String customerPhone;

        @Size(max = 500, message = "메모는 500자를 초과할 수 없습니다")
        private final String notes;

        @NotBlank(message = "수정 사유는 필수입니다")
        @Size(max = 200, message = "수정 사유는 200자를 초과할 수 없습니다")
        private final String reason;

        private UpdateReservation(LocalDate reservationDate, LocalTime reservationTime,
                                  String customerName, String customerPhone,
                                  String notes, String reason) {
            this.reservationDate = reservationDate;
            this.reservationTime = reservationTime;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
            this.notes = notes;
            this.reason = reason;
        }

        public static UpdateReservation of(LocalDate reservationDate, LocalTime reservationTime,
                                           String customerName, String customerPhone,
                                           String notes, String reason) {
            return new UpdateReservation(reservationDate, reservationTime, customerName,
                    customerPhone, notes, reason);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            UpdateReservation that = (UpdateReservation) other;
            return Objects.equals(reservationDate, that.reservationDate) &&
                    Objects.equals(reservationTime, that.reservationTime) &&
                    Objects.equals(reason, that.reason);
        }

        @Override
        public int hashCode() {
            return Objects.hash(reservationDate, reservationTime, reason);
        }
    }

    /**
     * 예약 취소 요청 (고객용)
     */
    @Getter
    public static class CancelReservation {

        @NotBlank(message = "취소 사유는 필수입니다")
        @Size(max = 200, message = "취소 사유는 200자를 초과할 수 없습니다")
        private final String reason;

        private CancelReservation(String reason) {
            this.reason = reason;
        }

        public static CancelReservation of(String reason) {
            return new CancelReservation(reason);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            CancelReservation that = (CancelReservation) other;
            return Objects.equals(reason, that.reason);
        }

        @Override
        public int hashCode() {
            return Objects.hash(reason);
        }
    }
}