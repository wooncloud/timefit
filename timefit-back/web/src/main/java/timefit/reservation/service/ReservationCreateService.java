package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessOperatingHours;
import timefit.business.repository.BusinessOperatingHoursRepository;
import timefit.business.repository.BusinessRepository;
import timefit.common.ResponseData;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.factory.ReservationResponseFactory;
import timefit.reservation.repository.ReservationRepository;
import timefit.reservation.repository.ReservationRepositoryCustom;
import timefit.reservation.service.util.ReservationNumberUtil;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * 예약 생성 전담 서비스 (단순화)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationCreateService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final BusinessOperatingHoursRepository businessOperatingHoursRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRepositoryCustom reservationRepositoryCustom;
    private final ReservationResponseFactory reservationResponseFactory;
    private final ReservationNumberUtil numberUtil;

    /**
     * 예약 생성
     */
    @Transactional
    public ResponseData<ReservationResponseDto.ReservationDetail> createReservation(
            ReservationRequestDto.CreateReservation request, UUID customerId) {

        log.info("예약 신청 시작: customerId={}, businessId={}, date={}, time={}",
                customerId, request.getBusinessId(), request.getReservationDate(), request.getReservationTime());

        // 1. 기본 존재 검증
        User customer = this.validateCustomerExists(customerId);
        Business business = this.validateBusinessExists(request.getBusinessId());

        // 2. 예약 가능성 검증 (핵심만)
        this.validateReservationAvailability(request, business.getId());

        // 3. 예약 생성
        Reservation reservation = this.createNewReservation(request, customer, business);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 4. 예약 번호 생성 및 할당
        String reservationNumber = numberUtil.generateReservationNumber(savedReservation);
        savedReservation.updateReservationNumber(reservationNumber);

        // 5. 응답 생성
        ReservationResponseDto.ReservationDetail response =
                reservationResponseFactory.createReservationDetailResponse(savedReservation);

        log.info("예약 신청 완료: reservationId={}, reservationNumber={}",
                savedReservation.getId(), reservationNumber);

        return ResponseData.of(response);
    }

    /**
     * 고객 존재 검증
     */
    private User validateCustomerExists(UUID customerId) {
        return userRepository.findById(customerId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.NOT_RESERVATION_OWNER));
    }

    /**
     * 업체 존재 검증
     */
    private Business validateBusinessExists(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    /**
     * 예약 가능성 검증
     */
    private void validateReservationAvailability(ReservationRequestDto.CreateReservation request, UUID businessId) {
        // 1. 과거 날짜 예약 방지
        if (request.getReservationDate().isBefore(LocalDate.now())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_PAST_DATE);
        }

        // 2. 영업시간 확인
        this.validateBusinessOperatingHours(businessId, request.getReservationDate(), request.getReservationTime());

        // 3. 중복 예약 방지 (기존 repository 활용)
        this.validateNoDuplicateReservation(request, businessId);
    }

    /**
     * 영업시간 확인
     */
    private void validateBusinessOperatingHours(UUID businessId, LocalDate reservationDate, LocalTime reservationTime) {
        DayOfWeek dayOfWeek = reservationDate.getDayOfWeek();

        List<BusinessOperatingHours> operatingHours = businessOperatingHoursRepository
                .findByBusinessIdOrderByDayOfWeek(businessId);

        BusinessOperatingHours todayHours = operatingHours.stream()
                .filter(hours -> hours.getDayOfWeek().matches(dayOfWeek))
                .findFirst()
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_TIME_UNAVAILABLE));

        if (todayHours.getIsClosed()) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_TIME_UNAVAILABLE);
        }

        if (reservationTime.isBefore(todayHours.getOpenTime()) ||
                reservationTime.isAfter(todayHours.getCloseTime())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_SLOT_UNAVAILABLE);
        }
    }

    /**
     * 중복 예약 검증
     */
    private void validateNoDuplicateReservation(ReservationRequestDto.CreateReservation request, UUID businessId) {
        // 기존 ReservationRepositoryCustom의 메서드 활용
        List<Reservation> existingReservations = reservationRepositoryCustom
                .findReservationsByBusinessAndDate(businessId, request.getReservationDate());

        boolean hasConflict = existingReservations.stream()
                .anyMatch(existing ->
                        existing.getReservationTime().equals(request.getReservationTime()) &&
                                existing.getCustomer().getId().equals(request.getCustomerId()) &&
                                (existing.getStatus() != timefit.reservation.entity.ReservationStatus.CANCELLED)
                );

        if (hasConflict) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
        }
    }

    // 새 예약 생성
    private Reservation createNewReservation(ReservationRequestDto.CreateReservation request,
                                                User customer, Business business) {
        return Reservation.createReservation(
                customer, business, null,
                request.getReservationDate(), request.getReservationTime(),
                request.getDurationMinutes(), request.getTotalPrice(),
                request.getCustomerName(), request.getCustomerPhone(),
                request.getNotes()
        );
    }
}