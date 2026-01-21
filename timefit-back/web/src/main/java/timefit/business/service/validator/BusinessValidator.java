package timefit.business.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.dto.BusinessRequestDto;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.common.entity.BusinessRole;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.exception.validation.ValidationErrorCode;
import timefit.exception.validation.ValidationException;
import timefit.menu.repository.MenuRepository;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepository;

import java.util.List;
import java.util.UUID;

/**
 * Business 도메인 공통 검증 클래스
 * - Service 계층의 중복된 검증 로직 제거
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessValidator {

    private final BusinessRepository businessRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final ReservationRepository reservationRepository;
    private final MenuRepository menuRepository;

    /**
     * Business 존재 여부 검증 및 조회
     *
     * @param businessId 검증할 업체 ID
     * @return 조회된 Business 엔티티
     * @throws BusinessException 업체가 존재하지 않을 경우
     */
    public Business validateBusinessExists(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 업체 ID: {}", businessId);
                    return new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND);
                });
    }

    /**
     * 사업자번호 중복 검증 (TODO: validateBusinessExists 이랑 나중에 통합 고려)
     * @param businessNumber 검증할 사업자번호
     * @throws BusinessException 사업자번호가 이미 존재하는 경우
     */
    public void validateBusinessNumberUnique(String businessNumber) {
        if (businessRepository.existsByBusinessNumber(businessNumber)) {
            log.warn("사업자번호 중복: businessNumber={}", businessNumber);
            throw new BusinessException(BusinessErrorCode.BUSINESS_ALREADY_EXISTS);
        }
    }

    /**
     * Business가 활성 상태인지 검증
     *
     * @param business 검증할 Business 엔티티
     * @throws BusinessException 비활성 상태일 경우
     */
    public void validateBusinessActive(Business business) {
        if (!business.getIsActive()) {
            log.warn("비활성 상태의 업체: businessId={}", business.getId());
            throw new BusinessException(BusinessErrorCode.BUSINESS_NOT_ACTIVE);
        }
    }

    /**
     * 사용자가 특정 업체에 대한 권한이 있는지 검증
     *
     * @param userId 검증할 사용자 ID
     * @param businessId 업체 ID
     * @return 조회된 UserBusinessRole 엔티티
     * @throws BusinessException 권한이 없을 경우
     */
    public UserBusinessRole validateUserBusinessRole(UUID userId, UUID businessId) {
        return userBusinessRoleRepository.findByUserIdAndBusinessIdAndIsActive(userId, businessId, true)
                .orElseThrow(() -> {
                    log.warn("업체 권한 없음: userId={}, businessId={}", userId, businessId);
                    return new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
                });
    }

    /**
     * MANAGER 또는 OWNER 권한 조회 후 반환
     * @param userId 사용자 ID
     * @param businessId 업체 ID
     * @return UserBusinessRole 엔티티
     * @throws BusinessException 권한이 없거나 MEMBER인 경우
     */
    public UserBusinessRole getManagerOrOwnerRole(UUID userId, UUID businessId) {
        UserBusinessRole userRole = validateUserBusinessRole(userId, businessId);

        if (userRole.getRole() == BusinessRole.MEMBER) {
            log.warn("권한 부족 - MEMBER는 접근 불가: userId={}, businessId={}", userId, businessId);
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }

        return userRole;
    }

    /**
     * 사용자가 Manager 또는 Owner 권한을 가지고 있는지 검증
     *
     * @param userId 검증할 사용자 ID
     * @param businessId 업체 ID
     * @throws BusinessException Manager 또는 Owner 권한이 없을 경우
     */
    public void validateManagerOrOwnerRole(UUID userId, UUID businessId) {
        UserBusinessRole role = validateUserBusinessRole(userId, businessId);

        if (role.getRole() != BusinessRole.MANAGER && role.getRole() != BusinessRole.OWNER) {
            log.warn("관리자 권한 없음: userId={}, businessId={}, role={}",
                    userId, businessId, role.getRole());
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }
    }

    /**
     * 사용자가 Owner 권한을 가지고 있는지 검증
     *
     * @param userId 검증할 사용자 ID
     * @param businessId 업체 ID
     * @throws BusinessException Owner 권한이 없을 경우
     */
    public void validateOwnerRole(UUID userId, UUID businessId) {
        UserBusinessRole role = validateUserBusinessRole(userId, businessId);

        if (role.getRole() != BusinessRole.OWNER) {
            log.warn("소유자 권한 없음: userId={}, businessId={}, role={}",
                    userId, businessId, role.getRole());
            throw new BusinessException(BusinessErrorCode.CANNOT_CHANGE_TO_OWNER);
        }
    }

    /**
     * Business 존재 및 활성 상태 동시 검증
     *
     * @param businessId 검증할 업체 ID
     * @return 조회된 활성 상태의 Business 엔티티
     * @throws BusinessException 업체가 없거나 비활성 상태일 경우
     */
    public Business validateActiveBusinessExists(UUID businessId) {
        Business business = validateBusinessExists(businessId);
        validateBusinessActive(business);
        return business;
    }

    /**
     * 사용자의 업체 권한 및 Manager/Owner 역할 동시 검증 (가장 많이 사용)
     *
     * @param userId 검증할 사용자 ID
     * @param businessId 업체 ID
     * @return 조회된 Business 엔티티
     * @throws BusinessException 권한이 없거나 업체가 없을 경우
     */
    public Business validateBusinessAccess(UUID userId, UUID businessId) {
        Business business = validateBusinessExists(businessId);
        validateManagerOrOwnerRole(userId, businessId);
        return business;
    }

    /**
     * 업체 삭제 가능 여부 검증
     * - 활성 예약 확인
     * - 활성 메뉴 확인
     */
    public void validateCanBeDeleted(UUID businessId) {
        // 1. 활성 예약 확인
        boolean hasActiveReservations = reservationRepository
                .existsByBusinessIdAndStatusIn(
                        businessId,
                        List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
                );

        if (hasActiveReservations) {
            throw new BusinessException(
                    BusinessErrorCode.BUSINESS_HAS_ACTIVE_RESERVATIONS
            );
        }

        // 2. 활성 메뉴 확인
        boolean hasActiveMenus = menuRepository
                .existsByBusinessIdAndIsActiveTrue(businessId);

        if (hasActiveMenus) {
            throw new BusinessException(
                    BusinessErrorCode.BUSINESS_HAS_ACTIVE_MENUS
            );
        }
    }

    public BusinessRequestDto.DeleteBusinessRequest validateAndGetDeleteRequest(
            BusinessRequestDto.DeleteBusinessRequest request) {

        if (request == null) {
            log.debug("삭제 요청 본문 없음 - 기본 사유 생성");
            return new BusinessRequestDto.DeleteBusinessRequest("사용자 요청에 의한 삭제");
        }

        // request가 있을 때 deleteReason 검증
        String deleteReason = request.deleteReason();

        if (deleteReason == null || deleteReason.isBlank()) {
            log.warn("삭제 사유가 비어있음");
            throw new ValidationException(ValidationErrorCode.INVALID_INPUT) ;
        }

        if (deleteReason.length() > 500) {
            log.warn("삭제 사유가 너무 김: {} characters", deleteReason.length());
            throw new ValidationException(ValidationErrorCode.INVALID_INPUT) ;
        }

        log.debug("삭제 요청 본문 확인 - 사유: {}", deleteReason);
        return request;
    }
}
