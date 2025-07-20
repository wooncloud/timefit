package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.BusinessOperatingHours;
import timefit.business.repository.BusinessOperatingHoursRepository;
import timefit.common.ResponseData;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.factory.ReservationResponseFactory;
import timefit.reservation.repository.ReservationRepository;
import timefit.reservation.repository.ReservationRepositoryCustom;
import timefit.reservation.service.util.ReservationValidationUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * 예약 수정 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationUpdateService {

    private final ReservationRepository reservationRepository;
    private final ReservationRepositoryCustom reservationRepositoryCustom;
    private final BusinessOperatingHoursRepository businessOperatingHoursRepository;
    private final ReservationResponseFactory reservationResponseFactory;
    private final ReservationValidationUtil validationUtil;

    /**
     * 예약 수정
     */
    @Transactional
    public ResponseData<ReservationResponseDto.ReservationDetailWithHistory> updateReservation(
            UUID reservationId, UUID customerId, ReservationRequestDto.UpdateReservation request) {

        log.info("예약 수정 시작: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 예약 존재 및 소유권 확인
        Reservation reservation = validationUtil.validateReservationOwnership(reservationId, customerId);

        // 2. 수정 가능 여부 검증
        this.validateReservationModifiable(reservation);

        // 3. 수정 내용 검증
        this.validateUpdateRequest(request, reservation);

        // 4. 예약 정보 업데이트
        this.updateReservationInfo(reservation, request);

        // 5. 저장
        Reservation updatedReservation = reservationRepository.save(reservation);

        // 6. 응답 생성
        boolean canModify = validationUtil.checkCanModify(updatedReservation);
        boolean canCancel = validationUtil.checkCanCancel(updatedReservation);
        LocalDateTime cancelDeadline = validationUtil.calculateCancelDeadline(updatedReservation);

        ReservationResponseDto.ReservationDetailWithHistory response =
                reservationResponseFactory.createReservationDetailWithHistoryResponse(
                        updatedReservation, canModify, canCancel, cancelDeadline);

        log.info("예약 수정 완료: reservationId={}", reservationId);

        return ResponseData.of(response);
    }

    /**
     * 예약 수정 가능 여부 검증
     */
    private void validateReservationModifiable(Reservation reservation) {
        if (!validationUtil.checkCanModify(reservation)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_MODIFIABLE);
        }
    }

    /**
     * 수정 요청 내용 검증
     */
    private void validateUpdateRequest(ReservationRequestDto.UpdateReservation request, Reservation reservation) {
        // 수정 사유 필수 체크
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new ReservationException(ReservationErrorCode.MODIFICATION_REASON_REQUIRED);
        }

        // 날짜/시간 변경이 있는 경우만 추가 검증
        if (this.hasDateTimeChange(request, reservation)) {
            this.validateNewDateTime(request, reservation);
        }
    }

    /**
     * 날짜/시간 변경 여부 체크
     */
    private boolean hasDateTimeChange(ReservationRequestDto.UpdateReservation request, Reservation reservation) {
        boolean dateChanged = request.getReservationDate() != null &&
                !request.getReservationDate().equals(reservation.getReservationDate());
        boolean timeChanged = request.getReservationTime() != null &&
                !request.getReservationTime().equals(reservation.getReservationTime());

        return dateChanged || timeChanged;
    }

    /**
     * 새로운 날짜/시간 유효성 검증
     */
    private void validateNewDateTime(ReservationRequestDto.UpdateReservation request, Reservation reservation) {
        LocalDate newDate = request.getReservationDate() != null ?
                request.getReservationDate() : reservation.getReservationDate();
        LocalTime newTime = request.getReservationTime() != null ?
                request.getReservationTime() : reservation.getReservationTime();

        // 1. 과거 날짜 방지
        if (newDate.isBefore(LocalDate.now())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_PAST_DATE);
        }

        // 2. 영업시간 확인
        this.validateBusinessOperatingHours(reservation.getBusiness().getId(), newDate, newTime);

        // 3. 중복 예약 확인 (기존 예약 제외)
        this.validateNoConflictingReservation(reservation, newDate, newTime);
    }

    /**
     * 영업시간 확인
     */
    private void validateBusinessOperatingHours(UUID businessId, LocalDate date, LocalTime time) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        // 해당 업체의 전체 요일별 영업시간 조회 (월~일)
        List<BusinessOperatingHours> operatingHours = businessOperatingHoursRepository
                .findByBusinessIdOrderByDayOfWeek(businessId);
        // 오늘 요일의 영업시간 정보 찾기 (정보가 없으면 예외)
        BusinessOperatingHours todayHours = operatingHours.stream()
                .filter(hours -> hours.getDayOfWeek().matches(dayOfWeek))
                .findFirst()
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_BUSINESS_CLOSED));
        // 오늘이 닫은 날 인지 확인
        if (todayHours.getIsClosed()) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_BUSINESS_CLOSED);
        }
        // 예약 시간이 영업시간을 벗어나면 예외
        if (time.isBefore(todayHours.getOpenTime()) || time.isAfter(todayHours.getCloseTime())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_SLOT_UNAVAILABLE);
        }
    }

    /**
     * 충돌하는 예약 확인 (기존 예약 제외)
     */
    private void validateNoConflictingReservation(Reservation currentReservation, LocalDate newDate, LocalTime newTime) {
        List<Reservation> existingReservations = reservationRepositoryCustom
                .findReservationsByBusinessAndDate(currentReservation.getBusiness().getId(), newDate);

        boolean hasConflict = existingReservations.stream()
                .anyMatch(existing ->
                        !existing.getId().equals(currentReservation.getId()) && // 자기 자신 제외
                                existing.getReservationTime().equals(newTime) &&
                                existing.getCustomer().getId().equals(currentReservation.getCustomer().getId()) &&
                                (existing.getStatus() != timefit.reservation.entity.ReservationStatus.CANCELLED)
                );

        if (hasConflict) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
        }
    }

    /**
     * 예약 정보 업데이트
     */
    private void updateReservationInfo(Reservation reservation, ReservationRequestDto.UpdateReservation request) {
        // 날짜 변경
        if (request.getReservationDate() != null) {
            reservation.updateReservationDateTime(request.getReservationDate(), reservation.getReservationTime());
        }

        // 시간 변경
        if (request.getReservationTime() != null) {
            reservation.updateReservationDateTime(reservation.getReservationDate(), request.getReservationTime());
        }

        // 메모 변경
        if (request.getNotes() != null) {
            reservation.updateNotes(request.getNotes());
        }

        // TODO: 수정 이력 저장 (필요시)
        log.debug("예약 수정 완료: reason={}", request.getReason());
    }
}