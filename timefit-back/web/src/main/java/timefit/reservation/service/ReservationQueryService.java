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
 *
 * 담당 기능:
 * - 고객 예약 목록 조회
 * - 고객 예약 상세 조회
 * - 업체 예약 목록 조회
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
    public ReservationResponseDto.ReservationListResult getMyReservations(
            UUID customerId, String status, String startDate, String endDate,
            UUID businessId, int page, int size) {

        log.info("내 예약 목록 조회 시작: customerId={}, status={}, page={}",
                customerId, status, page);

        // 1. 페이징 검증
        validatePagingParameters(page, size);

        // 2. 필터 파라미터 파싱
        ReservationStatus reservationStatus = parseStatus(status);
        LocalDate startLocalDate = parseDate(startDate);
        LocalDate endLocalDate = parseDate(endDate);

        // 3. Pageable 생성
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));

        // 4. 예약 목록 조회
        Page<Reservation> reservationPage = reservationQueryRepository.findMyReservationsWithFilters(
                customerId, reservationStatus, startLocalDate, endLocalDate, businessId, pageable);

        // 5. DTO 변환
        List<ReservationResponseDto.ReservationSummary> reservationSummaries = reservationPage.getContent()
                .stream()
                .map(this::convertToReservationSummary)
                .collect(Collectors.toList());

        ReservationResponseDto.PaginationInfo paginationInfo = createPaginationInfo(reservationPage);

        log.info("내 예약 목록 조회 완료: customerId={}, totalElements={}",
                customerId, reservationPage.getTotalElements());

        return ReservationResponseDto.ReservationListResult.of(reservationSummaries, paginationInfo);
    }

    /**
     * 예약 상세 조회 (고객)
     */
    public ReservationResponseDto.ReservationDetailWithHistory getReservationDetail(
            UUID reservationId, UUID customerId) {

        log.info("예약 상세 조회 시작: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 예약 조회 및 소유자 확인
        Reservation reservation = reservationValidator.validateExists(reservationId);
        reservationValidator.validateOwner(reservation, customerId);

        log.info("예약 상세 조회 완료: reservationId={}", reservationId);

        // 2. DTO 변환
        return ReservationResponseDto.ReservationDetailWithHistory.from(reservation);
    }

    // ========== 업체용 조회 ==========

    /**
     * 업체 예약 목록 조회 (업체)
     */
    public ReservationResponseDto.BusinessReservationListResult getBusinessReservations(
            UUID businessId, UUID currentUserId, String status,
            LocalDate startDate, LocalDate endDate, int page, int size) {

        log.info("업체 예약 목록 조회 시작: businessId={}, userId={}, status={}",
                businessId, currentUserId, status);

        // 1. 업체 존재 확인
        Business business = getBusinessEntity(businessId);

        // 2. 페이징 검증
        validatePagingParameters(page, size);

        // 3. 필터 파라미터 파싱
        ReservationStatus reservationStatus = parseStatus(status);

        // 4. Pageable 생성
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));

        // 5. 예약 목록 조회
        Page<Reservation> reservationPage = reservationQueryRepository.findBusinessReservationsWithFilters(
                businessId, reservationStatus, startDate, endDate, pageable);

        // 6. DTO 변환
        ReservationResponseDto.BusinessInfo businessInfo = ReservationResponseDto.BusinessInfo.of(
                business.getId(),
                business.getBusinessName(),
                business.getAddress(),
                business.getContactPhone()
        );

        List<ReservationResponseDto.BusinessReservationSummary> reservationSummaries = reservationPage.getContent()
                .stream()
                .map(this::convertToBusinessReservationSummary)
                .collect(Collectors.toList());

        ReservationResponseDto.PaginationInfo paginationInfo = createPaginationInfo(reservationPage);

        log.info("업체 예약 목록 조회 완료: businessId={}, totalElements={}",
                businessId, reservationPage.getTotalElements());

        return ReservationResponseDto.BusinessReservationListResult.of(
                businessInfo, reservationSummaries, paginationInfo);
    }

    // ========== Private Helper Methods ==========

    /**
     * Business 엔티티 조회
     */
    private Business getBusinessEntity(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    /**
     * 페이징 파라미터 검증
     */
    private void validatePagingParameters(int page, int size) {
        if (page < 0) {
            throw new ReservationException(ReservationErrorCode.INVALID_PAGE_NUMBER);
        }
        if (size < 1 || size > 100) {
            throw new ReservationException(ReservationErrorCode.INVALID_PAGE_SIZE);
        }
    }

    /**
     * 상태 문자열 파싱
     */
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

    /**
     * 날짜 문자열 파싱
     */
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

    /**
     * Reservation을 ReservationSummary로 변환 (고객용)
     */
    private ReservationResponseDto.ReservationSummary convertToReservationSummary(Reservation reservation) {
        return ReservationResponseDto.ReservationSummary.of(
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
                        List.of(), // selectedOptions - 추후 구현
                        reservation.getReservationPrice()
                ),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    /**
     * Reservation을 BusinessReservationSummary로 변환 (업체용)
     */
    private ReservationResponseDto.BusinessReservationSummary convertToBusinessReservationSummary(
            Reservation reservation) {
        return ReservationResponseDto.BusinessReservationSummary.of(
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
                        List.of(), // selectedOptions - 추후 구현
                        reservation.getReservationPrice()
                ),
                reservation.getCreatedAt(),
                reservation.getStatus() == ReservationStatus.PENDING // requiresAction
        );
    }

    /**
     * PaginationInfo 생성
     */
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