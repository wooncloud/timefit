package timefit.reservation.dto;

import lombok.Getter;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReservationRequestDto {

    /**
     * 예약 신청 요청
     */
    @Getter
    public static class CreateReservation {
        private final UUID businessId;
        private final UUID customerId;
        private final UUID availableSlotId;
        private final Integer totalPrice;
        private final Integer durationMinutes;
        private final List<SelectedOption> selectedOptions;
        private final String notes;
        private final String customerName;
        private final String customerPhone;

        private CreateReservation(UUID businessId, UUID customerId, UUID availableSlotId, Integer totalPrice,
                                    Integer durationMinutes, List<SelectedOption> selectedOptions,
                                    String notes, String customerName, String customerPhone) {
            this.businessId = businessId;
            this.customerId = customerId;
            this.availableSlotId = availableSlotId;
            this.totalPrice = totalPrice;
            this.durationMinutes = durationMinutes;
            this.selectedOptions = selectedOptions;
            this.notes = notes;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
        }

        public static CreateReservation of(UUID businessId, UUID customerId, UUID availableSlotId, Integer totalPrice,
                                            Integer durationMinutes, List<SelectedOption> selectedOptions,
                                            String notes, String customerName, String customerPhone) {
            return new CreateReservation(businessId, customerId, availableSlotId, durationMinutes, totalPrice,
                    selectedOptions, notes, customerName, customerPhone);
        }

        // 총 금액 계산
        public Integer getTotalPrice() {
            if (selectedOptions == null || selectedOptions.isEmpty()) {
                return this.totalPrice;
            }
            return selectedOptions.stream()
                    .mapToInt(SelectedOption::getPrice)
                    .sum();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            CreateReservation that = (CreateReservation) other;
            return Objects.equals(businessId, that.businessId) &&
                    Objects.equals(customerId, that.customerId) &&
                    Objects.equals(availableSlotId, that.availableSlotId) &&
                    Objects.equals(totalPrice, that.totalPrice) &&
                    Objects.equals(durationMinutes, that.durationMinutes) &&
                    Objects.equals(selectedOptions, that.selectedOptions) &&
                    Objects.equals(notes, that.notes) &&
                    Objects.equals(customerName, that.customerName) &&
                    Objects.equals(customerPhone, that.customerPhone);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessId, customerId, availableSlotId, durationMinutes, totalPrice,
                    selectedOptions, notes, customerName, customerPhone);
        }
    }

    /**
     * 선택된 서비스 옵션
     */
    @Getter
    public static class SelectedOption {
        private final UUID optionId;
        private final String optionName;
        private final Integer price;

        private SelectedOption(UUID optionId, String optionName, Integer price) {
            this.optionId = optionId;
            this.optionName = optionName;
            this.price = price;
        }

        public static SelectedOption of(UUID optionId, String optionName, Integer price) {
            return new SelectedOption(optionId, optionName, price);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            SelectedOption that = (SelectedOption) other;
            return Objects.equals(optionId, that.optionId) &&
                    Objects.equals(optionName, that.optionName) &&
                    Objects.equals(price, that.price);
        }

        @Override
        public int hashCode() {
            return Objects.hash(optionId, optionName, price);
        }
    }

    /**
     * 내 예약 목록 조회 요청
     */
    @Getter
    public static class GetMyReservations {
        private final String status;
        private final String startDate;
        private final String endDate;
        private final UUID businessId;
        private final Integer page;
        private final Integer size;

        private GetMyReservations(String status, String startDate, String endDate, UUID businessId, Integer page, Integer size) {
            this.status = status;
            this.startDate = startDate;
            this.endDate = endDate;
            this.businessId = businessId;
            this.page = page != null ? page : 0;
            this.size = size != null ? size : 20;
        }

        public static GetMyReservations of(String status, String startDate, String endDate, UUID businessId, Integer page, Integer size) {
            return new GetMyReservations(status, startDate, endDate, businessId, page, size);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            GetMyReservations that = (GetMyReservations) other;
            return Objects.equals(status, that.status) &&
                    Objects.equals(startDate, that.startDate) &&
                    Objects.equals(endDate, that.endDate) &&
                    Objects.equals(businessId, that.businessId) &&
                    Objects.equals(page, that.page) &&
                    Objects.equals(size, that.size);
        }

        @Override
        public int hashCode() {
            return Objects.hash(status, startDate, endDate, businessId, page, size);
        }
    }

    /**
     * 예약 수정 요청
     */
    @Getter
    public static class UpdateReservation {
        private final LocalDate reservationDate;
        private final LocalTime reservationTime;
        private final String notes;
        private final String reason;

        private UpdateReservation(LocalDate reservationDate, LocalTime reservationTime, String notes, String reason) {
            this.reservationDate = reservationDate;
            this.reservationTime = reservationTime;
            this.notes = notes;
            this.reason = reason;
        }

        public static UpdateReservation of(LocalDate reservationDate, LocalTime reservationTime, String notes, String reason) {
            return new UpdateReservation(reservationDate, reservationTime, notes, reason);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            UpdateReservation that = (UpdateReservation) other;
            return Objects.equals(reservationDate, that.reservationDate) &&
                    Objects.equals(reservationTime, that.reservationTime) &&
                    Objects.equals(notes, that.notes) &&
                    Objects.equals(reason, that.reason);
        }

        @Override
        public int hashCode() {
            return Objects.hash(reservationDate, reservationTime, notes, reason);
        }
    }

    /**
     * 예약 취소 요청
     */
    @Getter
    public static class CancelReservation {
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

    /**
     * 예약 상태 변경 요청 (승인/거절)
     */
    @Getter
    public static class ChangeReservationStatus {
        private final ReservationStatus status;
        private final String reason;

        private ChangeReservationStatus(ReservationStatus status, String reason) {
            this.status = status;
            this.reason = reason;
        }

        public static ChangeReservationStatus of(ReservationStatus status, String reason) {
            return new ChangeReservationStatus(status, reason);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            ChangeReservationStatus that = (ChangeReservationStatus) other;
            return Objects.equals(status, that.status) && Objects.equals(reason, that.reason);
        }

        @Override
        public int hashCode() {
            return Objects.hash(status, reason);
        }
    }

    @Getter
    public static class CompleteReservation {
        private final ReservationStatus status;
        private final String notes;

        private CompleteReservation(ReservationStatus status, String notes) {
            this.status = status;
            this.notes = notes;
        }

        public static CompleteReservation of(ReservationStatus status, String notes) {
            return new CompleteReservation(status, notes);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            CompleteReservation that = (CompleteReservation) other;
            return Objects.equals(status, that.status) && Objects.equals(notes, that.notes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(status, notes);
        }
    }

}