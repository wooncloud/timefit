package timefit.business.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.common.entity.BusinessRole;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;

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
}
