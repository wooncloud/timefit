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
import java.util.Map;
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

                    ReservationFieldResolver.resolveBusinessId(reservation),
                    ReservationFieldResolver.resolveBusinessName(reservation),
                    ReservationFieldResolver.resolveBusinessAddress(reservation),
                    ReservationFieldResolver.resolveBusinessContactPhone(reservation),
                    ReservationFieldResolver.resolveBusinessLogoUrl(reservation),

                    reservation.getReservationDate(),
                    reservation.getReservationTime(),
                    reservation.getReservationPrice(),
                    reservation.getReservationDuration(),
                    ReservationFieldResolver.resolveMenuName(reservation),
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

                    ReservationFieldResolver.resolveBusinessId(reservation),
                    ReservationFieldResolver.resolveBusinessName(reservation),
                    ReservationFieldResolver.resolveBusinessAddress(reservation),
                    ReservationFieldResolver.resolveBusinessContactPhone(reservation),

                    customer.getId(),
                    customer.getName(),
                    customer.getPhoneNumber(),
                    customer.getEmail(),

                    ReservationFieldResolver.resolveMenuId(reservation),
                    ReservationFieldResolver.resolveMenuName(reservation),
                    ReservationFieldResolver.resolveMenuCategoryName(reservation),
                    ReservationFieldResolver.resolveMenuPrice(reservation),
                    ReservationFieldResolver.resolveMenuDescription(reservation),
                    ReservationFieldResolver.resolveMenuOrderType(reservation),
                    ReservationFieldResolver.resolveMenuDurationMinutes(reservation),
                    ReservationFieldResolver.resolveMenuImageUrl(reservation),
                    ReservationFieldResolver.resolveMenuIsActive(reservation),

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

    public record ReservationStats(
            long pending,
            long confirmed,
            long completed,
            long cancelled,
            long noShow
    ) {
        public static ReservationStats of(Map<ReservationStatus, Long> counts) {
            return new ReservationStats(
                    counts.getOrDefault(ReservationStatus.PENDING, 0L),
                    counts.getOrDefault(ReservationStatus.CONFIRMED, 0L),
                    counts.getOrDefault(ReservationStatus.COMPLETED, 0L),
                    counts.getOrDefault(ReservationStatus.CANCELLED, 0L),
                    counts.getOrDefault(ReservationStatus.NO_SHOW, 0L)
            );
        }
    }

    // -----------------------------------



    /**
     * 업체 고객 목록 아이템 (예약 데이터 기반 집계)
     *
     * 집계 기준: status = COMPLETED 예약만
     */
    @Schema(description = "업체 고객 목록 아이템")
    public record CustomerListItem(
            @Schema(description = "고객 ID", example = "20000000-0000-0000-0000-000000000001")
            UUID customerId,

            @Schema(description = "고객 이름", example = "홍길동")
            String customerName,

            @Schema(description = "고객 연락처", example = "010-1234-5678")
            String customerPhone,

            @Schema(description = "총 방문 횟수 (COMPLETED 예약 기준)", example = "12")
            Long totalVisits,

            @Schema(description = "최근 방문일 (COMPLETED 예약 기준)", example = "2024-03-20")
            LocalDate lastVisitDate,

            @Schema(description = "첫 방문일 (COMPLETED 예약 기준)", example = "2024-01-15")
            LocalDate firstVisitDate,

            @Schema(description = "사업자 메모 (없으면 null)", example = "라떼 extra shot 선호", nullable = true)
            String memo
    ) {
        public static CustomerListItem of(
                UUID customerId, String customerName, String customerPhone,
                Long totalVisits, LocalDate lastVisitDate, LocalDate firstVisitDate,
                String memo) {
            return new CustomerListItem(
                    customerId, customerName, customerPhone,
                    totalVisits, lastVisitDate, firstVisitDate, memo
            );
        }
    }

    /**
     * 업체 고객 목록 (페이지네이션 포함)
     *
     * 사용처: GET /api/business/{businessId}/customers
     */
    @Schema(description = "업체 고객 목록")
    public record CustomerList(
            @Schema(description = "고객 배열")
            List<CustomerListItem> customers,

            @Schema(description = "페이지네이션 정보")
            PaginationInfo pagination
    ) {
        public static CustomerList of(List<CustomerListItem> customers, PaginationInfo pagination) {
            return new CustomerList(customers, pagination);
        }
    }

    /**
     * 고객 상세 - 기본 요약 (Repository → Service 전달용 내부 DTO)
     * - 고객 목록과 달리 이메일 포함
     * - Projections.constructor() 매핑 대상
     */
    @Schema(description = "고객 상세 요약 (내부용)")
    public record CustomerDetailSummary(
            UUID customerId,
            String customerName,
            String customerPhone,
            String customerEmail,
            Long totalVisits,
            LocalDate lastVisitDate,
            LocalDate firstVisitDate,
            String memo
    ) {
        public static CustomerDetailSummary of(
                UUID customerId, String customerName, String customerPhone,
                String customerEmail, Long totalVisits,
                LocalDate lastVisitDate, LocalDate firstVisitDate, String memo) {
            return new CustomerDetailSummary(
                    customerId, customerName, customerPhone, customerEmail,
                    totalVisits, lastVisitDate, firstVisitDate, memo
            );
        }
    }

    /**
     * 고객 상세 - 예약 이력 아이템
     * - COMPLETED 예약만 포함
     * - Projections.constructor() 매핑 대상
     */
    @Schema(description = "고객 예약 이력 아이템")
    public record ReservationHistoryItem(
            @Schema(description = "예약 ID")
            UUID reservationId,

            @Schema(description = "예약 날짜", example = "2024-03-20")
            LocalDate reservationDate,

            @Schema(description = "예약 시간", example = "10:00:00")
            LocalTime reservationTime,

            @Schema(description = "서비스명 (스냅샷)", example = "디자인 컷")
            String menuServiceName,

            @Schema(description = "예약 상태", example = "COMPLETED")
            ReservationStatus status,

            @Schema(description = "예약 금액", example = "30000")
            Integer reservationPrice
    ) {
        public static ReservationHistoryItem of(
                UUID reservationId, LocalDate reservationDate, LocalTime reservationTime,
                String menuServiceName, ReservationStatus status, Integer reservationPrice) {
            return new ReservationHistoryItem(
                    reservationId, reservationDate, reservationTime,
                    menuServiceName, status, reservationPrice
            );
        }
    }

    /**
     * 고객 상세 응답 (Service에서 조립 후 반환)
     *
     * 사용처: GET /api/business/{businessId}/customers/{customerId}
     */
    @Schema(description = "고객 상세")
    public record CustomerDetail(
            @Schema(description = "고객 ID")
            UUID customerId,

            @Schema(description = "고객 이름", example = "홍길동")
            String customerName,

            @Schema(description = "고객 연락처", example = "010-1234-5678")
            String customerPhone,

            @Schema(description = "고객 이메일", example = "hong@example.com", nullable = true)
            String customerEmail,

            @Schema(description = "총 방문 횟수", example = "12")
            Long totalVisits,

            @Schema(description = "최근 방문일", example = "2024-03-20")
            LocalDate lastVisitDate,

            @Schema(description = "첫 방문일", example = "2024-01-15")
            LocalDate firstVisitDate,

            @Schema(description = "사업자 메모", nullable = true)
            String memo,

            @Schema(description = "최근 예약 이력 (COMPLETED, 최신 10건)")
            List<ReservationHistoryItem> recentReservations
    ) {
        // CustomerDetailSummary + recentReservations 조합으로 생성
        public static CustomerDetail of(
                CustomerDetailSummary summary,
                List<ReservationHistoryItem> recentReservations) {
            return new CustomerDetail(
                    summary.customerId(), summary.customerName(), summary.customerPhone(),
                    summary.customerEmail(), summary.totalVisits(),
                    summary.lastVisitDate(), summary.firstVisitDate(), summary.memo(),
                    recentReservations
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