package timefit.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "예약 요청")
public class ReservationRequestDto {

    /**
     * 예약 생성 요청 (고객용)
     * RESERVATION_BASED와 ONDEMAND_BASED 모두 지원
     */
    @Schema(description = "예약 생성 (RESERVATION_BASED 또는 ONDEMAND_BASED)")
    public record CreateReservation(
            @Schema(
                    description = "업체 ID",
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "업체 ID는 필수입니다")
            UUID businessId,

            @Schema(
                    description = "메뉴 ID",
                    example = "550e8400-e29b-41d4-a716-446655440002",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "메뉴 ID는 필수입니다")
            UUID menuId,

            // RESERVATION_BASED 예약 시 필수 (슬롯 기반)
            @Schema(
                    description = "슬롯 ID (RESERVATION_BASED일 때 필수)",
                    example = "550e8400-e29b-41d4-a716-446655440003",
                    nullable = true
            )
            UUID bookingSlotId,

            // ONDEMAND_BASED 예약 시 필수 (즉시 예약)
            @Schema(
                    description = "예약 날짜 (ONDEMAND_BASED일 때 필수)",
                    example = "2025-12-01",
                    nullable = true
            )
            LocalDate reservationDate,

            @Schema(
                    description = "예약 시간 (ONDEMAND_BASED일 때 필수)",
                    example = "14:00:00",
                    nullable = true
            )
            LocalTime reservationTime,

            @Schema(
                    description = "서비스 시간 (분 단위, 10~480분)",
                    example = "60",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "서비스 시간은 필수입니다")
            @Min(value = 10, message = "최소 10분 이상이어야 합니다")
            @Max(value = 480, message = "최대 8시간을 초과할 수 없습니다")
            Integer durationMinutes,

            @Schema(
                    description = "예약 금액 (0원 이상)",
                    example = "30000",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "예약 금액은 필수입니다")
            @Min(value = 0, message = "금액은 0원 이상이어야 합니다")
            Integer totalPrice,

            @Schema(
                    description = "예약자 이름 (최대 50자)",
                    example = "홍길동",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "예약자 이름은 필수입니다")
            @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
            String customerName,

            @Schema(
                    description = "연락처 (10~11자리 숫자)",
                    example = "01012345678",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "연락처는 필수입니다")
            @Pattern(regexp = "^\\d{10,11}$", message = "올바른 전화번호 형식이 아닙니다")
            String customerPhone,

            @Schema(
                    description = "메모 (최대 500자)",
                    example = "처음 방문입니다",
                    nullable = true
            )
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
    @Schema(description = "예약 수정")
    public record UpdateReservation(
            @Schema(
                    description = "예약 날짜",
                    example = "2025-12-02",
                    nullable = true
            )
            LocalDate reservationDate,

            @Schema(
                    description = "예약 시간",
                    example = "15:00:00",
                    nullable = true
            )
            LocalTime reservationTime,

            @Schema(
                    description = "예약자 이름",
                    example = "홍길동",
                    nullable = true
            )
            String customerName,

            @Schema(
                    description = "연락처",
                    example = "01087654321",
                    nullable = true
            )
            String customerPhone,

            @Schema(
                    description = "메모 (최대 500자)",
                    example = "시간 변경 요청합니다",
                    nullable = true
            )
            @Size(max = 500, message = "메모는 500자를 초과할 수 없습니다")
            String notes,

            @Schema(
                    description = "수정 사유 (최대 200자)",
                    example = "개인 사정으로 시간 변경",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
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
    @Schema(description = "예약 취소")
    public record CancelReservation(
            @Schema(
                    description = "취소 사유 (최대 200자)",
                    example = "개인 사정으로 취소합니다",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "취소 사유는 필수입니다")
            @Size(max = 200, message = "취소 사유는 200자를 초과할 수 없습니다")
            String reason
    ) {
        public static CancelReservation of(String reason) {
            return new CancelReservation(reason);
        }
    }
}