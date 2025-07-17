package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.repository.BusinessRepository;
import timefit.common.ResponseData;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.entity.ReservationTimeSlot;

import timefit.reservation.factory.ReservationResponseFactory;
import timefit.reservation.repository.ReservationRepository;
import timefit.reservation.repository.ReservationRepositoryCustom;
import timefit.reservation.repository.ReservationTimeSlotRepository;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    private final ReservationRepository reservationRepository;
    private final ReservationTimeSlotRepository reservationTimeSlotRepository;
    private final ReservationRepositoryCustom reservationRepositoryCustom;

    private final ReservationResponseFactory reservationResponseFactory;


    /**
     * 예약 신청
     */
    @Transactional
    public ResponseData<ReservationResponseDto.ReservationDetail> createReservation(
            ReservationRequestDto.CreateReservation request, UUID customerId) {

        log.info("예약 신청 시작: customerId={}, businessId={}, date={}, time={}",
                customerId, request.getBusinessId(), request.getReservationDate(), request.getReservationTime());

        // 1. 고객 및 업체 존재 확인
        User customer = validateUserExists(customerId);
        Business business = validateBusinessExists(request.getBusinessId());

        // 2. 예약 슬롯 유효성 및 가용성 확인
        ReservationTimeSlot slot = validateSlotAvailability(request.getAvailableSlotId());

        // 3. 예약 정책 검증
        validateReservationPolicies(request, business);

        // 4. 예약 생성
        Reservation reservation = Reservation.createReservation(
                customer, business, slot,
                request.getReservationDate(), request.getReservationTime(),
                request.getDurationMinutes(), request.getTotalPrice(),
                request.getCustomerName(), request.getCustomerPhone(),
                request.getNotes()
        );

        Reservation savedReservation = reservationRepository.save(reservation);

        // 5. 예약 번호 생성 및 업데이트
        String reservationNumber = generateReservationNumber(savedReservation);
        savedReservation.updateReservationNumber(reservationNumber);

        // 6. 응답 생성
        ReservationResponseDto.ReservationDetail response =
                reservationResponseFactory.createReservationDetailResponse(savedReservation);

        log.info("예약 신청 완료: reservationId={}, reservationNumber={}, customerId={}",
                savedReservation.getId(), reservationNumber, customerId);

        return ResponseData.of(response);
    }

    /**
     * 내 예약 목록 조회
     */
    public ResponseData<ReservationResponseDto.ReservationListResult> getMyReservations(
            UUID customerId, String status, String startDate, String endDate, UUID businessId, int page, int size) {

        log.info("내 예약 목록 조회 시작: customerId={}, status={}", customerId, status);

        // 1. 페이징 검증
        validatePagingParameters(page, size);

        // 2. 날짜 필터 파싱
        LocalDate startLocalDate = parseDate(startDate);
        LocalDate endLocalDate = parseDate(endDate);

        // 3. 상태 필터 파싱
        ReservationStatus statusFilter = parseStatus(status);

        // 4. 페이징 및 정렬 설정
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("reservationDate").descending()
                .and(Sort.by("reservationTime").descending()));

        // 5. 필터 조건으로 예약 조회
        Page<Reservation> reservationPage = findMyReservationsWithFilters(
                customerId, statusFilter, startLocalDate, endLocalDate, businessId, pageRequest);

        // 6. 빈 결과 처리
        if (reservationPage.isEmpty()) {
            log.info("내 예약 목록 조회 결과 없음: customerId={}", customerId);
            ReservationResponseDto.ReservationListResult emptyResult = createEmptyReservationListResult(page, size);
            return ResponseData.of(emptyResult);
        }

        // 7. 응답 생성
        ReservationResponseDto.ReservationListResult response =
                reservationResponseFactory.createReservationListResponse(reservationPage);

        log.info("내 예약 목록 조회 완료: customerId={}, totalElements={}", customerId, reservationPage.getTotalElements());

        return ResponseData.of(response);
    }


    /**
     * 예약 상세 조회
     */
    public ResponseData<ReservationResponseDto.ReservationDetailWithHistory> getReservationDetail(
            UUID reservationId, UUID customerId) {

        log.info("예약 상세 조회 시작: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 예약 존재 및 소유권 확인
        Reservation reservation = validateReservationOwnership(reservationId, customerId);

        // 2. 예약 수정/취소 가능성 체크
        boolean canModify = checkCanModify(reservation);
        boolean canCancel = checkCanCancel(reservation);
        LocalDateTime cancelDeadline = calculateCancelDeadline(reservation);

        // 3. 응답 생성
        ReservationResponseDto.ReservationDetailWithHistory response =
                reservationResponseFactory.createReservationDetailWithHistoryResponse(
                        reservation, canModify, canCancel, cancelDeadline);

        log.info("예약 상세 조회 완료: reservationId={}, status={}", reservationId, reservation.getStatus());

        return ResponseData.of(response);
    }


    //    --- util


    private User validateUserExists(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.NOT_RESERVATION_OWNER));
    }

    private Business validateBusinessExists(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    private ReservationTimeSlot validateSlotAvailability(UUID slotId) {
//        ReservationTimeSlot slot = reservationTimeSlotRepository.findById(slotId)
//                .orElseThrow(() -> new ReservationException(ReservationErrorCode.SLOT_NOT_FOUND));

//        if (!slot.getIsAvailable()) {
//            throw new ReservationException(ReservationErrorCode.SLOT_NOT_AVAILABLE);
//        }

//        // 슬롯 용량 확인
//        int activeReservations = reservationRepositoryCustom.countActiveReservationsBySlot(slotId);
//        if (activeReservations >= slot.getCapacity()) {
//            throw new ReservationException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
//        }

        return null;
    }

    private void validateReservationPolicies(ReservationRequestDto.CreateReservation request, Business business) {
        // 과거 날짜 예약 방지
        if (request.getReservationDate().isBefore(LocalDate.now())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_PAST_DATE);
        }

        // 영업시간 확인 (필요시 BusinessOperatingHours 조회)
        // 추가 비즈니스 룰 검증
    }

    private Reservation validateReservationOwnership(UUID reservationId, UUID customerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getCustomer().getId().equals(customerId)) {
            throw new ReservationException(ReservationErrorCode.NOT_RESERVATION_OWNER);
        }

        return reservation;
    }

    private void validateReservationModifiable(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING &&
                reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_MODIFIABLE);
        }

        // 데드라인 확인 (24시간 전)
        LocalDateTime deadline = reservation.getReservationDate().atTime(reservation.getReservationTime()).minusHours(24);
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new ReservationException(ReservationErrorCode.MODIFICATION_DEADLINE_PASSED);
        }
    }

    private void validateReservationCancellable(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELLED ||
                reservation.getStatus() == ReservationStatus.COMPLETED ||
                reservation.getStatus() == ReservationStatus.NO_SHOW) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_CANCELLABLE);
        }

        // 취소 데드라인 확인 (24시간 전)
        LocalDateTime deadline = reservation.getReservationDate().atTime(reservation.getReservationTime()).minusHours(24);
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new ReservationException(ReservationErrorCode.CANCELLATION_DEADLINE_PASSED);
        }
    }

    private void validatePagingParameters(int page, int size) {
        if (page < 0) {
            throw new ReservationException(ReservationErrorCode.INVALID_PAGE_NUMBER);
        }
        if (size <= 0 || size > 100) {
            throw new ReservationException(ReservationErrorCode.INVALID_PAGE_SIZE);
        }
    }

    // 예약 번호 생성 (ex : RES-20240615-001)
    private String generateReservationNumber(Reservation reservation) {
        String dateStr = reservation.getReservationDate().toString().replace("-", "");
        long sequence = reservationRepository.countByReservationDate(reservation.getReservationDate()) + 1;
        return String.format("RES-%s-%03d", dateStr, sequence);
    }

    //  ---  page util

    /**
     * 필터 조건으로 내 예약 조회
     */
    private Page<Reservation> findMyReservationsWithFilters(UUID customerId, ReservationStatus status,
                                                            LocalDate startDate, LocalDate endDate, UUID businessId,
                                                            PageRequest pageRequest) {
        // Custom Repository 메서드 호출
        return reservationRepositoryCustom.findMyReservationsWithFilters(
                customerId, status, startDate, endDate, businessId, pageRequest);
    }

    /**
     * 날짜 문자열 파싱
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new ReservationException(ReservationErrorCode.INVALID_DATE_FORMAT);
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
     * 빈 예약 목록 결과 생성
     */
    private ReservationResponseDto.ReservationListResult createEmptyReservationListResult(int page, int size) {
        ReservationResponseDto.PaginationInfo pagination = ReservationResponseDto.PaginationInfo.of(
                page, 0, 0L, size, false, false);
        return ReservationResponseDto.ReservationListResult.of(Collections.emptyList(), pagination);
    }

    /**
     * 예약 수정 가능 여부 체크
     */
    private boolean checkCanModify(Reservation reservation) {
        // 1. 상태 체크
        if (reservation.getStatus() != ReservationStatus.PENDING &&
                reservation.getStatus() != ReservationStatus.CONFIRMED) {
            return false;
        }

        // 2. 데드라인 체크 (24시간 전)
        LocalDateTime deadline = reservation.getReservationDate()
                .atTime(reservation.getReservationTime())
                .minusHours(24);

        return LocalDateTime.now().isBefore(deadline);
    }

    /**
     * 예약 취소 가능 여부 체크
     */
    private boolean checkCanCancel(Reservation reservation) {
        // 1. 상태 체크
        if (reservation.getStatus() == ReservationStatus.CANCELLED ||
                reservation.getStatus() == ReservationStatus.COMPLETED ||
                reservation.getStatus() == ReservationStatus.NO_SHOW) {
            return false;
        }

        // 2. 데드라인 체크 (24시간 전)
        LocalDateTime deadline = reservation.getReservationDate()
                .atTime(reservation.getReservationTime())
                .minusHours(24);

        return LocalDateTime.now().isBefore(deadline);
    }

    /**
     * 취소 데드라인 계산
     */
    private LocalDateTime calculateCancelDeadline(Reservation reservation) {
        return reservation.getReservationDate()
                .atTime(reservation.getReservationTime())
                .minusHours(24);
    }
}