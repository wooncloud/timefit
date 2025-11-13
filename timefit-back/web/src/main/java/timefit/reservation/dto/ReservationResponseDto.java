package timefit.reservation.dto;

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
    public record CustomerReservationList(
            List<CustomerReservationItem> reservations,
            PaginationInfo pagination
    ) {
        public static CustomerReservationList of(
                List<CustomerReservationItem> reservations,
                PaginationInfo pagination) {
            return new CustomerReservationList(reservations, pagination);
        }
    }

    /**
     * 고객 예약 목록 아이템
     */
    public record CustomerReservationItem(
            UUID reservationId,
            String reservationNumber,
            ReservationStatus status,
            BusinessSummaryInfo businessInfo,
            ReservationSummaryDetails reservationDetails,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static CustomerReservationItem of(
                UUID reservationId,
                String reservationNumber,
                ReservationStatus status,
                BusinessSummaryInfo businessInfo,
                ReservationSummaryDetails reservationDetails,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
            return new CustomerReservationItem(
                    reservationId, reservationNumber, status,
                    businessInfo, reservationDetails,
                    createdAt, updatedAt
            );
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
    public record BusinessReservationList(
            BusinessInfo businessInfo,
            List<BusinessReservationItem> reservations,
            PaginationInfo pagination
    ) {
        public static BusinessReservationList of(
                BusinessInfo businessInfo,
                List<BusinessReservationItem> reservations,
                PaginationInfo pagination) {
            return new BusinessReservationList(businessInfo, reservations, pagination);
        }
    }

    /**
     * 업체 예약 목록 아이템
     */
    public record BusinessReservationItem(
            UUID reservationId,
            String reservationNumber,
            ReservationStatus status,
            CustomerSummaryInfo customerInfo,
            ReservationSummaryDetails reservationDetails,
            LocalDateTime createdAt,
            Boolean requiresAction
    ) {
        public static BusinessReservationItem of(
                UUID reservationId,
                String reservationNumber,
                ReservationStatus status,
                CustomerSummaryInfo customerInfo,
                ReservationSummaryDetails reservationDetails,
                LocalDateTime createdAt,
                Boolean requiresAction) {
            return new BusinessReservationItem(
                    reservationId, reservationNumber, status,
                    customerInfo, reservationDetails,
                    createdAt, requiresAction
            );
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
    public record BusinessInfo(
            UUID businessId,
            String businessName,
            String address,
            String contactPhone
    ) {
        public static BusinessInfo of(
                UUID businessId,
                String businessName,
                String address,
                String contactPhone) {
            return new BusinessInfo(businessId, businessName, address, contactPhone);
        }
    }

    /**
     * 업체 정보 (간략)
     */
    public record BusinessSummaryInfo(
            UUID businessId,
            String businessName,
            String logoUrl
    ) {
        public static BusinessSummaryInfo of(
                UUID businessId,
                String businessName,
                String logoUrl) {
            return new BusinessSummaryInfo(businessId, businessName, logoUrl);
        }
    }

    /**
     * 고객 정보 (간략)
     */
    public record CustomerSummaryInfo(
            UUID customerId,
            String customerName,
            String customerPhone
    ) {
        public static CustomerSummaryInfo of(
                UUID customerId,
                String customerName,
                String customerPhone) {
            return new CustomerSummaryInfo(customerId, customerName, customerPhone);
        }
    }

    // 예약 상세 정보
    public record ReservationSummaryDetails(
            LocalDate date,
            LocalTime time,
            Integer durationMinutes,
            Integer totalPrice
    ) {
        public static ReservationSummaryDetails of(
                LocalDate date,
                LocalTime time,
                Integer durationMinutes,
                Integer totalPrice) {
            return new ReservationSummaryDetails(date, time, durationMinutes, totalPrice);
        }
    }

    // 페이지네이션 정보
    public record PaginationInfo(
            Integer currentPage,
            Integer totalPages,
            Long totalElements,
            Integer size,
            Boolean hasNext,
            Boolean hasPrevious
    ) {
        public static PaginationInfo of(
                Integer currentPage,
                Integer totalPages,
                Long totalElements,
                Integer size,
                Boolean hasNext,
                Boolean hasPrevious) {
            return new PaginationInfo(
                    currentPage, totalPages, totalElements,
                    size, hasNext, hasPrevious
            );
        }
    }
}