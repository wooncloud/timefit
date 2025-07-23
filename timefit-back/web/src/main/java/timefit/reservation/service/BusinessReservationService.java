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
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.common.ResponseData;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepositoryCustom;
import timefit.reservation.service.util.ReservationValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessReservationService {

    private final BusinessRepository businessRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final ReservationRepositoryCustom reservationRepositoryCustom;
    private final ReservationValidationUtil validationUtil;
    private final ReservationApprovalService reservationApprovalService;


    /**
     * 업체의 받은 예약 신청 조회
     */
    public ResponseData<ReservationResponseDto.BusinessReservationListResult> getBusinessReservations(
            UUID businessId, UUID currentUserId, String status, LocalDate date,
            LocalDate startDate, LocalDate endDate, int page, int size) {

        log.info("업체 예약 목록 조회 시작: businessId={}, userId={}, status={}",
                businessId, currentUserId, status);

        // 1. 업체 존재 및 권한 확인
        Business business = validateBusinessExists(businessId);
        validateUserBusinessAccess(currentUserId, businessId);

        // 2. 파라미터 검증
        validationUtil.validatePagingParameters(page, size);
        ReservationStatus reservationStatus = parseStatus(status);

        // 3. 날짜 범위 설정
        LocalDate queryStartDate = determineStartDate(date, startDate);
        LocalDate queryEndDate = determineEndDate(date, endDate);

        // 4. 예약 목록 조회 (페이징)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));

        Page<Reservation> reservationPage = reservationRepositoryCustom.findBusinessReservationsWithFilters(
                businessId, reservationStatus, queryStartDate, queryEndDate, pageable);

        // 5. 응답 데이터 생성
        ReservationResponseDto.BusinessReservationListResult result = createBusinessReservationListResult(
                business, reservationPage);

        log.info("업체 예약 목록 조회 완료: businessId={}, totalElements={}, currentPage={}",
                businessId, reservationPage.getTotalElements(), page);

        return ResponseData.of(result);
    }

    /**
     * 예약 상태 변경 (승인/거절)
     */
    public ResponseData<ReservationResponseDto.ReservationStatusChangeResult> changeReservationStatus(
            UUID businessId, UUID reservationId, UUID currentUserId,
            ReservationRequestDto.ChangeReservationStatus request) {

        return reservationApprovalService.changeReservationStatus(
                businessId, reservationId, currentUserId, request);
    }

    // Private

    /**
     * 업체 예약 목록 응답 생성
     */
    private ReservationResponseDto.BusinessReservationListResult createBusinessReservationListResult(
            Business business, Page<Reservation> reservationPage) {

        // 업체 정보 생성
        ReservationResponseDto.BusinessInfo businessInfo = ReservationResponseDto.BusinessInfo.of(
                business.getId(),
                business.getBusinessName(),
                business.getAddress(),
                business.getContactPhone()
        );

        // 예약 요약 정보 생성
        List<ReservationResponseDto.BusinessReservationSummary> reservationSummaries =
                reservationPage.getContent().stream()
                        .map(this::createBusinessReservationSummary)
                        .collect(Collectors.toList());

        // 페이징 정보 생성
        ReservationResponseDto.PaginationInfo pagination = ReservationResponseDto.PaginationInfo.of(
                reservationPage.getNumber(),
                reservationPage.getTotalPages(),
                reservationPage.getTotalElements(),
                reservationPage.getSize(),
                reservationPage.hasNext(),
                reservationPage.hasPrevious()
        );

        return ReservationResponseDto.BusinessReservationListResult.of(
                businessInfo, reservationSummaries, pagination);
    }

    /**
     * 업체용 예약 요약 정보 생성
     */
    private ReservationResponseDto.BusinessReservationSummary createBusinessReservationSummary(Reservation reservation) {
        // 고객 요약 정보
        ReservationResponseDto.CustomerSummaryInfo customerInfo =
                ReservationResponseDto.CustomerSummaryInfo.of(
                        reservation.getCustomer().getId(),
                        reservation.getCustomerName(),
                        reservation.getCustomerPhone()
                );

        // 예약 세부 정보
        ReservationResponseDto.ReservationDetails reservationDetails =
                ReservationResponseDto.ReservationDetails.of(
                        reservation.getReservationDate(),
                        reservation.getReservationTime(),
                        reservation.getDurationMinutes(),
                        List.of(), // selectedOptions - 향후 확장 시 추가
                        reservation.getTotalPrice(),
                        reservation.getNotes()
                );

        // 액션 필요 여부 (PENDING 상태인 경우 true)
        boolean requiresAction = reservation.getStatus() == ReservationStatus.PENDING;

        return ReservationResponseDto.BusinessReservationSummary.of(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                customerInfo,
                reservationDetails,
                reservation.getCreatedAt(),
                requiresAction
        );
    }

    /**
     * 시작 날짜 결정
     */
    private LocalDate determineStartDate(LocalDate date, LocalDate startDate) {
        if (date != null) {
            return date; // 특정 날짜가 지정된 경우
        }
        if (startDate != null) {
            return startDate; // 시작 날짜가 지정된 경우
        }
        return LocalDate.now().minusDays(7); // 기본값: 7일 전
    }

    /**
     * 종료 날짜 결정
     */
    private LocalDate determineEndDate(LocalDate date, LocalDate endDate) {
        if (date != null) {
            return date; // 특정 날짜가 지정된 경우
        }
        if (endDate != null) {
            return endDate; // 종료 날짜가 지정된 경우
        }
        return LocalDate.now().plusDays(30); // 기본값: 30일 후
    }

    /**
     * 업체 존재 여부 확인
     */
    private Business validateBusinessExists(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    /**
     * 사용자가 해당 업체에 속하는지 확인
     */
    private UserBusinessRole validateUserBusinessAccess(UUID userId, UUID businessId) {
        return userBusinessRoleRepository.findByUserIdAndBusinessIdAndIsActive(userId, businessId, true)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_BUSINESS_MEMBER));
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
}
