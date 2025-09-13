package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.common.ResponseData;
import timefit.common.entity.BusinessRole;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.exception.reservation.ReservationErrorCode;
import timefit.exception.reservation.ReservationException;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepository;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 예약 승인/거절 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationApprovalService {

    private final BusinessRepository businessRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 예약 상태 변경 (승인/거절)
     */
    @Transactional
    public ResponseData<ReservationResponseDto.ReservationStatusChangeResult> changeReservationStatus(
            UUID businessId, UUID reservationId, UUID currentUserId,
            ReservationRequestDto.ChangeReservationStatus request) {

        log.info("예약 상태 변경 시작: businessId={}, reservationId={}, userId={}, newStatus={}",
                businessId, reservationId, currentUserId, request.getStatus());

        // 1. 업체 존재 확인
        Business business = validateBusinessExists(businessId);

        // 2. 권한 확인 (OWNER, MANAGER만 가능)
        UserBusinessRole userRole = validateManagerOrOwnerRole(currentUserId, businessId);

        // 3. 예약 존재 및 소속 확인
        Reservation reservation = validateReservationExists(reservationId);
        validateReservationBelongsToBusiness(reservation, businessId);

        // 4. 상태 변경 가능 여부 확인
        validateStatusChangeAllowed(reservation, request.getStatus());

        // 5. 상태 변경 처리
        ReservationStatus previousStatus = reservation.getStatus();
        reservation.updateStatus(request.getStatus(), request.getReason());

        Reservation updatedReservation = reservationRepository.save(reservation);
        LocalDateTime updatedAt = LocalDateTime.now();

        // 6. 응답 생성
        ReservationResponseDto.UpdatedByInfo updatedByInfo = createUpdatedByInfo(userRole);
        ReservationResponseDto.ReservationStatusChangeResult result =
                ReservationResponseDto.ReservationStatusChangeResult.of(
                        updatedReservation.getId(),
                        previousStatus,
                        updatedReservation.getStatus(),
                        request.getReason(),
                        updatedAt,
                        updatedByInfo
                );

        log.info("예약 상태 변경 완료: reservationId={}, {} → {}",
                reservationId, previousStatus, updatedReservation.getStatus());

        return ResponseData.of(result);
    }

    // Private

    /**
     * 업체 존재 여부 확인
     */
    private Business validateBusinessExists(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    /**
     * OWNER 또는 MANAGER 권한 확인
     */
    private UserBusinessRole validateManagerOrOwnerRole(UUID userId, UUID businessId) {
        UserBusinessRole userRole = userBusinessRoleRepository
                .findByUserIdAndBusinessIdAndIsActive(userId, businessId, true)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_BUSINESS_MEMBER));

        if (userRole.getRole() != BusinessRole.OWNER && userRole.getRole() != BusinessRole.MANAGER) {
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }

        return userRole;
    }

    /**
     * 예약 존재 여부 확인
     */
    private Reservation validateReservationExists(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    /**
     * 예약이 해당 업체 소속인지 확인
     */
    private void validateReservationBelongsToBusiness(Reservation reservation, UUID businessId) {
        if (!reservation.getBusiness().getId().equals(businessId)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }
    }

    /**
     * 상태 변경 가능 여부 확인
     */
    private void validateStatusChangeAllowed(Reservation reservation, ReservationStatus newStatus) {
        ReservationStatus currentStatus = reservation.getStatus();

        // 1. 현재 상태가 PENDING 인지 확인 (승인/거절은 PENDING 상태에서만 가능)
        if (currentStatus != ReservationStatus.PENDING) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_INVALID_STATUS);
        }

        // 2. 변경하려는 상태가 유효한지 확인 (CONFIRMED 또는 CANCELLED만 가능)
        if (newStatus != ReservationStatus.CONFIRMED && newStatus != ReservationStatus.CANCELLED) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_INVALID_STATUS);
        }

        // 3. 과거 날짜 예약의 상태 변경 방지 (당일 포함 허용)
        if (reservation.getReservationDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_DEADLINE_PASSED);
        }
    }

    /**
     * 상태 변경자 정보 생성
     */
    private ReservationResponseDto.UpdatedByInfo createUpdatedByInfo(UserBusinessRole userRole) {
        User user = userRole.getUser();
        return ReservationResponseDto.UpdatedByInfo.of(
                user.getId(),
                user.getName(),
                userRole.getRole()
        );
    }
}