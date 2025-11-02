package timefit.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.dto.BusinessRequest;
import timefit.business.dto.BusinessResponse;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.common.entity.BusinessRole;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

/**
 * Business CUD 전담 서비스
 * - 모든 생성/수정/삭제(CUD) 작업 처리
 * - @Transactional 적용
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessCommandService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final BusinessValidator businessValidator;

    /**
     * 업체 생성
     * 권한: 로그인한 사용자 누구나 (생성자는 자동으로 OWNER가 됨)
     */
    public BusinessResponse.BusinessDetail createBusiness(
            BusinessRequest.CreateBusiness request,
            UUID ownerId) {

        log.info("업체 생성 시작: ownerId={}, businessName={}", ownerId, request.getBusinessName());

        // 1. 사업자번호 중복 체크
        if (businessRepository.existsByBusinessNumber(request.getBusinessNumber())) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_ALREADY_EXISTS);
        }

        // 2. 소유자 사용자 존재 확인
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        // 3. 업체 생성 (Entity 정적 팩토리 사용)
        Business business = Business.createBusiness(
                request.getBusinessName(),
                request.getBusinessTypes(),
                request.getBusinessNumber(),
                request.getAddress(),
                request.getContactPhone(),
                request.getDescription()
        );
        Business savedBusiness = businessRepository.save(business);

        // 4. 소유자 권한 부여
        UserBusinessRole ownerRole = UserBusinessRole.createOwner(owner, savedBusiness);
        userBusinessRoleRepository.save(ownerRole);

        // 5. 응답 생성 (DTO 정적 팩토리 사용)
        BusinessResponse.BusinessDetail response = BusinessResponse.BusinessDetail.of(
                savedBusiness,
                ownerRole,
                1  // 멤버 수 (생성 시점에는 소유자만 존재)
        );

        log.info("업체 생성 완료: businessId={}, ownerId={}", savedBusiness.getId(), ownerId);

        return response;
    }

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
    public BusinessResponse.BusinessProfile updateBusiness(
            UUID businessId,
            BusinessRequest.UpdateBusiness request,
            UUID currentUserId) {

        log.info("업체 정보 수정 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. 업체 존재 확인
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 권한 확인 (Manager 또는 Owner)
        UserBusinessRole userRole = businessValidator.validateUserBusinessRole(currentUserId, businessId);
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 3. 사업자번호 변경 시 중복 체크
        if (request.getBusinessNumber() != null &&
                !request.getBusinessNumber().equals(business.getBusinessNumber())) {
            if (businessRepository.existsByBusinessNumber(request.getBusinessNumber())) {
                throw new BusinessException(BusinessErrorCode.BUSINESS_ALREADY_EXISTS);
            }
        }

        // 4. 업체 정보 수정 (Entity 비즈니스 메서드 사용)
        business.updateBusinessInfo(
                request.getBusinessName(),
                request.getBusinessTypes(),
                request.getAddress(),
                request.getContactPhone(),
                request.getDescription(),
                request.getLogoUrl(),
                request.getBusinessNotice()
        );

        // 5. 응답 생성 (DTO 정적 팩토리 사용)
        BusinessResponse.BusinessProfile response = BusinessResponse.BusinessProfile.of(business, userRole);

        log.info("업체 정보 수정 완료: businessId={}, userId={}, role={}",
                businessId, currentUserId, userRole.getRole());

        return response;
    }

    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    public BusinessResponse.DeleteResult deleteBusiness(
            UUID businessId,
            BusinessRequest.DeleteBusiness request,
            UUID currentUserId) {

        log.info("업체 삭제 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. 업체 존재 확인
        Business business = businessValidator.validateBusinessExists(businessId);

        if (!business.getIsActive()) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_ALREADY_DELETED);
        }

        // 2. OWNER 권한 확인 (OWNER만 삭제 가능)
        businessValidator.validateOwnerRole(currentUserId, businessId);

        // 3. 삭제 확인 검증
        if (!Boolean.TRUE.equals(request.getConfirmDelete())) {
            throw new BusinessException(BusinessErrorCode.DELETE_CONFIRMATION_REQUIRED);
        }

        // 4. 비활성화 (Entity 비즈니스 메서드 사용)
        business.deactivate();

        // 5. 모든 구성원 비활성화
        List<UserBusinessRole> activeMembers = userBusinessRoleRepository
                .findByBusinessIdAndIsActive(businessId, true);

        activeMembers.forEach(UserBusinessRole::deactivate);

        // 6. 응답 생성 (DTO 정적 팩토리 사용)
        BusinessResponse.DeleteResult response = BusinessResponse.DeleteResult.of(business);

        log.info("업체 삭제 완료: businessId={}, deactivatedMemberCount={}",
                businessId, activeMembers.size());

        return response;
    }

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER (MANAGER는 MANAGER/MEMBER만 초대 가능)
     */
    public BusinessResponse.InvitationResult inviteUser(
            UUID businessId,
            BusinessRequest.InviteUser request,
            UUID inviterUserId) {

        log.info("구성원 초대 시작: businessId={}, inviterUserId={}, email={}, role={}",
                businessId, inviterUserId, request.getEmail(), request.getRole());

        // 1. 업체 존재 확인
        Business business = businessValidator.validateActiveBusinessExists(businessId);

        // 2. 초대자 권한 확인
        UserBusinessRole inviterRole = businessValidator.validateUserBusinessRole(inviterUserId, businessId);
        businessValidator.validateManagerOrOwnerRole(inviterUserId, businessId);

        // 3. MANAGER는 OWNER 권한 부여 불가
        if (inviterRole.getRole() == BusinessRole.MANAGER && request.getRole() == BusinessRole.OWNER) {
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }

        // 4. 초대할 사용자 조회
        User inviteeUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        // 5. 이미 구성원인지 확인
        boolean alreadyMember = userBusinessRoleRepository
                .findByUserIdAndBusinessIdAndIsActive(inviteeUser.getId(), businessId, true)
                .isPresent();

        if (alreadyMember) {
            throw new BusinessException(BusinessErrorCode.USER_ALREADY_MEMBER);
        }

        // 6. 구성원 추가 (Entity 정적 팩토리 사용)
        UserBusinessRole newMemberRole;

        // 역할에 따라 적절한 정적 팩토리 메서드 호출
        if (request.getRole() == BusinessRole.MANAGER) {
            newMemberRole = UserBusinessRole.createManager(inviteeUser, business, inviterRole.getUser());
        } else {
            newMemberRole = UserBusinessRole.createMember(inviteeUser, business, inviterRole.getUser());
        }

        userBusinessRoleRepository.save(newMemberRole);

        // 7. 응답 생성 (DTO 정적 팩토리 사용)
        BusinessResponse.InvitationResult response = BusinessResponse.InvitationResult.of(
                business,
                inviteeUser,
                newMemberRole
        );

        log.info("구성원 초대 완료: businessId={}, inviterUserId={}, inviteeUserId={}, role={}",
                businessId, inviterUserId, inviteeUser.getId(), request.getRole());

        return response;
    }

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */
    public void changeUserRole(
            UUID businessId,
            UUID targetUserId,
            BusinessRequest.ChangeRole request,
            UUID currentUserId) {

        log.info("구성원 권한 변경 요청: businessId={}, targetUserId={}, newRole={}, requesterUserId={}",
                businessId, targetUserId, request.getNewRole(), currentUserId);

        // 1. 업체 존재 확인
        Business business = businessValidator.validateBusinessExists(businessId);

        if (!business.getIsActive()) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_NOT_ACTIVE);
        }

        // 2. OWNER 권한 확인
        businessValidator.validateOwnerRole(currentUserId, businessId);

        // 3. 대상자 해당 업체의 활성 구성원인지 확인
        UserBusinessRole targetRole = userBusinessRoleRepository
                .findByUserIdAndBusinessIdAndIsActive(targetUserId, businessId, true)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_BUSINESS_MEMBER));

        // 4. 본인 권한 변경 시도 방지
        if (currentUserId.equals(targetUserId)) {
            throw new BusinessException(BusinessErrorCode.CANNOT_CHANGE_OWN_ROLE);
        }

        // 5. OWNER 권한으로 변경 시도 방지
        if (request.getNewRole() == BusinessRole.OWNER) {
            throw new BusinessException(BusinessErrorCode.CANNOT_CHANGE_TO_OWNER);
        }

        // 6. 권한 변경 (Entity 비즈니스 메서드 사용)
        targetRole.changeRole(request.getNewRole());

        log.info("구성원 권한 변경 완료: businessId={}, targetUserId={}, oldRole={}, newRole={}",
                businessId, targetUserId, targetRole.getRole(), request.getNewRole());
    }

    /**
     * 구성원 제거
     * 권한: OWNER는 모든 구성원 제거 가능, MANAGER는 MEMBER만 제거 가능
     */
    public void removeMember(
            UUID businessId,
            UUID targetUserId,
            UUID requesterUserId) {

        log.info("구성원 제거 요청: businessId={}, targetUserId={}, requesterUserId={}",
                businessId, targetUserId, requesterUserId);

        // 1. 업체 존재 확인
        Business business = businessValidator.validateBusinessExists(businessId);

        if (!business.getIsActive()) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_NOT_ACTIVE);
        }

        // 2. 요청자 권한 확인
        UserBusinessRole requesterRole = businessValidator.validateUserBusinessRole(requesterUserId, businessId);
        businessValidator.validateManagerOrOwnerRole(requesterUserId, businessId);

        // 3. 대상자 조회
        UserBusinessRole targetRole = userBusinessRoleRepository
                .findByUserIdAndBusinessIdAndIsActive(targetUserId, businessId, true)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_BUSINESS_MEMBER));

        // 4. 본인 제거 시도 방지
        if (requesterUserId.equals(targetUserId)) {
            throw new BusinessException(BusinessErrorCode.CANNOT_REMOVE_SELF);
        }

        // 5. OWNER는 제거 불가
        if (targetRole.getRole() == BusinessRole.OWNER) {
            throw new BusinessException(BusinessErrorCode.CANNOT_REMOVE_OWNER);
        }

        // 6. MANAGER는 다른 MANAGER 제거 불가
        if (requesterRole.getRole() == BusinessRole.MANAGER &&
                targetRole.getRole() == BusinessRole.MANAGER) {
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }

        // 7. 구성원 비활성화 (Entity 비즈니스 메서드 사용)
        targetRole.deactivate();

        log.info("구성원 제거 완료: businessId={}, targetUserId={}, targetRole={}",
                businessId, targetUserId, targetRole.getRole());
    }
}