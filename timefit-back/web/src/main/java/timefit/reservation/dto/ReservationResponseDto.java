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

    /**
     * 예약 목록 조회 결과 응답
     */
    @Getter
    public static class ReservationListResult {
        private final List<ReservationSummary> reservations;
        private final PaginationInfo pagination;

        private ReservationListResult(List<ReservationSummary> reservations, PaginationInfo pagination) {
            this.reservations = reservations;
            this.pagination = pagination;
        }

        public static ReservationListResult of(List<ReservationSummary> reservations, PaginationInfo pagination) {
            return new ReservationListResult(reservations, pagination);
        }
    }

    /**
     * 예약 요약 정보
     */
    @Getter
    public static class ReservationSummary {
        private final UUID reservationId;
        private final String reservationNumber;
        private final ReservationStatus status;
        private final BusinessSummaryInfo businessInfo;
        private final ReservationSummaryDetails reservationDetails;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private ReservationSummary(UUID reservationId, String reservationNumber, ReservationStatus status,
                                    BusinessSummaryInfo businessInfo, ReservationSummaryDetails reservationDetails,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.reservationId = reservationId;
            this.reservationNumber = reservationNumber;
            this.status = status;
            this.businessInfo = businessInfo;
            this.reservationDetails = reservationDetails;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static ReservationSummary of(UUID reservationId, String reservationNumber, ReservationStatus status,
                                            BusinessSummaryInfo businessInfo, ReservationSummaryDetails reservationDetails,
                                            LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new ReservationSummary(reservationId, reservationNumber, status, businessInfo,
                    reservationDetails, createdAt, updatedAt);
        }
    }

    /**
     * 업체 요약 정보
     */
    @Getter
    public static class BusinessSummaryInfo {
        private final UUID businessId;
        private final String businessName;
        private final String logoUrl;

        private BusinessSummaryInfo(UUID businessId, String businessName, String logoUrl) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.logoUrl = logoUrl;
        }

        public static BusinessSummaryInfo of(UUID businessId, String businessName, String logoUrl) {
            return new BusinessSummaryInfo(businessId, businessName, logoUrl);
        }
    }

    /**
     * 예약 요약 세부 정보
     */
    @Getter
    public static class ReservationSummaryDetails {
        private final LocalDate date;
        private final LocalTime time;
        private final Integer durationMinutes;
        private final List<SelectedOptionInfo> selectedOptions;
        private final Integer totalPrice;

        private ReservationSummaryDetails(LocalDate date, LocalTime time, Integer durationMinutes,
                                            List<SelectedOptionInfo> selectedOptions, Integer totalPrice) {
            this.date = date;
            this.time = time;
            this.durationMinutes = durationMinutes;
            this.selectedOptions = selectedOptions;
            this.totalPrice = totalPrice;
        }

        public static ReservationSummaryDetails of(LocalDate date, LocalTime time, Integer durationMinutes,
                                                    List<SelectedOptionInfo> selectedOptions, Integer totalPrice) {
            return new ReservationSummaryDetails(date, time, durationMinutes, selectedOptions, totalPrice);
        }
    }

    /**
     * 페이지네이션 정보
     */
    @Getter
    public static class PaginationInfo {
        private final Integer currentPage;
        private final Integer totalPages;
        private final Long totalElements;
        private final Integer size;
        private final Boolean hasNext;
        private final Boolean hasPrevious;

        private PaginationInfo(Integer currentPage, Integer totalPages, Long totalElements, Integer size,
                                Boolean hasNext, Boolean hasPrevious) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.size = size;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        public static PaginationInfo of(Integer currentPage, Integer totalPages, Long totalElements, Integer size,
                                        Boolean hasNext, Boolean hasPrevious) {
            return new PaginationInfo(currentPage, totalPages, totalElements, size, hasNext, hasPrevious);
        }
    }
}