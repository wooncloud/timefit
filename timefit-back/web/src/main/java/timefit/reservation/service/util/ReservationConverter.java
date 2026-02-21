package timefit.reservation.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;

import java.util.ArrayList;
import java.util.List;
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

    // ========== 단일 변환 메서드 (기존) ==========
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

    // ========== List 변환 메서드 (신규 - for-loop 최적화) ==========

    /**
     * Entity List -> CustomerReservationItem List
     * @param reservations 예약 엔티티 리스트
     * @return 고객용 예약 아이템 리스트
     */
    public List<ReservationResponseDto.CustomerReservationItem> toCustomerReservationItems(
            List<Reservation> reservations) {

        if (reservations == null || reservations.isEmpty()) {
            return new ArrayList<>();
        }

        // ArrayList 사전 할당
        List<ReservationResponseDto.CustomerReservationItem> items =
                new ArrayList<>(reservations.size());

        // for-loop로 변환 (Stream API 대신)
        for (Reservation reservation : reservations) {
            items.add(toCustomerReservationItem(reservation));
        }

        return items;
    }

    /**
     * Entity List -> BusinessReservationItem List
     * @param reservations 예약 엔티티 리스트
     * @return 업체용 예약 아이템 리스트
     */
    public List<ReservationResponseDto.BusinessReservationItem> toBusinessReservationItems(
            List<Reservation> reservations) {

        if (reservations == null || reservations.isEmpty()) {
            return new ArrayList<>();
        }

        // ArrayList 사전 할당
        List<ReservationResponseDto.BusinessReservationItem> items =
                new ArrayList<>(reservations.size());

        // for-loop로 변환 (Stream API 대신)
        for (Reservation reservation : reservations) {
            items.add(toBusinessReservationItem(reservation));
        }

        return items;
    }

    // ========== Response 생성 메서드 (신규 - Command 패턴과 통일) ==========

    /**
     * 고객용 예약 목록 Response 생성
     *
     * @param reservationPage 예약 페이지
     * @return 고객용 예약 목록 Response
     */
    public ReservationResponseDto.CustomerReservationList toCustomerReservationList(
            Page<Reservation> reservationPage) {

        // DTO 변환
        List<ReservationResponseDto.CustomerReservationItem> items =
                toCustomerReservationItems(reservationPage.getContent());

        // PaginationInfo 생성
        ReservationResponseDto.PaginationInfo pagination = toPaginationInfo(reservationPage);

        // 최종 Response 조립
        return ReservationResponseDto.CustomerReservationList.of(items, pagination);
    }

    /**
     * 업체용 예약 목록 Response 생성
     *
     * @param business 업체 정보
     * @param reservationPage 예약 페이지
     * @return 업체용 예약 목록 Response
     */
    public ReservationResponseDto.BusinessReservationList toBusinessReservationList(
            timefit.business.entity.Business business,
            Page<Reservation> reservationPage) {

        // DTO 변환
        List<ReservationResponseDto.BusinessReservationItem> items =
                toBusinessReservationItems(reservationPage.getContent());

        // PaginationInfo 생성
        ReservationResponseDto.PaginationInfo pagination = toPaginationInfo(reservationPage);

        // 최종 Response 조립 (6개 파라미터)
        return ReservationResponseDto.BusinessReservationList.of(
                business.getId(),
                business.getBusinessName(),
                business.getAddress(),
                business.getContactPhone(),
                items,
                pagination
        );
    }
}