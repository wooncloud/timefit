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
import timefit.business.service.validator.BusinessValidator;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationQueryRepository;
import timefit.reservation.service.util.ReservationConverter;
import timefit.reservation.service.validator.ReservationValidator;

import java.time.LocalDate;
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
    private final ReservationValidator reservationValidator;
    private final BusinessValidator businessValidator;
    private final ReservationConverter converter;

    // ========== 고객용 조회 ==========

    /**
     * 내 예약 목록 조회 (고객)
     */
    public ReservationResponseDto.CustomerReservationList getMyReservations(
            UUID customerId, String status, String startDate, String endDate,
            UUID businessId, int page, int size) {

        log.info("내 예약 목록 조회: customerId={}, status={}, page={}", customerId, status, page);

        // Pageable 생성, 파라미터 파싱
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ReservationStatus reservationStatus = status != null ? ReservationStatus.valueOf(status) : null;
        LocalDate startLocalDate = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate endLocalDate = endDate != null ? LocalDate.parse(endDate) : null;

        Page<Reservation> reservationPage = reservationQueryRepository.findMyReservationsWithFilters(
                customerId, reservationStatus, startLocalDate, endLocalDate, businessId, pageable);

        // Converter를 사용한 변환
        List<ReservationResponseDto.CustomerReservationItem> items = reservationPage.getContent()
                .stream()
                .map(converter::toCustomerReservationItem)
                .collect(Collectors.toList());

        ReservationResponseDto.PaginationInfo pagination = converter.toPaginationInfo(reservationPage);

        return ReservationResponseDto.CustomerReservationList.of(items, pagination);
    }

    /**
     * 예약 상세 조회 (고객)
     */
    public ReservationResponseDto.CustomerReservation getReservationDetail(
            UUID reservationId, UUID customerId) {

        log.info("예약 상세 조회: reservationId={}, customerId={}", reservationId, customerId);

        // 검증
        Reservation reservation = reservationValidator.validateExists(reservationId);
        reservationValidator.validateOwner(reservation, customerId);

        // Converter 변환
        return converter.toCustomerReservation(reservation);
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
            UUID businessId, UUID currentUserId, String status,
            LocalDate startDate, LocalDate endDate, int page, int size) {

        log.info("업체 예약 목록 조회: businessId={}, userId={}, status={}",
                businessId, currentUserId, status);

        // 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 업체 정보 조회
        Business business = businessValidator.validateBusinessExists(businessId);

        // Pageable 생성, 파라미터 파싱
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ReservationStatus reservationStatus = status != null ? ReservationStatus.valueOf(status) : null;

        Page<Reservation> reservationPage = reservationQueryRepository.findBusinessReservationsWithFilters(
                businessId, reservationStatus, null, startDate, endDate, pageable);

        // Converter를 사용한 변환
        List<ReservationResponseDto.BusinessReservationItem> items = reservationPage.getContent()
                .stream()
                .map(converter::toBusinessReservationItem)
                .collect(Collectors.toList());

        ReservationResponseDto.PaginationInfo pagination = converter.toPaginationInfo(reservationPage);

        return ReservationResponseDto.BusinessReservationList.of(
                business.getId(),
                business.getBusinessName(),
                business.getAddress(),
                business.getContactPhone(),
                items,
                pagination
        );
    }

    /**
     * 업체용 예약 상세 조회
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