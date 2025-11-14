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
import timefit.reservation.service.util.ReservationTimeUtil;
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
     *
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @param status 예약 상태 필터 (선택)
     * @param customerName 고객명 검색 (선택) - 대소문자 무시
     * @param startDate 시작 날짜 (선택, 기본값: 30일 전)
     * @param endDate 종료 날짜 (선택, 기본값: 오늘)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기 (1-100)
     * @return 업체 예약 목록 및 페이징 정보
     */
    public ReservationResponseDto.BusinessReservationList getBusinessReservations(
            UUID businessId,
            UUID currentUserId,
            String status,
            String customerName,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        log.info("업체 예약 목록 조회 시작: businessId={}, userId={}, status={}, customerName={}",
                businessId, currentUserId, status, customerName);

        // 1. 업체 조회
        Business business = getBusinessEntity(businessId);

        // 2. 페이징 파라미터 검증
        validatePagingParameters(page, size);

        // 3. 상태 파싱
        ReservationStatus reservationStatus = parseStatus(status);

        // 4. 날짜 범위 결정 (디폴트: 시작일 30일 전, 종료일 오늘)
        LocalDate queryStartDate = ReservationTimeUtil.determineStartDate(startDate);
        LocalDate queryEndDate = ReservationTimeUtil.determineEndDate(endDate);

        // 5. 날짜 범위 검증 (5년 상한선, 시작/종료일 순서 체크)
        reservationValidator.validateDateRange(queryStartDate, queryEndDate);

        // 6. 페이징 설정 (최신 순)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));

        // 7. 예약 목록 조회 (필터링 적용)
        Page<Reservation> reservationPage = reservationQueryRepository.findBusinessReservationsWithFilters(
                businessId,
                reservationStatus,
                customerName,
                queryStartDate,
                queryEndDate,
                pageable);

        // 8. 업체 정보 DTO 생성
        ReservationResponseDto.BusinessInfo businessInfo = ReservationResponseDto.BusinessInfo.of(
                business.getId(),
                business.getBusinessName(),
                business.getAddress(),
                business.getContactPhone()
        );

        // 9. 예약 목록 DTO 변환
        List<ReservationResponseDto.BusinessReservationItem> reservationItems = reservationPage.getContent()
                .stream()
                .map(this::convertToBusinessReservationItem)
                .collect(Collectors.toList());

        // 10. 페이징 정보 DTO 생성
        ReservationResponseDto.PaginationInfo paginationInfo = createPaginationInfo(reservationPage);

        log.info("업체 예약 목록 조회 완료: businessId={}, totalElements={}, currentPage={}, customerFilter={}",
                businessId, reservationPage.getTotalElements(), page,
                customerName != null ? "적용됨" : "없음");

        // 11. 최종 응답 생성
        return ReservationResponseDto.BusinessReservationList.of(
                businessInfo,
                reservationItems,
                paginationInfo
        );
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