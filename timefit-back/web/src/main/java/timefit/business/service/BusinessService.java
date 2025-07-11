package timefit.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.dto.BusinessRequestDto;
import timefit.business.dto.BusinessResponseDto;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.factory.BusinessResponseFactory;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.BusinessRepositoryCustom;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.common.ResponseData;
import timefit.common.entity.BusinessRole;
import timefit.exception.business.BusinessException;
import timefit.exception.business.BusinessErrorCode;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

/**
 * - OWNER: 모든 권한
 * - MANAGER: 업체 정보 수정, 구성원 조회/초대 가능
 * - MEMBER: 업체 정보 조회만 가능
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessRepositoryCustom businessRepositoryCustom;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final UserRepository userRepository;
    private final BusinessResponseFactory businessResponseFactory;


    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인 (모든 권한)
     */
    public ResponseData<List<BusinessResponseDto.BusinessSummary>> getMyBusinesses(UUID userId) {
        List<Business> businesses = businessRepositoryCustom.findBusinessesByUserId(userId);
        if (businesses.isEmpty()) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND);
        }

        List<UserBusinessRole> userBusinessRoles = userBusinessRoleRepository
                .findByUserIdAndIsActive(userId, true);

        log.info("=== UserBusinessRole 디버깅 ===");
        log.info("조회된 UserBusinessRole 수: {}", userBusinessRoles.size());
        userBusinessRoles.forEach(role ->
                log.info("Role - UserId: {}, BusinessId: {}, Role: {}, Active: {}",
                        role.getUser().getId(),
                        role.getBusiness().getId(),
                        role.getRole(),
                        role.getIsActive())
        );

        if (userBusinessRoles.isEmpty()) {
            log.warn("사용자에게 연결된 UserBusinessRole이 없음!");
        }

        List<BusinessResponseDto.BusinessSummary> result = businessResponseFactory.createMyBusinessesResponse(userBusinessRoles);

        return ResponseData.of(result);
    }

    /**
     * 업체 상세 정보 조회
     * 권한: OWNER, MANAGER, MEMBER (해당 업체에 속한 사용자만)
     */

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */

    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */


    /**
     * 업체 구성원 목록 조회
     * 권한: OWNER, MANAGER만 가능
     */

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER만 가능
     */

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */

    /**
     * 구성원 제거
     * 권한: OWNER만 가능 (본인 제거 불가)
     */


    /**
     * 업체 검색 (공개 API - 인증 불필요)
     * 권한: 모든 사용자 (로그인 불필요)
     */


    /**
     * 업체 활성화/비활성화 토글
     * 권한: OWNER만 가능
     * 특징: 단순 상태 변경 (성공/실패만 반환)
     */


    /**
     * 업체 통계 조회
     */

    /**
     * 추천 업체 조회
     */


//    ---

    // 사용자가 해당 업체에 속하는지 확인
    private UserBusinessRole validateUserBusinessAccess(UUID userId, UUID businessId) {
        return userBusinessRoleRepository.findByUserIdAndBusinessIdAndIsActive(userId, businessId, true)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_BUSINESS_MEMBER));
    }

    // 특정 권한 이상인지 확인 (OWNER, MANAGER)
    private UserBusinessRole validateManagerOrOwnerRole(UUID userId, UUID businessId) {
        UserBusinessRole userRole = validateUserBusinessAccess(userId, businessId);

        if (userRole.getRole() != BusinessRole.OWNER && userRole.getRole() != BusinessRole.MANAGER) {
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }

        return userRole;
    }

    // OWNER 권한인지 확인
    private UserBusinessRole validateOwnerRole(UUID userId, UUID businessId) {
        UserBusinessRole userRole = validateUserBusinessAccess(userId, businessId);

        if (userRole.getRole() != BusinessRole.OWNER) {
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }

        return userRole;
    }

    // Business 존재 여부 확인
    private Business validateBusinessExists(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    // 업체의 총 구성원 수 조회
    private Integer getTotalMembersCount(UUID businessId) {
        return userBusinessRoleRepository.findByBusinessIdAndIsActive(businessId, true).size();
    }

    // 비즈니스에 대응하는 UserBusinessRole 찾기
    private UserBusinessRole findCorrespondingRole(Business business, List<UserBusinessRole> userBusinessRoles) {
        return userBusinessRoles.stream()
                .filter(role -> role.getBusiness().getId().equals(business.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("비즈니스에 해당하는 권한을 찾을 수 없습니다: " + business.getId()));
    }
}