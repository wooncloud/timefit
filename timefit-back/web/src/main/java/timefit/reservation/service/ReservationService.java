package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        ReservationTimeSlot slot = reservationTimeSlotRepository.findById(slotId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.SLOT_NOT_FOUND));

        if (!slot.getIsAvailable()) {
            throw new ReservationException(ReservationErrorCode.SLOT_NOT_AVAILABLE);
        }

        // 슬롯 용량 확인
        int activeReservations = reservationRepositoryCustom.countActiveReservationsBySlot(slotId);
        if (activeReservations >= slot.getCapacity()) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
        }

        return slot;
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
}