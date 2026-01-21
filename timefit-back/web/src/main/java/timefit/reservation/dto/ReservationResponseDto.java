package timefit.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import timefit.menu.entity.OrderType;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "예약 응답")
public class ReservationResponseDto {

    /**
     * 고객용 예약 상세 (단수)
     */
    @Schema(description = "고객용 예약 상세")
    public record CustomerReservation(
            @Schema(
                    description = "예약 ID",
                    example = "10000000-0000-0000-0000-000000000001"
            )
            UUID reservationId,

            @Schema(
                    description = "예약 번호",
                    example = "R20251123001"
            )
            String reservationNumber,

            @Schema(
                    description = "예약 상태",
                    example = "PENDING",
                    allowableValues = {"PENDING", "CONFIRMED", "REJECTED", "CANCELLED", "COMPLETED", "NO_SHOW"}
            )
            ReservationStatus status,

            @Schema(
                    description = "생성 일시",
                    example = "2025-11-23T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "최종 수정 일시",
                    example = "2025-11-23T15:30:00"
            )
            LocalDateTime updatedAt,

            @Schema(
                    description = "취소 일시",
                    example = "2025-11-23T14:00:00",
                    nullable = true
            )
            LocalDateTime cancelledAt,

            @Schema(
                    description = "업체 ID",
                    example = "30000000-0000-0000-0000-000000000001"
            )
            UUID businessId,

            @Schema(
                    description = "업체명",
                    example = "강남 헤어샵"
            )
            String businessName,

            @Schema(
                    description = "업체 주소",
                    example = "서울시 강남구 강남대로 123"
            )
            String businessAddress,

            @Schema(
                    description = "업체 연락처",
                    example = "02-1111-1111"
            )
            String businessContactPhone,

            @Schema(
                    description = "업체 로고 URL",
                    example = "https://example.com/logo.jpg",
                    nullable = true
            )
            String businessLogoUrl,

            @Schema(
                    description = "예약 날짜",
                    example = "2025-01-10"
            )
            LocalDate reservationDate,

            @Schema(
                    description = "예약 시간",
                    example = "09:00:00"
            )
            LocalTime reservationTime,

            @Schema(
                    description = "예약 금액",
                    example = "30000"
            )
            Integer reservationPrice,

            @Schema(
                    description = "서비스 시간 (분)",
                    example = "60"
            )
            Integer reservationDuration,

            @Schema(
                    description = "메뉴/서비스명",
                    example = "헤어 컷"
            )
            String menuServiceName,

            @Schema(
                    description = "메모",
                    example = "처음 방문입니다",
                    nullable = true
            )
            String notes,

            @Schema(
                    description = "예약자 이름 (스냅샷)",
                    example = "Owner Kim"
            )
            String customerNameSnapshot,

            @Schema(
                    description = "예약자 연락처 (스냅샷)",
                    example = "010-1111-1111"
            )
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
     * 고객 예약 목록 (복수)
     */
    @Schema(description = "고객용 예약 목록")
    public record CustomerReservationList(
            @Schema(description = "예약 배열")
            List<CustomerReservationItem> reservations,

            @Schema(description = "페이지네이션 정보")
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
    @Schema(description = "고객용 예약 목록 아이템")
    public record CustomerReservationItem(
            @Schema(
                    description = "예약 ID",
                    example = "10000000-0000-0000-0000-000000000001"
            )
            UUID reservationId,

            @Schema(
                    description = "예약 번호",
                    example = "R20251123001"
            )
            String reservationNumber,

            @Schema(
                    description = "예약 상태",
                    example = "CONFIRMED",
                    allowableValues = {"PENDING", "CONFIRMED", "REJECTED", "CANCELLED", "COMPLETED", "NO_SHOW"}
            )
            ReservationStatus status,

            @Schema(
                    description = "업체 ID",
                    example = "30000000-0000-0000-0000-000000000001"
            )
            UUID businessId,

            @Schema(
                    description = "업체명",
                    example = "강남 헤어샵"
            )
            String businessName,

            @Schema(
                    description = "업체 로고 URL",
                    example = "https://example.com/logo.jpg",
                    nullable = true
            )
            String businessLogoUrl,

            @Schema(
                    description = "예약 날짜",
                    example = "2025-01-10"
            )
            LocalDate reservationDate,

            @Schema(
                    description = "예약 시간",
                    example = "09:00:00"
            )
            LocalTime reservationTime,

            @Schema(
                    description = "서비스 시간 (분)",
                    example = "60"
            )
            Integer reservationDuration,

            @Schema(
                    description = "예약 금액",
                    example = "30000"
            )
            Integer reservationPrice,

            @Schema(
                    description = "생성 일시",
                    example = "2025-11-23T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "최종 수정 일시",
                    example = "2025-11-23T15:30:00"
            )
            LocalDateTime updatedAt
    ) {
        public static CustomerReservationItem of(
                UUID reservationId,
                String reservationNumber,
                ReservationStatus status,
                UUID businessId,
                String businessName,
                String businessLogoUrl,
                LocalDate reservationDate,
                LocalTime reservationTime,
                Integer reservationDuration,
                Integer reservationPrice,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
            return new CustomerReservationItem(
                    reservationId, reservationNumber, status,
                    businessId, businessName, businessLogoUrl,
                    reservationDate, reservationTime,
                    reservationDuration, reservationPrice,
                    createdAt, updatedAt
            );
        }
    }

    /**
     * 업체용 예약 상세 (단수)
     */
    @Schema(description = "업체용 예약 상세")
    public record BusinessReservation(
            @Schema(
                    description = "예약 ID",
                    example = "10000000-0000-0000-0000-000000000001"
            )
            UUID reservationId,

            @Schema(
                    description = "예약 번호",
                    example = "R20251123001"
            )
            String reservationNumber,

            @Schema(
                    description = "예약 상태",
                    example = "PENDING",
                    allowableValues = {"PENDING", "CONFIRMED", "REJECTED", "CANCELLED", "COMPLETED", "NO_SHOW"}
            )
            ReservationStatus status,

            @Schema(
                    description = "생성 일시",
                    example = "2025-11-23T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "최종 수정 일시",
                    example = "2025-11-23T15:30:00"
            )
            LocalDateTime updatedAt,

            @Schema(
                    description = "취소 일시",
                    example = "2025-11-23T14:00:00",
                    nullable = true
            )
            LocalDateTime cancelledAt,

            @Schema(
                    description = "업체 ID",
                    example = "30000000-0000-0000-0000-000000000001"
            )
            UUID businessId,

            @Schema(
                    description = "업체명",
                    example = "강남 헤어샵"
            )
            String businessName,

            @Schema(
                    description = "업체 주소",
                    example = "서울시 강남구 강남대로 123"
            )
            String businessAddress,

            @Schema(
                    description = "업체 연락처",
                    example = "02-1111-1111"
            )
            String businessContactPhone,

            @Schema(
                    description = "고객 ID",
                    example = "550e8400-e29b-41d4-a716-446655440005"
            )
            UUID customerId,

            @Schema(
                    description = "고객 이름",
                    example = "Owner Kim"
            )
            String customerName,

            @Schema(
                    description = "고객 연락처",
                    example = "010-1111-1111"
            )
            String customerPhone,

            @Schema(
                    description = "고객 이메일",
                    example = "customer@example.com",
                    nullable = true
            )
            String customerEmail,

            @Schema(
                    description = "메뉴 ID",
                    example = "60000000-0000-0000-0000-000000000001"
            )
            UUID menuId,

            @Schema(
                    description = "메뉴/서비스명",
                    example = "헤어 컷"
            )
            String menuServiceName,

            @Schema(
                    description = "메뉴 카테고리 코드",
                    example = "헤어"
            )
            String menuCategoryCode,

            @Schema(
                    description = "메뉴 가격",
                    example = "30000"
            )
            Integer menuPrice,

            @Schema(
                    description = "메뉴 설명",
                    example = "기본 헤어 컷 서비스",
                    nullable = true
            )
            String menuDescription,

            @Schema(
                    description = "메뉴 서비스 유형",
                    example = "RESERVATION_BASED",
                    allowableValues = {"RESERVATION_BASED", "ONDEMAND_BASED"}
            )
            OrderType menuOrderType,

            @Schema(
                    description = "메뉴 소요 시간 (분)",
                    example = "60",
                    nullable = true
            )
            Integer menuDurationMinutes,

            @Schema(
                    description = "메뉴 이미지 URL",
                    example = "https://example.com/menu.jpg",
                    nullable = true
            )
            String menuImageUrl,

            @Schema(
                    description = "메뉴 활성화 상태",
                    example = "true"
            )
            Boolean menuIsActive,

            @Schema(
                    description = "예약 날짜",
                    example = "2025-01-10"
            )
            LocalDate reservationDate,

            @Schema(
                    description = "예약 시간",
                    example = "09:00:00"
            )
            LocalTime reservationTime,

            @Schema(
                    description = "슬롯 ID",
                    example = "50000000-0000-0000-0000-000000000001",
                    nullable = true
            )
            UUID bookingSlotId,

            @Schema(
                    description = "예약 금액",
                    example = "30000"
            )
            Integer reservationPrice,

            @Schema(
                    description = "서비스 시간 (분)",
                    example = "60"
            )
            Integer reservationDuration,

            @Schema(
                    description = "예약자 이름 (스냅샷)",
                    example = "Owner Kim"
            )
            String customerNameSnapshot,

            @Schema(
                    description = "예약자 연락처 (스냅샷)",
                    example = "010-1111-1111"
            )
            String customerPhoneSnapshot,

            @Schema(
                    description = "메모",
                    example = "처음 방문입니다",
                    nullable = true
            )
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
                    reservation.getMenu().getBusinessCategory().getCategoryName(),
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

    @Schema(description = "업체용 예약 목록")
    public record BusinessReservationList(
            @Schema(
                    description = "업체 ID",
                    example = "30000000-0000-0000-0000-000000000001"
            )
            UUID businessId,

            @Schema(
                    description = "업체명",
                    example = "강남 헤어샵"
            )
            String businessName,

            @Schema(
                    description = "업체 주소",
                    example = "서울시 강남구 강남대로 123"
            )
            String businessAddress,

            @Schema(
                    description = "업체 연락처",
                    example = "02-1111-1111"
            )
            String businessContactPhone,

            @Schema(description = "예약 배열")
            List<BusinessReservationItem> reservations,

            @Schema(description = "페이지네이션 정보")
            PaginationInfo pagination
    ) {
        public static BusinessReservationList of(
                UUID businessId,
                String businessName,
                String businessAddress,
                String businessContactPhone,
                List<BusinessReservationItem> reservations,
                PaginationInfo pagination) {
            return new BusinessReservationList(
                    businessId, businessName, businessAddress, businessContactPhone,
                    reservations, pagination
            );
        }
    }

    @Schema(description = "업체용 예약 목록 아이템")
    public record BusinessReservationItem(
            @Schema(
                    description = "예약 ID",
                    example = "10000000-0000-0000-0000-000000000001"
            )
            UUID reservationId,

            @Schema(
                    description = "예약 번호",
                    example = "R20251123001"
            )
            String reservationNumber,

            @Schema(
                    description = "예약 상태",
                    example = "PENDING",
                    allowableValues = {"PENDING", "CONFIRMED", "REJECTED", "CANCELLED", "COMPLETED", "NO_SHOW"}
            )
            ReservationStatus status,

            @Schema(
                    description = "고객 ID",
                    example = "550e8400-e29b-41d4-a716-446655440005"
            )
            UUID customerId,

            @Schema(
                    description = "고객 이름",
                    example = "Owner Kim"
            )
            String customerName,

            @Schema(
                    description = "고객 연락처",
                    example = "010-1111-1111"
            )
            String customerPhone,

            @Schema(
                    description = "예약 날짜",
                    example = "2025-01-10"
            )
            LocalDate reservationDate,

            @Schema(
                    description = "예약 시간",
                    example = "09:00:00"
            )
            LocalTime reservationTime,

            @Schema(
                    description = "서비스 시간 (분)",
                    example = "60"
            )
            Integer reservationDuration,

            @Schema(
                    description = "예약 금액",
                    example = "30000"
            )
            Integer reservationPrice,

            @Schema(
                    description = "생성 일시",
                    example = "2025-11-23T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "조치 필요 여부 (PENDING 상태 등)",
                    example = "true"
            )
            Boolean requiresAction
    ) {
        public static BusinessReservationItem of(
                UUID reservationId,
                String reservationNumber,
                ReservationStatus status,
                UUID customerId,
                String customerName,
                String customerPhone,
                LocalDate reservationDate,
                LocalTime reservationTime,
                Integer reservationDuration,
                Integer reservationPrice,
                LocalDateTime createdAt,
                Boolean requiresAction) {
            return new BusinessReservationItem(
                    reservationId, reservationNumber, status,
                    customerId, customerName, customerPhone,
                    reservationDate, reservationTime,
                    reservationDuration, reservationPrice,
                    createdAt, requiresAction
            );
        }
    }

    @Schema(description = "예약 액션 결과 (승인/거절/취소/완료/노쇼)")
    public record ReservationActionResult(
            @Schema(
                    description = "예약 ID",
                    example = "10000000-0000-0000-0000-000000000001"
            )
            UUID reservationId,

            @Schema(
                    description = "이전 상태",
                    example = "PENDING",
                    allowableValues = {"PENDING", "CONFIRMED", "REJECTED", "CANCELLED", "COMPLETED", "NO_SHOW"}
            )
            ReservationStatus previousStatus,

            @Schema(
                    description = "현재 상태",
                    example = "CONFIRMED",
                    allowableValues = {"PENDING", "CONFIRMED", "REJECTED", "CANCELLED", "COMPLETED", "NO_SHOW"}
            )
            ReservationStatus currentStatus,

            @Schema(
                    description = "결과 메시지",
                    example = "예약이 확정되었습니다"
            )
            String message,

            @Schema(
                    description = "액션 수행 일시",
                    example = "2025-11-23T15:30:00"
            )
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
    // 4. 공통 DTO
    // ========================================

    /**
     * 페이지네이션 정보
     */
    @Schema(description = "페이지네이션 정보")
    public record PaginationInfo(
            @Schema(
                    description = "현재 페이지 번호 (0부터 시작)",
                    example = "0"
            )
            Integer currentPage,

            @Schema(
                    description = "전체 페이지 수",
                    example = "5"
            )
            Integer totalPages,

            @Schema(
                    description = "전체 요소 수",
                    example = "87"
            )
            Long totalElements,

            @Schema(
                    description = "페이지 크기",
                    example = "20"
            )
            Integer size,

            @Schema(
                    description = "다음 페이지 존재 여부",
                    example = "true"
            )
            Boolean hasNext,

            @Schema(
                    description = "이전 페이지 존재 여부",
                    example = "false"
            )
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