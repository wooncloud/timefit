package timefit.reservation.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

/**
 * Reservation Entity -> Response DTO 변환 전담
 * 역할:
 * - Entity to DTO 변환 로직 집중화
 * - Service 레이어 코드 간결화
 * - 재사용성 및 테스트 용이성 향상
 */
@Component
@RequiredArgsConstructor
public class ReservationConverter {

    /**
     * Entity -> CustomerReservation (단수 - 상세)
     */
    public ReservationResponseDto.CustomerReservation toCustomerReservation(Reservation reservation) {
        return ReservationResponseDto.CustomerReservation.from(reservation);
    }

    /**
     * Entity -> CustomerReservationItem (복수 - 목록용)
     */
    public ReservationResponseDto.CustomerReservationItem toCustomerReservationItem(Reservation reservation) {
        return ReservationResponseDto.CustomerReservationItem.of(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                // 업체 정보
                reservation.getBusiness().getId(),
                reservation.getBusiness().getBusinessName(),
                reservation.getBusiness().getLogoUrl(),
                // 예약 정보
                reservation.getReservationDate(),
                reservation.getReservationTime(),
                reservation.getReservationDuration(),
                reservation.getReservationPrice(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    /**
     * Entity -> BusinessReservation (단수 - 상세)
     */
    public ReservationResponseDto.BusinessReservation toBusinessReservation(Reservation reservation) {
        return ReservationResponseDto.BusinessReservation.from(reservation);
    }

    /**
     * Entity -> BusinessReservationItem (복수 - 목록용)
     */
    public ReservationResponseDto.BusinessReservationItem toBusinessReservationItem(Reservation reservation) {
        return ReservationResponseDto.BusinessReservationItem.of(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                // 고객 정보
                reservation.getCustomer().getId(),
                reservation.getCustomerName(),
                reservation.getCustomerPhone(),
                // 예약 정보
                reservation.getReservationDate(),
                reservation.getReservationTime(),
                reservation.getReservationDuration(),
                reservation.getReservationPrice(),
                reservation.getCreatedAt(),
                // 액션 필요 여부 (PENDING 상태만)
                reservation.getStatus() == ReservationStatus.PENDING
        );
    }

    /**
     * Page -> PaginationInfo
     */
    public ReservationResponseDto.PaginationInfo toPaginationInfo(Page<?> page) {
        return ReservationResponseDto.PaginationInfo.of(
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    /**
     * Entity + 상태 -> ReservationActionResult
     */
    public ReservationResponseDto.ReservationActionResult toActionResult(
            Reservation reservation,
            ReservationStatus previousStatus,
            String message) {
        return ReservationResponseDto.ReservationActionResult.of(
                reservation,
                previousStatus,
                message
        );
    }

    /**
     * Entity + 상태 -> ReservationActionResult (취소용)
     */
    public ReservationResponseDto.ReservationActionResult toCancelActionResult(
            Reservation reservation,
            ReservationStatus previousStatus,
            String message) {
        return ReservationResponseDto.ReservationActionResult.ofCancel(
                reservation,
                previousStatus,
                message
        );
    }
}