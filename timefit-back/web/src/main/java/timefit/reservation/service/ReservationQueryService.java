package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.service.validator.BusinessValidator;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.service.helper.ReservationQueryHelper;
import timefit.reservation.service.util.ReservationConverter;
import timefit.reservation.service.util.ReservationPageableUtil;
import timefit.reservation.service.util.ReservationTimeUtil;
import timefit.reservation.service.validator.ReservationValidator;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Reservation 조회 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationValidator reservationValidator;
    private final BusinessValidator businessValidator;
    private final ReservationQueryHelper queryHelper;
    private final ReservationConverter converter;

    // ========== 고객용 조회 ==========

    /**
     * 내 예약 목록 조회 (고객)
     * 1. 파라미터 검증 (Validator)
     * 2. 파라미터 파싱 (TimeUtil)
     * 3. Pageable 생성 (PageableUtil)
     * 4. 데이터 조회 (Helper)
     * 5. Response 생성 (Converter)
     * @param customerId 고객 ID
     * @param status 예약 상태 (nullable)
     * @param startDate 시작 날짜 문자열 (nullable)
     * @param endDate 종료 날짜 문자열 (nullable)
     * @param businessId 업체 ID 필터 (nullable)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 고객용 예약 목록 Response
     */
    public ReservationResponseDto.CustomerReservationList getMyReservations(
            UUID customerId, String status, String startDate, String endDate,
            UUID businessId, int page, int size) {

        log.info("내 예약 목록 조회: customerId={}, status={}, page={}", customerId, status, page);

        // 1. 파라미터 검증
        reservationValidator.validatePageParameters(page, size);
        reservationValidator.validateStatus(status);
        reservationValidator.validateDateRange(startDate, endDate);

        // 2. 파라미터 파싱
        ReservationStatus reservationStatus = status != null
                ? ReservationStatus.valueOf(status)
                : null;

        ReservationTimeUtil.DateRange dateRange = ReservationTimeUtil
                .parseDateRange(startDate, endDate)
                .setDateRangeDefaults();

        // 3. Pageable 생성
        Pageable pageable = ReservationPageableUtil.createDefault(page, size);

        // 4. 데이터 조회
        Page<Reservation> reservationPage = queryHelper.loadMyReservations(
                customerId, reservationStatus, dateRange.start(), dateRange.end(),
                businessId, pageable);

        log.info("내 예약 목록 조회 완료: totalElements={}, page={}/{}",
                reservationPage.getTotalElements(), page, reservationPage.getTotalPages());

        // 5. Response 생성 (Converter)
        return converter.toCustomerReservationList(reservationPage);
    }

    /**
     * 예약 상세 조회 (고객)
     * @param reservationId 예약 ID
     * @param customerId 고객 ID
     * @return 예약 상세 Response
     */
    public ReservationResponseDto.CustomerReservation getReservationDetail(
            UUID reservationId, UUID customerId) {

        log.info("예약 상세 조회: reservationId={}, customerId={}", reservationId, customerId);

        // 검증
        Reservation reservation = reservationValidator.validateExists(reservationId);
        reservationValidator.validateOwner(reservation, customerId);

        // DTO 변환
        return converter.toCustomerReservation(reservation);
    }

    // ========== 업체용 조회 ==========

    /**
     * 업체 예약 목록 조회 (업체)
     * 1. 권한 검증 (BusinessValidator)
     * 2. 파라미터 검증 (Validator)
     * 3. Pageable 생성 (PageableUtil)
     * 4. 데이터 조회 (Helper)
     * 5. Response 생성 (Converter)
     *
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @param status 예약 상태 (nullable)
     * @param startDate 시작 날짜 (nullable)
     * @param endDate 종료 날짜 (nullable)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 업체용 예약 목록 Response
     */
    public ReservationResponseDto.BusinessReservationList getBusinessReservations(
            UUID businessId, UUID currentUserId, String status,
            LocalDate startDate, LocalDate endDate, int page, int size) {

        log.info("업체 예약 목록 조회: businessId={}, userId={}, status={}",
                businessId, currentUserId, status);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 파라미터 검증
        reservationValidator.validatePageParameters(page, size);
        reservationValidator.validateStatus(status);
        reservationValidator.validateDateRange(startDate, endDate);

        // 3. 파라미터 파싱
        ReservationStatus reservationStatus = status != null
                ? ReservationStatus.valueOf(status)
                : null;

        // 날짜 기본값 적용
        LocalDate finalStartDate = ReservationTimeUtil.determineStartDate(startDate);
        LocalDate finalEndDate = ReservationTimeUtil.determineEndDate(endDate);

        // 4. Pageable 생성
        Pageable pageable = ReservationPageableUtil.createDefault(page, size);

        // 5. 데이터 조회 (customerName은 null)
        Page<Reservation> reservationPage = queryHelper.loadBusinessReservations(
                businessId, reservationStatus, null, finalStartDate, finalEndDate, pageable);

        log.info("업체 예약 목록 조회 완료: totalElements={}, page={}/{}",
                reservationPage.getTotalElements(), page, reservationPage.getTotalPages());

        // 6. Response 생성 (Converter)
        return converter.toBusinessReservationList(business, reservationPage);
    }

    /**
     * 업체 예약 상세 조회 (업체)
     *
     * @param businessId 업체 ID
     * @param reservationId 예약 ID
     * @param currentUserId 현재 사용자 ID
     * @return 업체용 예약 상세 Response
     */
    public ReservationResponseDto.BusinessReservation getBusinessReservationDetail(
            UUID businessId, UUID reservationId, UUID currentUserId) {

        log.info("업체 예약 상세 조회: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 예약 검증
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        // Converter 변환
        return converter.toBusinessReservation(reservation);
    }
}