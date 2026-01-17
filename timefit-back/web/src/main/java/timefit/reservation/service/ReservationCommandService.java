package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.service.validator.BusinessValidator;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepository;
import timefit.reservation.service.helper.ReservationCreationHelper;
import timefit.reservation.service.helper.ReservationUpdateHelper;
import timefit.reservation.service.util.ReservationConverter;
import timefit.reservation.service.util.ReservationMessageUtil;
import timefit.reservation.service.validator.ReservationValidator;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandService {

    // Repository
    private final ReservationRepository reservationRepository;

    // Helper
    private final ReservationCreationHelper creationHelper;
    private final ReservationUpdateHelper updateHelper;

    // Validator
    private final ReservationValidator reservationValidator;
    private final BusinessValidator businessValidator;

    // Util
    private final ReservationConverter converter;
    private final ReservationMessageUtil messageUtil;

    // ========== 예약 생성 ==========

    /**
     * 예약 생성
     * Menu 패턴과 동일한 구조:
     * 1. 예약 생성 (Helper 위임)
     * 2. 저장
     * 3. DTO 변환
     */
    public ReservationResponseDto.CustomerReservation createReservation(
            ReservationRequestDto.CreateReservation request,
            UUID customerId) {

        log.info("예약 생성: customerId={}, businessId={}, menuId={}",
                customerId, request.businessId(), request.menuId());

        // 1. 예약 생성 (Helper에 완전 위임)
        Reservation reservation = creationHelper.create(request, customerId);

        // 2. 저장
        Reservation saved = reservationRepository.save(reservation);

        log.info("예약 생성 완료: reservationId={}, reservationNumber={}",
                saved.getId(), saved.getReservationNumber());

        // 3. DTO 변환
        return converter.toCustomerReservation(saved);
    }

    // ========== 예약 수정 (고객) ==========

    /**
     * 예약 수정
     *
     * 구조:
     * 1. 조회 및 검증
     * 2. 수정 (Helper 위임)
     * 3. DTO 변환
     */
    public ReservationResponseDto.CustomerReservation updateReservation(
            UUID reservationId,
            UUID customerId,
            ReservationRequestDto.UpdateReservation request) {

        log.info("예약 수정: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 조회 및 검증
        Reservation reservation = reservationValidator.validateExists(reservationId);
        reservationValidator.validateOwner(reservation, customerId);

        // 2. 수정 (Helper 위임)
        updateHelper.update(reservation, request);

        log.info("예약 수정 완료: reservationId={}", reservationId);

        // 3. DTO 변환
        return converter.toCustomerReservation(reservation);
    }

    // ========== 예약 취소 (고객) ==========

    /**
     * 예약 취소
     *
     * 구조:
     * 1. 검증
     * 2. 취소 처리
     * 3. 메시지 생성 (MessageUtil)
     * 4. DTO 변환
     */
    public ReservationResponseDto.ReservationActionResult cancelReservation(
            UUID reservationId,
            UUID customerId,
            ReservationRequestDto.CancelReservation request) {

        log.info("예약 취소: reservationId={}, customerId={}", reservationId, customerId);

        // 1. 검증
        Reservation reservation = reservationValidator.validateForCancel(reservationId, customerId);

        // 2. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 3. 취소 처리
        reservation.cancel();

        log.info("예약 취소 완료: reservationId={}", reservationId);

        // 4. 메시지 생성 (Util 위임)
        String message = messageUtil.buildCancelMessage(request.reason());

        // 5. DTO 변환
        return converter.toCancelActionResult(reservation, previousStatus, message);
    }

    // ========== 예약 승인/거절 (업체) ==========

    /**
     * 예약 승인
     *
     * 구조:
     * 1. 권한 검증
     * 2. 예약 검증
     * 3. 승인 처리
     * 4. 메시지 생성 (MessageUtil)
     * 5. DTO 변환
     */
    public ReservationResponseDto.ReservationActionResult approveReservation(
            UUID businessId,
            UUID reservationId,
            UUID currentUserId) {

        log.info("예약 승인: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 검증
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);
        reservationValidator.validateStatusForApproval(reservation);

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 승인 처리
        reservation.confirm();

        log.info("예약 승인 완료: reservationId={}", reservationId);

        // 5. 메시지 생성 (Util 위임)
        String message = messageUtil.buildApproveMessage();

        // 6. DTO 변환
        return converter.toActionResult(reservation, previousStatus, message);
    }

    /**
     * 예약 거절
     *
     * 구조:
     * 1. 권한 검증
     * 2. 예약 검증
     * 3. 거절 처리
     * 4. 메시지 생성 (MessageUtil)
     * 5. DTO 변환
     */
    public ReservationResponseDto.ReservationActionResult rejectReservation(
            UUID businessId,
            UUID reservationId,
            UUID currentUserId,
            String reason) {

        log.info("예약 거절: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 검증
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);
        reservationValidator.validateStatusForApproval(reservation);

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 거절 처리 (취소와 동일)
        reservation.cancel();

        log.info("예약 거절 완료: reservationId={}", reservationId);

        // 5. 메시지 생성 (Util 위임)
        String message = messageUtil.buildRejectMessage(reason);

        // 6. DTO 변환
        return converter.toActionResult(reservation, previousStatus, message);
    }

    // ========== 예약 완료/노쇼 (업체) ==========

    /**
     * 예약 완료 처리
     *
     * 구조:
     * 1. 권한 검증
     * 2. 예약 검증
     * 3. 완료 처리
     * 4. 메시지 생성 (MessageUtil)
     * 5. DTO 변환
     */
    public ReservationResponseDto.ReservationActionResult completeReservation(
            UUID businessId,
            UUID reservationId,
            UUID currentUserId,
            String notes) {

        log.info("예약 완료 처리: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 검증
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 완료 처리
        reservation.complete();

        log.info("예약 완료 처리 완료: reservationId={}", reservationId);

        // 5. 메시지 생성 (Util 위임)
        String message = messageUtil.buildCompleteMessage(notes);

        // 6. DTO 변환
        return converter.toActionResult(reservation, previousStatus, message);
    }

    /**
     * 노쇼 처리
     *
     * 구조:
     * 1. 권한 검증
     * 2. 예약 검증
     * 3. 노쇼 처리
     * 4. 메시지 생성 (MessageUtil)
     * 5. DTO 변환
     */
    public ReservationResponseDto.ReservationActionResult markAsNoShow(
            UUID businessId,
            UUID reservationId,
            UUID currentUserId,
            String notes) {

        log.info("노쇼 처리: businessId={}, reservationId={}, userId={}",
                businessId, reservationId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 예약 검증
        Reservation reservation = reservationValidator.validateOfBusiness(reservationId, businessId);

        // 3. 이전 상태 저장
        ReservationStatus previousStatus = reservation.getStatus();

        // 4. 노쇼 처리
        reservation.markAsNoShow();

        log.info("노쇼 처리 완료: reservationId={}", reservationId);

        // 5. 메시지 생성 (Util 위임)
        String message = messageUtil.buildNoShowMessage(notes);

        // 6. DTO 변환
        return converter.toActionResult(reservation, previousStatus, message);
    }
}