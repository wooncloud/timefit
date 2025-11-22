package timefit.reservation.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class ReservationRequestDto {

    /**
     * 예약 생성 요청 (고객용)
     * RESERVATION_BASED와 ONDEMAND_BASED 모두 지원
     */
    public record CreateReservation(
            @NotNull(message = "업체 ID는 필수입니다")
            UUID businessId,

            @NotNull(message = "메뉴 ID는 필수입니다")
            UUID menuId,

            // RESERVATION_BASED 예약 시 필수 (슬롯 기반)
            UUID bookingSlotId,

            // ONDEMAND_BASED 예약 시 필수 (즉시 예약)
            LocalDate reservationDate,
            LocalTime reservationTime,

            @NotNull(message = "서비스 시간은 필수입니다")
            @Min(value = 10, message = "최소 10분 이상이어야 합니다")
            @Max(value = 480, message = "최대 8시간을 초과할 수 없습니다")
            Integer durationMinutes,

            @NotNull(message = "예약 금액은 필수입니다")
            @Min(value = 0, message = "금액은 0원 이상이어야 합니다")
            Integer totalPrice,

            @NotBlank(message = "예약자 이름은 필수입니다")
            @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
            String customerName,

            @NotBlank(message = "연락처는 필수입니다")
            @Pattern(regexp = "^\\d{10,11}$", message = "올바른 전화번호 형식이 아닙니다")
            String customerPhone,

            @Size(max = 500, message = "메모는 500자를 초과할 수 없습니다")
            String notes
    ) {
        public static CreateReservation of(
                UUID businessId, UUID menuId, UUID bookingSlotId,
                LocalDate reservationDate, LocalTime reservationTime,
                Integer durationMinutes, Integer totalPrice,
                String customerName, String customerPhone, String notes) {
            return new CreateReservation(
                    businessId, menuId, bookingSlotId,
                    reservationDate, reservationTime,
                    durationMinutes, totalPrice,
                    customerName, customerPhone, notes
            );
        }
    }

    /**
     * 예약 수정 요청 (고객용)
     */
    public record UpdateReservation(
            LocalDate reservationDate,
            LocalTime reservationTime,
            String customerName,
            String customerPhone,

            @Size(max = 500, message = "메모는 500자를 초과할 수 없습니다")
            String notes,

            @NotBlank(message = "수정 사유는 필수입니다")
            @Size(max = 200, message = "수정 사유는 200자를 초과할 수 없습니다")
            String reason
    ) {
        public static UpdateReservation of(
                LocalDate reservationDate, LocalTime reservationTime,
                String customerName, String customerPhone,
                String notes, String reason) {
            return new UpdateReservation(
                    reservationDate, reservationTime,
                    customerName, customerPhone,
                    notes, reason
            );
        }
    }

    /**
     * 예약 취소 요청 (고객용)
     */
    public record CancelReservation(
            @NotBlank(message = "취소 사유는 필수입니다")
            @Size(max = 200, message = "취소 사유는 200자를 초과할 수 없습니다")
            String reason
    ) {
        public static CancelReservation of(String reason) {
            return new CancelReservation(reason);
        }
    }
}