package timefit.reservation.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class ReservationServiceDto {

    /**
     * 업체 정보 (서비스 내부용)
     */
    public record BusinessInfo(
            UUID businessId,
            String businessName,
            String address,
            String contactPhone,
            String logoUrl
    ) {
        public static BusinessInfo of(
                UUID businessId,
                String businessName,
                String address,
                String contactPhone,
                String logoUrl) {
            return new BusinessInfo(businessId, businessName, address, contactPhone, logoUrl);
        }

        public static BusinessInfo ofBasic(
                UUID businessId,
                String businessName,
                String logoUrl) {
            return new BusinessInfo(businessId, businessName, null, null, logoUrl);
        }
    }

    /**
     * 고객 정보 (서비스 내부용)
     */
    public record CustomerInfo(
            UUID customerId,
            String customerName,
            String customerPhone,
            String customerEmail
    ) {
        public static CustomerInfo of(
                UUID customerId,
                String customerName,
                String customerPhone,
                String customerEmail) {
            return new CustomerInfo(customerId, customerName, customerPhone, customerEmail);
        }

        public static CustomerInfo ofBasic(
                UUID customerId,
                String customerName,
                String customerPhone) {
            return new CustomerInfo(customerId, customerName, customerPhone, null);
        }
    }

    /**
     * 예약 상세 정보 (서비스 내부용)
     */
    public record ReservationInfo(
            LocalDate date,
            LocalTime time,
            Integer durationMinutes,
            Integer totalPrice,
            String menuName
    ) {
        public static ReservationInfo of(
                LocalDate date,
                LocalTime time,
                Integer durationMinutes,
                Integer totalPrice,
                String menuName) {
            return new ReservationInfo(date, time, durationMinutes, totalPrice, menuName);
        }
    }
}