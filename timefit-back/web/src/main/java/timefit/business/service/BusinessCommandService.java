package timefit.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.service.validator.AuthValidator;
import timefit.business.dto.BusinessRequestDto;
import timefit.business.dto.BusinessResponseDto;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.common.entity.BusinessRole;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.invitation.dto.InvitationResponseDto;
import timefit.invitation.service.InvitationService;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessCommandService {

    private final BusinessRepository businessRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final InvitationService invitationService;
    private final BusinessValidator businessValidator;
    private final AuthValidator authValidator;

    /**
     * 업체 생성
     * 권한: 로그인한 사용자 누구나 (생성자는 자동으로 OWNER가 됨)
     */
    public BusinessResponseDto.BusinessResponse createBusiness(
            BusinessRequestDto.CreateBusinessRequest request,
            UUID ownerId) {

        log.info("업체 생성 시작: ownerId={}, businessName={}", ownerId, request.businessName());

        // 1. 사용자 존재 확인
        User owner = authValidator.validateUserExists(ownerId);

        // 2. 사업자번호 중복 검증
        businessValidator.validateBusinessNumberUnique(request.businessNumber());

        // 3. Business 생성
        Business business = Business.createBusiness(
                request.businessName(),
                request.businessTypes(),
                request.businessNumber(),
                request.ownerName(),
                request.address(),
                request.contactPhone(),
                request.description(),
                request.logoUrl(),
                request.businessNotice()
        );

        Business savedBusiness = businessRepository.save(business);
        log.info("업체 생성 완료: businessId={}", savedBusiness.getId());

        // 4. 생성자를 OWNER로 등록
        UserBusinessRole ownerRole = UserBusinessRole.createOwner(
                owner, savedBusiness, owner
        );

        UserBusinessRole savedOwnerRole = userBusinessRoleRepository.save(ownerRole);
        log.info("OWNER 권한 부여 완료: userId={}, businessId={}, role=OWNER",
                ownerId, savedBusiness.getId());

        return BusinessResponseDto.BusinessResponse.of(savedBusiness, savedOwnerRole);
    }

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
    public BusinessResponseDto.BusinessResponse updateBusiness(
            UUID businessId,
            BusinessRequestDto.UpdateBusinessRequest request,
            UUID currentUserId) {

        log.info("업체 정보 수정 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. 업체 존재 확인
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 권한 확인 (OWNER 또는 MANAGER)
        UserBusinessRole userRole = businessValidator.getManagerOrOwnerRole(currentUserId, businessId);
        log.info("권한 확인 완료: userId={}, role={}", currentUserId, userRole.getRole());

        // 3. 사업자번호 변경 시 중복 검증
        if (request.businessNumber() != null &&
                !request.businessNumber().equals(business.getBusinessNumber())) {

            log.info("사업자번호 변경 감지: {} -> {}",
                    business.getBusinessNumber(), request.businessNumber());

            businessValidator.validateBusinessNumberUnique(request.businessNumber());
        }

        // 4. 업체 정보 업데이트
        business.updateBusinessInfo(
                request.businessName(),
                request.businessTypes(),
                request.ownerName(),
                request.address(),
                request.contactPhone(),
                request.description(),
                request.logoUrl(),
                request.businessNotice()
        );

        log.info("업체 정보 수정 완료: businessId={}", businessId);

        return BusinessResponseDto.BusinessResponse.of(business, userRole);
    }

    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    public BusinessResponseDto.DeleteBusinessResponse deleteBusiness(
            UUID businessId,
            BusinessRequestDto.DeleteBusinessRequest request,
            UUID currentUserId) {

        log.info("업체 삭제 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. 업체 존재 확인
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 권한 확인 (OWNER만 가능)
        businessValidator.validateOwnerRole(currentUserId, businessId);

        // 3. 업체 비활성화
        business.deactivate();

        log.info("업체 삭제 완료: businessId={}, deleteReason={}", businessId, request.deleteReason());

        return BusinessResponseDto.DeleteBusinessResponse.of(business, request.deleteReason());
    }

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER
     * 신규 초대는 무조건 MEMBER로 생성
     */
    public BusinessResponseDto.MemberListResponse.MemberResponse inviteMember(
            UUID businessId,
            BusinessRequestDto.InviteMemberRequest request,
            UUID inviterUserId) {

        log.info("구성원 초대 시작: businessId={}, inviterUserId={}, email={}",
                businessId, inviterUserId, request.email());

        InvitationResponseDto.Invitation invitation = invitationService.sendInvitation(
                businessId,
                request.email(),
                BusinessRole.MEMBER,
                inviterUserId
        );

        log.info("초대 이메일 발송 완료: invitationId={}, email={}",
                invitation.invitationId(), request.email());

        return BusinessResponseDto.MemberListResponse.MemberResponse.fromInvitation(invitation);
    }

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */
    public void changeMemberRole(
            UUID businessId,
            UUID targetUserId,
            BusinessRequestDto.ChangeMemberRoleRequest request,
            UUID currentUserId) {

        log.info("구성원 권한 변경 시작: businessId={}, targetUserId={}, newRole={}",
                businessId, targetUserId, request.newRole());

        // 1. 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 2. 요청자 권한 확인 (OWNER만 가능)
        businessValidator.validateOwnerRole(currentUserId, businessId);

        // 3. 대상 사용자 권한 조회
        UserBusinessRole targetRole = businessValidator.validateUserBusinessRole(targetUserId, businessId);

        // 4. OWNER 권한 변경 방지
        if (targetRole.getRole() == BusinessRole.OWNER) {
            log.warn("권한 변경 실패 - OWNER 권한 변경 불가: targetUserId={}", targetUserId);
            throw new BusinessException(BusinessErrorCode.CANNOT_CHANGE_OWN_ROLE);
        }

        // 5. OWNER로 변경 방지
        if (request.newRole() == BusinessRole.OWNER) {
            log.warn("권한 변경 실패 - OWNER 권한 부여 불가: targetUserId={}", targetUserId);
            throw new BusinessException(BusinessErrorCode.CANNOT_CHANGE_OWN_ROLE);
        }

        // 6. 권한 변경
        targetRole.changeRole(request.newRole());

        log.info("구성원 권한 변경 완료: targetUserId={}, newRole={}", targetUserId, request.newRole());
    }

    /**
     * 구성원 제거
     * 권한: OWNER는 모든 구성원 제거 가능, MANAGER는 MEMBER만 제거 가능
     */
    public void removeMember(
            UUID businessId,
            UUID targetUserId,
            UUID requesterUserId) {

        log.info("구성원 제거 시작: businessId={}, targetUserId={}, requesterId={}",
                businessId, targetUserId, requesterUserId);

        // 1. 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 2. 요청자 권한 확인
        UserBusinessRole requesterRole = businessValidator.getManagerOrOwnerRole(requesterUserId, businessId);

        // 3. 대상 사용자 권한 조회
        UserBusinessRole targetRole = businessValidator.validateUserBusinessRole(targetUserId, businessId);

        // 4. 자기 자신 제거 방지
        if (requesterUserId.equals(targetUserId)) {
            log.warn("구성원 제거 실패 - 자기 자신 제거 불가: userId={}", requesterUserId);
            throw new BusinessException(BusinessErrorCode.CANNOT_REMOVE_SELF);
        }

        // 5. OWNER 제거 방지
        if (targetRole.getRole() == BusinessRole.OWNER) {
            log.warn("구성원 제거 실패 - OWNER 제거 불가: targetUserId={}", targetUserId);
            throw new BusinessException(BusinessErrorCode.CANNOT_REMOVE_OWNER);
        }

        // 6. MANAGER가 MANAGER 제거 시도 방지
        if (requesterRole.getRole() == BusinessRole.MANAGER &&
                targetRole.getRole() == BusinessRole.MANAGER) {
            log.warn("구성원 제거 실패 - MANAGER는 MANAGER 제거 불가: requesterId={}, targetId={}",
                    requesterUserId, targetUserId);
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }

        // 7. 구성원 제거 (논리적 삭제)
        targetRole.deactivate();

        log.info("구성원 제거 완료: targetUserId={}, businessId={}", targetUserId, businessId);
    }

    /**
     * 구성원 활성화
     * 권한: OWNER, MANAGER
     */
    public void activateMember(
            UUID businessId,
            UUID targetUserId,
            UUID currentUserId) {

        log.info("구성원 활성화 시작: businessId={}, targetUserId={}, requesterId={}",
                businessId, targetUserId, currentUserId);

        // 1. 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 2. 요청자 권한 확인 (OWNER 또는 MANAGER)
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 3. 대상 사용자 권한 조회 (비활성 상태도 조회)
        UserBusinessRole targetRole = userBusinessRoleRepository
                .findByUserIdAndBusinessId(targetUserId, businessId)
                .orElseThrow(() -> {
                    log.warn("구성원 활성화 실패 - 권한 없음: userId={}, businessId={}",
                            targetUserId, businessId);
                    return new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
                });

        // 4. 활성화
        targetRole.activate();

        log.info("구성원 활성화 완료: targetUserId={}, businessId={}", targetUserId, businessId);
    }

    /**
     * 구성원 비활성화
     * 권한: OWNER, MANAGER
     */
    public void deactivateMember(
            UUID businessId,
            UUID targetUserId,
            UUID currentUserId) {

        log.info("구성원 비활성화 시작: businessId={}, targetUserId={}, requesterId={}",
                businessId, targetUserId, currentUserId);

        // 1. 업체 존재 확인
        businessValidator.validateBusinessExists(businessId);

        // 2. 요청자 권한 확인 (OWNER 또는 MANAGER)
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 3. 대상 사용자 권한 조회
        UserBusinessRole targetRole = businessValidator.validateUserBusinessRole(targetUserId, businessId);

        // 4. OWNER 비활성화 방지
        if (targetRole.getRole() == BusinessRole.OWNER) {
            log.warn("구성원 비활성화 실패 - OWNER 비활성화 불가: targetUserId={}", targetUserId);
            throw new BusinessException(BusinessErrorCode.CANNOT_REMOVE_OWNER);
        }

        // 5. 비활성화
        targetRole.deactivate();

        log.info("구성원 비활성화 완료: targetUserId={}, businessId={}", targetUserId, businessId);
    }
}