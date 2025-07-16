package timefit.reservation.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReservationResponseFactory {

    /**
     * 예약 상세 정보 응답 생성
     */
    public ReservationResponseDto.ReservationDetail createReservationDetailResponse(Reservation reservation) {

        // 업체 정보 생성
        ReservationResponseDto.BusinessInfo businessInfo = ReservationResponseDto.BusinessInfo.of(
                reservation.getBusiness().getId(),
                reservation.getBusiness().getBusinessName(),
                reservation.getBusiness().getAddress(),
                reservation.getBusiness().getContactPhone()
        );

        List<ReservationResponseDto.SelectedOptionInfo> selectedOptions = List.of();
        // TODO: 실제 선택된 옵션 정보를 reservation에서 조회하여 변환

        // 예약 세부 정보 생성
        ReservationResponseDto.ReservationDetails reservationDetails = ReservationResponseDto.ReservationDetails.of(
                reservation.getReservationDate(),
                reservation.getReservationTime(),
                reservation.getDurationMinutes(),
                selectedOptions,
                reservation.getTotalPrice(),
                reservation.getNotes()
        );

        // 고객 정보 생성
        ReservationResponseDto.CustomerInfo customerInfo = ReservationResponseDto.CustomerInfo.of(
                reservation.getCustomer().getId(),
                reservation.getCustomerName(),
                reservation.getCustomerPhone()
        );

        return ReservationResponseDto.ReservationDetail.of(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                businessInfo,
                reservationDetails,
                customerInfo,
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    /**
     * 예약 목록 조회 응답 생성
     */
    public ReservationResponseDto.ReservationListResult createReservationListResponse(Page<Reservation> reservationPage) {

        List<ReservationResponseDto.ReservationSummary> reservationSummaries = reservationPage.getContent().stream()
                .map(this::createReservationSummaryResponse)
                .collect(Collectors.toList());

        ReservationResponseDto.PaginationInfo pagination = ReservationResponseDto.PaginationInfo.of(
                reservationPage.getNumber(),
                reservationPage.getTotalPages(),
                reservationPage.getTotalElements(),
                reservationPage.getSize(),
                reservationPage.hasNext(),
                reservationPage.hasPrevious()
        );

        return ReservationResponseDto.ReservationListResult.of(reservationSummaries, pagination);
    }

    /**
     * 예약 요약 정보 응답 생성
     */
    private ReservationResponseDto.ReservationSummary createReservationSummaryResponse(Reservation reservation) {

        // 업체 요약 정보 생성
        ReservationResponseDto.BusinessSummaryInfo businessInfo = ReservationResponseDto.BusinessSummaryInfo.of(
                reservation.getBusiness().getId(),
                reservation.getBusiness().getBusinessName(),
                reservation.getBusiness().getLogoUrl()
        );

        // 선택된 옵션 정보 생성 (향후 확장용)
        List<ReservationResponseDto.SelectedOptionInfo> selectedOptions = List.of();

        // 예약 요약 세부 정보 생성
        ReservationResponseDto.ReservationSummaryDetails reservationDetails = ReservationResponseDto.ReservationSummaryDetails.of(
                reservation.getReservationDate(),
                reservation.getReservationTime(),
                reservation.getDurationMinutes(),
                selectedOptions,
                reservation.getTotalPrice()
        );

        return ReservationResponseDto.ReservationSummary.of(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                businessInfo,
                reservationDetails,
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}