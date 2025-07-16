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
        private final UUID availableSlotId;
        private final LocalDate reservationDate;
        private final LocalTime reservationTime;
        private final Integer durationMinutes;
        private final List<SelectedOption> selectedOptions;
        private final String notes;
        private final String customerName;
        private final String customerPhone;

        private CreateReservation(UUID businessId, UUID availableSlotId, LocalDate reservationDate,
                                    LocalTime reservationTime, Integer durationMinutes, List<SelectedOption> selectedOptions,
                                    String notes, String customerName, String customerPhone) {
            this.businessId = businessId;
            this.availableSlotId = availableSlotId;
            this.reservationDate = reservationDate;
            this.reservationTime = reservationTime;
            this.durationMinutes = durationMinutes;
            this.selectedOptions = selectedOptions;
            this.notes = notes;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
        }

        public static CreateReservation of(UUID businessId, UUID availableSlotId, LocalDate reservationDate,
                                            LocalTime reservationTime, Integer durationMinutes, List<SelectedOption> selectedOptions,
                                            String notes, String customerName, String customerPhone) {
            return new CreateReservation(businessId, availableSlotId, reservationDate, reservationTime,
                    durationMinutes, selectedOptions, notes, customerName, customerPhone);
        }

        // 총 금액 계산
        public Integer getTotalPrice() {
            if (selectedOptions == null || selectedOptions.isEmpty()) {
                return 0;
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
                    Objects.equals(availableSlotId, that.availableSlotId) &&
                    Objects.equals(reservationDate, that.reservationDate) &&
                    Objects.equals(reservationTime, that.reservationTime) &&
                    Objects.equals(durationMinutes, that.durationMinutes) &&
                    Objects.equals(selectedOptions, that.selectedOptions) &&
                    Objects.equals(notes, that.notes) &&
                    Objects.equals(customerName, that.customerName) &&
                    Objects.equals(customerPhone, that.customerPhone);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessId, availableSlotId, reservationDate, reservationTime,
                    durationMinutes, selectedOptions, notes, customerName, customerPhone);
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
}