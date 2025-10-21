package timefit.reservation.dto;

import lombok.Getter;
import timefit.common.entity.BusinessRole;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Reservation Response DTO
 * 예약 관련 모든 응답 DTO를 포함
 */
public class ReservationResponseDto {

    // ========================================
    // 1. 주요 응답 DTO
    // ========================================

    /**
     * 예약 상세 정보 응답 (생성 시 사용)
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

        /**
         * Entity로부터 DTO 생성
         */
        public static ReservationDetail from(Reservation reservation) {
            return ReservationDetail.of(
                    reservation.getId(),
                    reservation.getReservationNumber(),
                    reservation.getStatus(),
                    BusinessInfo.of(
                            reservation.getBusiness().getId(),
                            reservation.getBusiness().getBusinessName(),
                            reservation.getBusiness().getAddress(),
                            reservation.getBusiness().getContactPhone()
                    ),
                    ReservationDetails.of(
                            reservation.getReservationDate(),
                            reservation.getReservationTime(),
                            reservation.getReservationDuration(),
                            List.of(), // selectedOptions - 추후 구현
                            reservation.getReservationPrice(),
                            reservation.getNotes()
                    ),
                    CustomerInfo.of(
                            reservation.getCustomer().getId(),
                            reservation.getCustomerName(),
                            reservation.getCustomerPhone()
                    ),
                    reservation.getCreatedAt(),
                    reservation.getUpdatedAt()
            );
        }
    }

    /**
     * 예약 상세 정보 + 수정/취소 가능 여부 (조회 시 사용)
     */
    @Getter
    public static class ReservationDetailWithHistory {
        private final UUID reservationId;
        private final String reservationNumber;
        private final ReservationStatus status;
        private final BusinessInfo businessInfo;
        private final ReservationDetails reservationDetails;
        private final CustomerInfo customerInfo;
        private final Boolean canModify;
        private final Boolean canCancel;
        private final LocalDateTime cancelDeadline;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private ReservationDetailWithHistory(UUID reservationId, String reservationNumber, ReservationStatus status,
                                             BusinessInfo businessInfo, ReservationDetails reservationDetails,
                                             CustomerInfo customerInfo, Boolean canModify, Boolean canCancel,
                                             LocalDateTime cancelDeadline, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.reservationId = reservationId;
            this.reservationNumber = reservationNumber;
            this.status = status;
            this.businessInfo = businessInfo;
            this.reservationDetails = reservationDetails;
            this.customerInfo = customerInfo;
            this.canModify = canModify;
            this.canCancel = canCancel;
            this.cancelDeadline = cancelDeadline;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static ReservationDetailWithHistory of(UUID reservationId, String reservationNumber, ReservationStatus status,
                                                      BusinessInfo businessInfo, ReservationDetails reservationDetails,
                                                      CustomerInfo customerInfo, Boolean canModify, Boolean canCancel,
                                                      LocalDateTime cancelDeadline, LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new ReservationDetailWithHistory(reservationId, reservationNumber, status, businessInfo,
                    reservationDetails, customerInfo, canModify, canCancel, cancelDeadline, createdAt, updatedAt);
        }

        /**
         * Entity로부터 DTO 생성
         */
        public static ReservationDetailWithHistory from(Reservation reservation) {
            return ReservationDetailWithHistory.of(
                    reservation.getId(),
                    reservation.getReservationNumber(),
                    reservation.getStatus(),
                    BusinessInfo.of(
                            reservation.getBusiness().getId(),
                            reservation.getBusiness().getBusinessName(),
                            reservation.getBusiness().getAddress(),
                            reservation.getBusiness().getContactPhone()
                    ),
                    ReservationDetails.of(
                            reservation.getReservationDate(),
                            reservation.getReservationTime(),
                            reservation.getReservationDuration(),
                            List.of(), // selectedOptions - 추후 구현
                            reservation.getReservationPrice(),
                            reservation.getNotes()
                    ),
                    CustomerInfo.of(
                            reservation.getCustomer().getId(),
                            reservation.getCustomerName(),
                            reservation.getCustomerPhone()
                    ),
                    reservation.isCancellable(), // canModify
                    reservation.isCancellable(), // canCancel
                    null, // cancelDeadline - 추후 구현
                    reservation.getCreatedAt(),
                    reservation.getUpdatedAt()
            );
        }
    }

    /**
     * 고객 예약 목록 조회 결과
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
     * 예약 요약 정보 (고객 목록용)
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
     * 업체용 예약 목록 조회 결과
     */
    @Getter
    public static class BusinessReservationListResult {
        private final BusinessInfo businessInfo;
        private final List<BusinessReservationSummary> reservations;
        private final PaginationInfo pagination;

        private BusinessReservationListResult(BusinessInfo businessInfo,
                                              List<BusinessReservationSummary> reservations,
                                              PaginationInfo pagination) {
            this.businessInfo = businessInfo;
            this.reservations = reservations;
            this.pagination = pagination;
        }

        public static BusinessReservationListResult of(BusinessInfo businessInfo,
                                                       List<BusinessReservationSummary> reservations,
                                                       PaginationInfo pagination) {
            return new BusinessReservationListResult(businessInfo, reservations, pagination);
        }
    }

    /**
     * 업체용 예약 요약 정보
     */
    @Getter
    public static class BusinessReservationSummary {
        private final UUID reservationId;
        private final String reservationNumber;
        private final ReservationStatus status;
        private final CustomerSummaryInfo customerInfo;
        private final ReservationSummaryDetails reservationDetails;
        private final LocalDateTime createdAt;
        private final Boolean requiresAction;

        private BusinessReservationSummary(UUID reservationId, String reservationNumber,
                                           ReservationStatus status, CustomerSummaryInfo customerInfo,
                                           ReservationSummaryDetails reservationDetails, LocalDateTime createdAt,
                                           Boolean requiresAction) {
            this.reservationId = reservationId;
            this.reservationNumber = reservationNumber;
            this.status = status;
            this.customerInfo = customerInfo;
            this.reservationDetails = reservationDetails;
            this.createdAt = createdAt;
            this.requiresAction = requiresAction;
        }

        public static BusinessReservationSummary of(UUID reservationId, String reservationNumber,
                                                    ReservationStatus status, CustomerSummaryInfo customerInfo,
                                                    ReservationSummaryDetails reservationDetails, LocalDateTime createdAt,
                                                    Boolean requiresAction) {
            return new BusinessReservationSummary(reservationId, reservationNumber, status, customerInfo,
                    reservationDetails, createdAt, requiresAction);
        }
    }

    /**
     * 예약 취소 결과
     */
    @Getter
    public static class ReservationCancelResult {
        private final UUID reservationId;
        private final ReservationStatus previousStatus;
        private final ReservationStatus currentStatus;
        private final String reason;
        private final LocalDateTime cancelledAt;

        private ReservationCancelResult(UUID reservationId, ReservationStatus previousStatus,
                                        ReservationStatus currentStatus, String reason, LocalDateTime cancelledAt) {
            this.reservationId = reservationId;
            this.previousStatus = previousStatus;
            this.currentStatus = currentStatus;
            this.reason = reason;
            this.cancelledAt = cancelledAt;
        }

        public static ReservationCancelResult of(UUID reservationId, ReservationStatus previousStatus,
                                                 ReservationStatus currentStatus, String reason, LocalDateTime cancelledAt) {
            return new ReservationCancelResult(reservationId, previousStatus, currentStatus, reason, cancelledAt);
        }

        /**
         * Entity로부터 DTO 생성
         */
        public static ReservationCancelResult from(Reservation reservation, String reason) {
            return ReservationCancelResult.of(
                    reservation.getId(),
                    ReservationStatus.CONFIRMED, // previousStatus - 추후 개선
                    reservation.getStatus(),
                    reason,
                    reservation.getCancelledAt()
            );
        }
    }

    /**
     * 예약 상태 변경 결과 (승인/거절)
     */
    @Getter
    public static class ReservationStatusChangeResult {
        private final UUID reservationId;
        private final ReservationStatus previousStatus;
        private final ReservationStatus currentStatus;
        private final String message;
        private final UpdatedByInfo updatedBy;
        private final LocalDateTime updatedAt;

        private ReservationStatusChangeResult(UUID reservationId, ReservationStatus previousStatus,
                                              ReservationStatus currentStatus, String message,
                                              UpdatedByInfo updatedBy, LocalDateTime updatedAt) {
            this.reservationId = reservationId;
            this.previousStatus = previousStatus;
            this.currentStatus = currentStatus;
            this.message = message;
            this.updatedBy = updatedBy;
            this.updatedAt = updatedAt;
        }

        public static ReservationStatusChangeResult of(UUID reservationId, ReservationStatus previousStatus,
                                                       ReservationStatus currentStatus, String message,
                                                       UpdatedByInfo updatedBy, LocalDateTime updatedAt) {
            return new ReservationStatusChangeResult(reservationId, previousStatus, currentStatus,
                    message, updatedBy, updatedAt);
        }

        /**
         * Entity로부터 DTO 생성
         */
        public static ReservationStatusChangeResult from(Reservation reservation, String message) {
            return ReservationStatusChangeResult.of(
                    reservation.getId(),
                    ReservationStatus.PENDING, // previousStatus - 추후 개선
                    reservation.getStatus(),
                    message,
                    null, // updatedBy - 추후 구현
                    reservation.getUpdatedAt()
            );
        }
    }

    /**
     * 예약 완료/노쇼 처리 결과 (업체용)
     */
    @Getter
    public static class ReservationCompletionResult {
        private final UUID reservationId;
        private final ReservationStatus previousStatus;
        private final ReservationStatus currentStatus;
        private final String message;
        private final LocalDateTime completedAt;

        private ReservationCompletionResult(UUID reservationId, ReservationStatus previousStatus,
                                            ReservationStatus currentStatus, String message,
                                            LocalDateTime completedAt) {
            this.reservationId = reservationId;
            this.previousStatus = previousStatus;
            this.currentStatus = currentStatus;
            this.message = message;
            this.completedAt = completedAt;
        }

        public static ReservationCompletionResult of(UUID reservationId, ReservationStatus previousStatus,
                                                     ReservationStatus currentStatus, String message,
                                                     LocalDateTime completedAt) {
            return new ReservationCompletionResult(reservationId, previousStatus, currentStatus,
                    message, completedAt);
        }

        /**
         * Entity로부터 DTO 생성
         */
        public static ReservationCompletionResult from(Reservation reservation, String message) {
            return ReservationCompletionResult.of(
                    reservation.getId(),
                    ReservationStatus.CONFIRMED, // previousStatus - 추후 개선
                    reservation.getStatus(),
                    message,
                    reservation.getUpdatedAt()
            );
        }
    }

    /**
     * 예약 캘린더 조회 결과
     */
    @Getter
    public static class ReservationCalendarResult {
        private final BusinessInfo businessInfo;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final List<DailyReservationSummary> dailyReservations;

        private ReservationCalendarResult(BusinessInfo businessInfo, LocalDate startDate, LocalDate endDate,
                                          List<DailyReservationSummary> dailyReservations) {
            this.businessInfo = businessInfo;
            this.startDate = startDate;
            this.endDate = endDate;
            this.dailyReservations = dailyReservations;
        }

        public static ReservationCalendarResult of(BusinessInfo businessInfo, LocalDate startDate, LocalDate endDate,
                                                   List<DailyReservationSummary> dailyReservations) {
            return new ReservationCalendarResult(businessInfo, startDate, endDate, dailyReservations);
        }
    }

    /**
     * 일별 예약 요약 (캘린더용)
     */
    @Getter
    public static class DailyReservationSummary {
        private final LocalDate date;
        private final Integer totalCount;
        private final Integer pendingCount;
        private final Integer confirmedCount;
        private final Integer completedCount;
        private final List<ReservationSummary> reservations;

        private DailyReservationSummary(LocalDate date, Integer totalCount, Integer pendingCount,
                                        Integer confirmedCount, Integer completedCount,
                                        List<ReservationSummary> reservations) {
            this.date = date;
            this.totalCount = totalCount;
            this.pendingCount = pendingCount;
            this.confirmedCount = confirmedCount;
            this.completedCount = completedCount;
            this.reservations = reservations;
        }

        public static DailyReservationSummary of(LocalDate date, Integer totalCount, Integer pendingCount,
                                                 Integer confirmedCount, Integer completedCount,
                                                 List<ReservationSummary> reservations) {
            return new DailyReservationSummary(date, totalCount, pendingCount, confirmedCount,
                    completedCount, reservations);
        }
    }

    // ========================================
    // 2. 내부 정보 DTO (재사용)
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
     * 업체 요약 정보 (목록용)
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
     * 고객 정보 (상세)
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
     * 고객 요약 정보 (목록용)
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
     * 예약 세부 정보 (상세)
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
     * 예약 요약 세부 정보 (목록용 - notes 없음)
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

    /**
     * 상태 변경자 정보 (업체 직원)
     */
    @Getter
    public static class UpdatedByInfo {
        private final UUID userId;
        private final String userName;
        private final BusinessRole role;

        private UpdatedByInfo(UUID userId, String userName, BusinessRole role) {
            this.userId = userId;
            this.userName = userName;
            this.role = role;
        }

        public static UpdatedByInfo of(UUID userId, String userName, BusinessRole role) {
            return new UpdatedByInfo(userId, userName, role);
        }
    }
}