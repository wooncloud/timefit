package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.repository.BusinessRepository;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationQueryRepository;
import timefit.reservation.service.validator.ReservationValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Reservation 조회 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationQueryRepository reservationQueryRepository;
    private final BusinessRepository businessRepository;
    private final ReservationValidator reservationValidator;

    // ========== 고객용 조회 ==========

    /**
     * 내 예약 목록 조회 (고객)
     */
    public ReservationResponseDto.CustomerReservationList getMyReservations(
            UUID customerId, String status, String startDate, String endDate,
            UUID businessId, int page, int size) {

        log.info("내 예약 목록 조회 시작: customerId={}, status={}, page={}",
                customerId, status, page);

        validatePagingParameters(page, size);

        ReservationStatus reservationStatus = parseStatus(status);
        LocalDate startLocalDate = parseDate(startDate);
        LocalDate endLocalDate = parseDate(endDate);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));

        Page<Reservation> reservationPage = reservationQueryRepository.findMyReservationsWithFilters(
                customerId, reservationStatus, startLocalDate, endLocalDate, businessId, pageable);

        List<ReservationResponseDto.CustomerReservationItem> reservationItems = reservationPage.getContent()
                .stream()
                .map(this::convertToCustomerReservationItem)
                .collect(Collectors.toList());

        ReservationResponseDto.PaginationInfo paginationInfo = createPaginationInfo(reservationPage);

        log.info("내 예약 목록 조회 완료: customerId={}, totalElements={}",
                customerId, reservationPage.getTotalElements());

        return ReservationResponseDto.CustomerReservationList.of(reservationItems, paginationInfo);
    }

    /**
     * 예약 상세 조회 (고객)
     */
    public ReservationResponseDto.CustomerReservation getReservationDetail(
            UUID reservationId, UUID customerId) {

        log.info("예약 상세 조회 시작: reservationId={}, customerId={}", reservationId, customerId);

        Reservation reservation = reservationValidator.validateExists(reservationId);
        reservationValidator.validateOwner(reservation, customerId);

        log.info("예약 상세 조회 완료: reservationId={}", reservationId);

        return ReservationResponseDto.CustomerReservation.from(reservation);
    }

    // ========== 업체용 조회 ==========

    /**
     * 업체 예약 목록 조회 (업체)
     */
    public ReservationResponseDto.BusinessReservationList getBusinessReservations(
            UUID businessId, UUID currentUserId, String status,
            LocalDate startDate, LocalDate endDate, int page, int size) {

        log.info("업체 예약 목록 조회 시작: businessId={}, userId={}, status={}",
                businessId, currentUserId, status);

        Business business = getBusinessEntity(businessId);

        validatePagingParameters(page, size);

        ReservationStatus reservationStatus = parseStatus(status);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));

        Page<Reservation> reservationPage = reservationQueryRepository.findBusinessReservationsWithFilters(
                businessId, reservationStatus, startDate, endDate, pageable);

        ReservationResponseDto.BusinessInfo businessInfo = ReservationResponseDto.BusinessInfo.of(
                business.getId(),
                business.getBusinessName(),
                business.getAddress(),
                business.getContactPhone()
        );

        List<ReservationResponseDto.BusinessReservationItem> reservationItems = reservationPage.getContent()
                .stream()
                .map(this::convertToBusinessReservationItem)
                .collect(Collectors.toList());

        ReservationResponseDto.PaginationInfo paginationInfo = createPaginationInfo(reservationPage);

        log.info("업체 예약 목록 조회 완료: businessId={}, totalElements={}",
                businessId, reservationPage.getTotalElements());

        return ReservationResponseDto.BusinessReservationList.of(
                businessInfo, reservationItems, paginationInfo);
    }

    /**
     * 업체용 예약 상세 조회
     */
    public ReservationResponseDto.BusinessReservation getBusinessReservationDetail(
            UUID businessId, UUID reservationId, UUID currentUserId) {

        log.info("업체용 예약 상세 조회 시작: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        log.info("업체용 예약 상세 조회 완료: reservationId={}", reservationId);

        return ReservationResponseDto.BusinessReservation.from(reservation);
    }

    // ========== Private Helper Methods ==========

    private Business getBusinessEntity(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    private void validatePagingParameters(int page, int size) {
        if (page < 0) {
            throw new ReservationException(ReservationErrorCode.INVALID_PAGE_NUMBER);
        }
        if (size < 1 || size > 100) {
            throw new ReservationException(ReservationErrorCode.INVALID_PAGE_SIZE);
        }
    }

    private ReservationStatus parseStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return null;
        }
        try {
            return ReservationStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ReservationException(ReservationErrorCode.INVALID_STATUS);
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new ReservationException(ReservationErrorCode.INVALID_DATE_FORMAT);
        }
    }

    private ReservationResponseDto.CustomerReservationItem convertToCustomerReservationItem(
            Reservation reservation) {
        return ReservationResponseDto.CustomerReservationItem.of(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                ReservationResponseDto.BusinessSummaryInfo.of(
                        reservation.getBusiness().getId(),
                        reservation.getBusiness().getBusinessName(),
                        reservation.getBusiness().getLogoUrl()
                ),
                ReservationResponseDto.ReservationSummaryDetails.of(
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        reservation.getReservationDuration(),
                        reservation.getReservationPrice()
                ),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    private ReservationResponseDto.BusinessReservationItem convertToBusinessReservationItem(
            Reservation reservation) {
        return ReservationResponseDto.BusinessReservationItem.of(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                ReservationResponseDto.CustomerSummaryInfo.of(
                        reservation.getCustomer().getId(),
                        reservation.getCustomerName(),
                        reservation.getCustomerPhone()
                ),
                ReservationResponseDto.ReservationSummaryDetails.of(
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        reservation.getReservationDuration(),
                        reservation.getReservationPrice()
                ),
                reservation.getCreatedAt(),
                reservation.getStatus() == ReservationStatus.PENDING
        );
    }

    private ReservationResponseDto.PaginationInfo createPaginationInfo(Page<?> page) {
        return ReservationResponseDto.PaginationInfo.of(
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}