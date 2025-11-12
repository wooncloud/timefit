package timefit.reservation.dto;

import lombok.Getter;
import timefit.menu.entity.OrderType;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class ReservationResponseDto {

    // ========================================
    // 1. 고객용 DTO
    // ========================================

    /**
     * 고객용 예약 상세 (단일)
     */
    public record CustomerReservation(
            // 예약 기본 정보
            UUID reservationId,
            String reservationNumber,
            ReservationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime cancelledAt,

            // 업체 정보
            UUID businessId,
            String businessName,
            String businessAddress,
            String businessContactPhone,
            String businessLogoUrl,

            // 예약 상세
            LocalDate reservationDate,
            LocalTime reservationTime,
            Integer reservationPrice,
            Integer reservationDuration,
            String menuServiceName,
            String notes,

            // 고객 정보 스냅샷
            String customerNameSnapshot,
            String customerPhoneSnapshot
    ) {
        public static CustomerReservation from(Reservation reservation) {
            return new CustomerReservation(
                    reservation.getId(),
                    reservation.getReservationNumber(),
                    reservation.getStatus(),
                    reservation.getCreatedAt(),
                    reservation.getUpdatedAt(),
                    reservation.getCancelledAt(),

                    reservation.getBusiness().getId(),
                    reservation.getBusiness().getBusinessName(),
                    reservation.getBusiness().getAddress(),
                    reservation.getBusiness().getContactPhone(),
                    reservation.getBusiness().getLogoUrl(),

                    reservation.getReservationDate(),
                    reservation.getReservationTime(),
                    reservation.getReservationPrice(),
                    reservation.getReservationDuration(),
                    reservation.getMenu().getServiceName(),
                    reservation.getNotes(),

                    reservation.getCustomerName(),
                    reservation.getCustomerPhone()
            );
        }
    }

    /**
     * 고객 예약 목록 응답
     */
    @Getter
    public static class CustomerReservationList {
        private final List<CustomerReservationItem> reservations;
        private final PaginationInfo pagination;

        private CustomerReservationList(List<CustomerReservationItem> reservations, PaginationInfo pagination) {
            this.reservations = reservations;
            this.pagination = pagination;
        }

        public static CustomerReservationList of(List<CustomerReservationItem> reservations, PaginationInfo pagination) {
            return new CustomerReservationList(reservations, pagination);
        }
    }

    /**
     * 고객 예약 목록 아이템
     */
    @Getter
    public static class CustomerReservationItem {
        private final UUID reservationId;
        private final String reservationNumber;
        private final ReservationStatus status;
        private final BusinessSummaryInfo businessInfo;
        private final ReservationSummaryDetails reservationDetails;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private CustomerReservationItem(UUID reservationId, String reservationNumber, ReservationStatus status,
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

        public static CustomerReservationItem of(UUID reservationId, String reservationNumber, ReservationStatus status,
                                                 BusinessSummaryInfo businessInfo, ReservationSummaryDetails reservationDetails,
                                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new CustomerReservationItem(reservationId, reservationNumber, status, businessInfo,
                    reservationDetails, createdAt, updatedAt);
        }
    }

    // ========================================
    // 2. 업체용 DTO
    // ========================================

    /**
     * 업체용 예약 상세
     */
    public record BusinessReservation(
            // 예약 기본 정보
            UUID reservationId,
            String reservationNumber,
            ReservationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime cancelledAt,

            // 업체 정보
            UUID businessId,
            String businessName,
            String businessAddress,
            String businessContactPhone,

            // 고객 정보
            UUID customerId,
            String customerName,
            String customerPhone,
            String customerEmail,

            // 메뉴 정보
            UUID menuId,
            String menuServiceName,
            String menuCategoryCode,
            Integer menuPrice,
            String menuDescription,
            OrderType menuOrderType,
            Integer menuDurationMinutes,
            String menuImageUrl,
            Boolean menuIsActive,

            // 예약 상세
            LocalDate reservationDate,
            LocalTime reservationTime,
            UUID bookingSlotId,
            Integer reservationPrice,
            Integer reservationDuration,
            String customerNameSnapshot,
            String customerPhoneSnapshot,
            String notes
    ) {
        public static BusinessReservation from(Reservation reservation) {
            User customer = reservation.getCustomer();

            return new BusinessReservation(
                    reservation.getId(),
                    reservation.getReservationNumber(),
                    reservation.getStatus(),
                    reservation.getCreatedAt(),
                    reservation.getUpdatedAt(),
                    reservation.getCancelledAt(),

                    reservation.getBusiness().getId(),
                    reservation.getBusiness().getBusinessName(),
                    reservation.getBusiness().getAddress(),
                    reservation.getBusiness().getContactPhone(),

                    customer.getId(),
                    customer.getName(),
                    customer.getPhoneNumber(),
                    customer.getEmail(),

                    reservation.getMenu().getId(),
                    reservation.getMenu().getServiceName(),
                    reservation.getMenu().getBusinessCategory().getCategoryCode().name(),
                    reservation.getMenu().getPrice(),
                    reservation.getMenu().getDescription(),
                    reservation.getMenu().getOrderType(),
                    reservation.getMenu().getDurationMinutes(),
                    reservation.getMenu().getImageUrl(),
                    reservation.getMenu().getIsActive(),

                    reservation.getReservationDate(),
                    reservation.getReservationTime(),
                    reservation.getBookingSlot() != null ? reservation.getBookingSlot().getId() : null,
                    reservation.getReservationPrice(),
                    reservation.getReservationDuration(),
                    reservation.getCustomerName(),
                    reservation.getCustomerPhone(),
                    reservation.getNotes()
            );
        }
    }

    /**
     * 업체 예약 목록 응답
     */
    @Getter
    public static class BusinessReservationList {
        private final BusinessInfo businessInfo;
        private final List<BusinessReservationItem> reservations;
        private final PaginationInfo pagination;

        private BusinessReservationList(BusinessInfo businessInfo, List<BusinessReservationItem> reservations,
                                        PaginationInfo pagination) {
            this.businessInfo = businessInfo;
            this.reservations = reservations;
            this.pagination = pagination;
        }

        public static BusinessReservationList of(BusinessInfo businessInfo, List<BusinessReservationItem> reservations,
                                                 PaginationInfo pagination) {
            return new BusinessReservationList(businessInfo, reservations, pagination);
        }
    }

    /**
     * 업체 예약 목록 아이템
     */
    @Getter
    public static class BusinessReservationItem {
        private final UUID reservationId;
        private final String reservationNumber;
        private final ReservationStatus status;
        private final CustomerSummaryInfo customerInfo;
        private final ReservationSummaryDetails reservationDetails;
        private final LocalDateTime createdAt;
        private final Boolean requiresAction;

        private BusinessReservationItem(UUID reservationId, String reservationNumber, ReservationStatus status,
                                        CustomerSummaryInfo customerInfo, ReservationSummaryDetails reservationDetails,
                                        LocalDateTime createdAt, Boolean requiresAction) {
            this.reservationId = reservationId;
            this.reservationNumber = reservationNumber;
            this.status = status;
            this.customerInfo = customerInfo;
            this.reservationDetails = reservationDetails;
            this.createdAt = createdAt;
            this.requiresAction = requiresAction;
        }

        public static BusinessReservationItem of(UUID reservationId, String reservationNumber,
                                                 ReservationStatus status, CustomerSummaryInfo customerInfo,
                                                 ReservationSummaryDetails reservationDetails, LocalDateTime createdAt,
                                                 Boolean requiresAction) {
            return new BusinessReservationItem(reservationId, reservationNumber, status, customerInfo,
                    reservationDetails, createdAt, requiresAction);
        }
    }

    // ========================================
    // 3. 액션 결과 DTO
    // ========================================

    /**
     * 예약 액션 결과 (승인/거절/취소/완료/노쇼)
     */
    public record ReservationActionResult(
            UUID reservationId,
            ReservationStatus previousStatus,
            ReservationStatus currentStatus,
            String message,
            LocalDateTime actionAt
    ) {
        public static ReservationActionResult of(
                Reservation reservation,
                ReservationStatus previousStatus,
                String message) {

            return new ReservationActionResult(
                    reservation.getId(),
                    previousStatus,
                    reservation.getStatus(),
                    message,
                    reservation.getUpdatedAt()
            );
        }

        public static ReservationActionResult ofCancel(
                Reservation reservation,
                ReservationStatus previousStatus,
                String message) {

            return new ReservationActionResult(
                    reservation.getId(),
                    previousStatus,
                    reservation.getStatus(),
                    message,
                    reservation.getCancelledAt()
            );
        }
    }

    // ========================================
    // 4. 공통 내부 DTO
    // ========================================

    /**
     * 업체 정보 (상세)
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
     * 업체 정보 (간략)
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
     * 고객 정보 (간략)
     */
    @Getter
    public static class CustomerSummaryInfo {
        private final UUID customerId;
        private final String customerName;
        private final String customerPhone;

        private CustomerSummaryInfo(UUID customerId, String customerName, String customerPhone) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
        }

        public static CustomerSummaryInfo of(UUID customerId, String customerName, String customerPhone) {
            return new CustomerSummaryInfo(customerId, customerName, customerPhone);
        }
    }

    /**
     * 예약 상세 정보
     */
    @Getter
    public static class ReservationSummaryDetails {
        private final LocalDate date;
        private final LocalTime time;
        private final Integer durationMinutes;
        private final Integer totalPrice;

        private ReservationSummaryDetails(LocalDate date, LocalTime time, Integer durationMinutes, Integer totalPrice) {
            this.date = date;
            this.time = time;
            this.durationMinutes = durationMinutes;
            this.totalPrice = totalPrice;
        }

        public static ReservationSummaryDetails of(LocalDate date, LocalTime time, Integer durationMinutes,
                                                   Integer totalPrice) {
            return new ReservationSummaryDetails(date, time, durationMinutes, totalPrice);
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