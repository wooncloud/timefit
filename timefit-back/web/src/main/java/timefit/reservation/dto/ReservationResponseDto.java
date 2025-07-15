package timefit.reservation.dto;

import lombok.Getter;
import timefit.reservation.entity.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class ReservationResponseDto {

    /**
     * 예약 상세 정보 응답
     */
    @Getter
    public static class ReservationDetail {
        private final UUID reservationId;
        private final String reservationNumber;
        private final ReservationStatus status;
        private final BusinessInfo businessInfo;
        private final ReservationDetails reservationDetails;
        private final CustomerInfo customerInfo;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private ReservationDetail(UUID reservationId, String reservationNumber, ReservationStatus status,
                                    BusinessInfo businessInfo, ReservationDetails reservationDetails,
                                    CustomerInfo customerInfo, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.reservationId = reservationId;
            this.reservationNumber = reservationNumber;
            this.status = status;
            this.businessInfo = businessInfo;
            this.reservationDetails = reservationDetails;
            this.customerInfo = customerInfo;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static ReservationDetail of(UUID reservationId, String reservationNumber, ReservationStatus status,
                                            BusinessInfo businessInfo, ReservationDetails reservationDetails,
                                            CustomerInfo customerInfo, LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new ReservationDetail(reservationId, reservationNumber, status, businessInfo,
                    reservationDetails, customerInfo, createdAt, updatedAt);
        }
    }

    /**
     * 업체 정보
     */
    @Getter
    public static class BusinessInfo {
        private final UUID businessId;
        private final String businessName;
        private final String address;
        private final String contactPhone;

        private BusinessInfo(UUID businessId, String businessName, String address, String contactPhone) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.address = address;
            this.contactPhone = contactPhone;
        }

        public static BusinessInfo of(UUID businessId, String businessName, String address, String contactPhone) {
            return new BusinessInfo(businessId, businessName, address, contactPhone);
        }
    }

    /**
     * 예약 세부 정보
     */
    @Getter
    public static class ReservationDetails {
        private final LocalDate date;
        private final LocalTime time;
        private final Integer durationMinutes;
        private final List<SelectedOptionInfo> selectedOptions;
        private final Integer totalPrice;
        private final String notes;

        private ReservationDetails(LocalDate date, LocalTime time, Integer durationMinutes,
                                    List<SelectedOptionInfo> selectedOptions, Integer totalPrice, String notes) {
            this.date = date;
            this.time = time;
            this.durationMinutes = durationMinutes;
            this.selectedOptions = selectedOptions;
            this.totalPrice = totalPrice;
            this.notes = notes;
        }

        public static ReservationDetails of(LocalDate date, LocalTime time, Integer durationMinutes,
                                            List<SelectedOptionInfo> selectedOptions, Integer totalPrice, String notes) {
            return new ReservationDetails(date, time, durationMinutes, selectedOptions, totalPrice, notes);
        }
    }

    /**
     * 선택된 옵션 정보
     */
    @Getter
    public static class SelectedOptionInfo {
        private final UUID optionId;
        private final String optionName;
        private final Integer price;

        private SelectedOptionInfo(UUID optionId, String optionName, Integer price) {
            this.optionId = optionId;
            this.optionName = optionName;
            this.price = price;
        }

        public static SelectedOptionInfo of(UUID optionId, String optionName, Integer price) {
            return new SelectedOptionInfo(optionId, optionName, price);
        }
    }

    /**
     * 고객 정보
     */
    @Getter
    public static class CustomerInfo {
        private final UUID customerId;
        private final String customerName;
        private final String customerPhone;

        private CustomerInfo(UUID customerId, String customerName, String customerPhone) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
        }

        public static CustomerInfo of(UUID customerId, String customerName, String customerPhone) {
            return new CustomerInfo(customerId, customerName, customerPhone);
        }
    }
}