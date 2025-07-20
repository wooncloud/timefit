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
import timefit.exception.schedule.ScheduleErrorCode;
import timefit.exception.schedule.ScheduleException;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationTimeSlot;
import timefit.reservation.factory.ReservationResponseFactory;
import timefit.reservation.repository.ReservationRepository;
import timefit.reservation.repository.ReservationTimeSlotRepository;
import timefit.reservation.service.util.ReservationNumberUtil;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.UUID;

/**
 * 예약 생성 전담 서비스 (슬롯 기반)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationCreateService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ReservationTimeSlotRepository reservationTimeSlotRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationResponseFactory reservationResponseFactory;
    private final ReservationNumberUtil numberUtil;

    /**
     * 예약 생성 (슬롯 기반)
     */
    @Transactional
    public ResponseData<ReservationResponseDto.ReservationDetail> createReservation(
            ReservationRequestDto.CreateReservation request, UUID customerId) {

        log.info("예약 신청 시작: customerId={}, businessId={}, slotId={}",
                customerId, request.getBusinessId(), request.getAvailableSlotId());

        // 1. 기본 존재 검증
        User customer = validateCustomerExists(customerId);
        Business business = validateBusinessExists(request.getBusinessId());
        ReservationTimeSlot slot = validateAvailableSlotExists(request.getAvailableSlotId());

        // 2. 슬롯과 업체 매칭 검증
        validateSlotBelongsToBusiness(slot, business.getId());

        // 3. 예약 가능성 검증
        validateReservationAvailability(slot);

        // 4. 예약 생성
        Reservation reservation = createNewReservation(request, customer, business, slot);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 5. 예약 번호 생성 및 할당
        String reservationNumber = numberUtil.generateReservationNumber(savedReservation);
        savedReservation.updateReservationNumber(reservationNumber);

        // 6. 슬롯 상태 업데이트 (용량 확인)
        updateSlotAvailabilityIfNeeded(slot);

        // 7. 응답 생성
        ReservationResponseDto.ReservationDetail response =
                reservationResponseFactory.createReservationDetailResponse(savedReservation);

        log.info("예약 신청 완료: reservationId={}, reservationNumber={}",
                savedReservation.getId(), reservationNumber);

        return ResponseData.of(response);
    }

    // ===== Private =====

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
     * 예약 슬롯 존재 검증
     */
    private ReservationTimeSlot validateAvailableSlotExists(UUID slotId) {
        return reservationTimeSlotRepository.findById(slotId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.AVAILABLE_SLOT_NOT_FOUND));
    }

    /**
     * 슬롯이 해당 업체에 속하는지 검증
     */
    private void validateSlotBelongsToBusiness(ReservationTimeSlot slot, UUID businessId) {
        if (!slot.getBusiness().getId().equals(businessId)) {
            throw new ScheduleException(ScheduleErrorCode.AVAILABLE_SLOT_NOT_FOUND);
        }
    }

    /**
     * 예약 가능성 검증 (슬롯 기반)
     */
    private void validateReservationAvailability(ReservationTimeSlot slot) {
        // 1. 슬롯이 예약 가능한 상태인지 확인
        if (!slot.getIsAvailable()) {
            throw new ScheduleException(ScheduleErrorCode.AVAILABLE_SLOT_NOT_AVAILABLE);
        }

        // 2. 과거 날짜 슬롯인지 확인
        if (slot.getSlotDate().isBefore(java.time.LocalDate.now())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_PAST_DATE);
        }

        // 3. 슬롯 용량 확인
        Integer currentBookings = reservationTimeSlotRepository.countActiveReservationsBySlot(slot.getId());
        if (!slot.canAcceptReservation(currentBookings)) {
            throw new ScheduleException(ScheduleErrorCode.AVAILABLE_SLOT_CAPACITY_EXCEEDED);
        }

        // 4. 슬롯 시간 유효성 확인
        if (!slot.hasValidTime()) {
            throw new ScheduleException(ScheduleErrorCode.AVAILABLE_SLOT_TIME_FORMAT_INVALID);
        }
    }

    /**
     * 새 예약 생성
     */
    private Reservation createNewReservation(ReservationRequestDto.CreateReservation request,
                                                User customer, Business business, ReservationTimeSlot slot) {
        return Reservation.createReservation(
                customer,
                business,
                slot,
                slot.getSlotDate(),
                slot.getStartTime(),
                request.getDurationMinutes(),
                request.getTotalPrice(),
                request.getCustomerName(),
                request.getCustomerPhone(),
                request.getNotes()
        );
    }

    /**
     * 슬롯 용량이 다 찬 경우 비활성화
     */
    private void updateSlotAvailabilityIfNeeded(ReservationTimeSlot slot) {
        Integer currentBookings = reservationTimeSlotRepository.countActiveReservationsBySlot(slot.getId());

        // 용량이 다 찬 경우 슬롯을 비활성화
        if (currentBookings >= slot.getCapacity()) {
            slot.markAsFull();
            reservationTimeSlotRepository.save(slot);
            log.info("슬롯 용량 충족으로 비활성화: slotId={}, capacity={}, currentBookings={}",
                    slot.getId(), slot.getCapacity(), currentBookings);
        }
    }
}