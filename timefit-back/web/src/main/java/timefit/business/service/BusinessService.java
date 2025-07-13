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
import java.util.Optional;
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
     * 업체 생성
     * 권한: 로그인한 사용자 누구나 (생성자는 자동으로 OWNER가 됨)
     */
    @Transactional
    public ResponseData<BusinessResponseDto.BusinessDetail> createBusiness(
            BusinessRequestDto.CreateBusiness request, UUID ownerId ) {

        log.info("업체 생성 시작: ownerId={}, businessName={}", ownerId, request.getBusinessName());
        // 1. 사업자번호 중복 체크
        if (businessRepository.existsByBusinessNumber(request.getBusinessNumber())) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_ALREADY_EXISTS);
        }

        // 2. 소유자 사용자 존재 확인
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));
        // 3. 업체 생성
        Business business = Business.createBusiness(
                request.getBusinessName(),
                request.getBusinessType(),
                request.getBusinessNumber(),
                request.getAddress(),
                request.getContactPhone(),
                request.getDescription()
        );
        Business savedBusiness = businessRepository.save(business);
        // 4. 소유자 권한 부여
        UserBusinessRole ownerRole = UserBusinessRole.createOwner(owner, savedBusiness);
        userBusinessRoleRepository.save(ownerRole);

        // 5. 응답 생성
        BusinessResponseDto.BusinessDetail response =
                businessResponseFactory.createBusinessDetailResponse(savedBusiness, ownerRole, 1);

        log.info("업체 생성 완료: businessId={}, ownerId={}", savedBusiness.getId(), ownerId);


        return ResponseData.of(response);
    }


    /**
     * 업체 상세 정보 조회
     * 권한: OWNER, MANAGER, MEMBER (해당 업체에 속한 사용자만)
     */
    public ResponseData<BusinessResponseDto.BusinessDetail> getBusinessDetail(UUID businessId, UUID userId) {
        Business business = validateBusinessExists(businessId);
        UserBusinessRole userRole = validateUserBusinessAccess(userId, businessId);
        Integer totalMembers = getTotalMembersCount(businessId);

        BusinessResponseDto.BusinessDetail businessDetail =
                businessResponseFactory.createBusinessDetailResponse(business, userRole, totalMembers);
        log.info("업체 상세 조회 완료: businessId={}, userId={}, role={}",
                businessId, userId, userRole.getRole());
        return ResponseData.of(businessDetail);
    }


    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
    @Transactional
    public ResponseData<BusinessResponseDto.BusinessProfile> updateBusiness(
            UUID businessId, BusinessRequestDto.UpdateBusiness request, UUID currentUserId) {

        Business business = validateBusinessExists(businessId);
        UserBusinessRole userRole = validateManagerOrOwnerRole(currentUserId, businessId);

        if (request.getBusinessNumber() != null &&
                !request.getBusinessNumber().equals(business.getBusinessNumber())) {
            if (businessRepository.existsByBusinessNumber(request.getBusinessNumber())) {
                throw new BusinessException(BusinessErrorCode.BUSINESS_ALREADY_EXISTS);
            }
        }

        business.updateBusinessInfo(
                request.getBusinessName(),
                request.getBusinessType(),
                request.getAddress(),
                request.getContactPhone(),
                request.getDescription(),
                request.getLogoUrl()
        );
        Business updatedBusiness = businessRepository.save(business);
        BusinessResponseDto.BusinessProfile response =
                businessResponseFactory.createBusinessProfileResponse(updatedBusiness, userRole);

        log.info("업체 정보 수정 완료: businessId={}, userId={}, role={}",
                businessId, currentUserId, userRole.getRole());



        return ResponseData.of(response);
    }


    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    @Transactional
    public ResponseData<BusinessResponseDto.DeleteResult> deleteBusiness(
            UUID businessId, BusinessRequestDto.DeleteBusiness request, UUID currentUserId) {

        log.info("업체 삭제 시작: businessId={}, userId={}", businessId, currentUserId);

        // 업체 존재 확인
        Business business = validateBusinessExists(businessId);
        if (!business.isActiveBusiness()) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_ALREADY_DELETED);
        }

        // OWNER 권한 확인 (OWNER만 삭제 가능)
        UserBusinessRole userRole = validateOwnerRole(currentUserId, businessId);
        if (!Boolean.TRUE.equals(request.getConfirmDelete())) {
            throw new BusinessException(BusinessErrorCode.DELETE_CONFIRMATION_REQUIRED);
        }

        business.deactivate();
        Business deactivatedBusiness = businessRepository.save(business);

        BusinessResponseDto.DeleteResult response =
                businessResponseFactory.createDeleteResultResponse(deactivatedBusiness, request.getDeleteReason());
        log.info("업체 삭제 완료: businessId={}, userId={}, reason={}",
                businessId, currentUserId, request.getDeleteReason());
        return ResponseData.of(response);
    }

    /**
     * 업체 활성화/비활성화 토글
     * 권한: OWNER만 가능
     * 특징: 단순 상태 변경 (성공/실패만 반환)
     */
    @Transactional
    public ResponseData<String> toggleBusinessStatus(UUID businessId, UUID currentUserId) {

        log.info("업체 상태 토글 시작: businessId={}, userId={}", businessId, currentUserId);

        Business business = validateBusinessExists(businessId);
        UserBusinessRole userRole = validateOwnerRole(currentUserId, businessId);

        // 현재 상태 확인 및 토글
        boolean wasActive = business.isActiveBusiness();
        String resultMessage;

        if (wasActive) {
            // 활성 → 비활성
            business.deactivate();
            resultMessage = "업체가 비활성화되었습니다";
            log.info("업체 비활성화: businessId={}", businessId);
        } else {
            // 비활성 → 활성
            business.activate();
            resultMessage = "업체가 활성화되었습니다";
            log.info("업체 활성화: businessId={}", businessId);
        }

        businessRepository.save(business);
        log.info("업체 상태 토글 완료: businessId={}, userId={}, {} → {}",
                businessId, currentUserId, wasActive ? "활성" : "비활성", !wasActive ? "활성" : "비활성");
        return ResponseData.of(resultMessage);
    }

    /**
     * 업체 구성원 목록 조회
     * 권한: OWNER, MANAGER만 가능
     */
    public ResponseData<List<BusinessResponseDto.BusinessMember>> getBusinessMembers(
            UUID businessId, UUID currentUserId) {

        log.info("업체 구성원 목록 조회 시작: businessId={}, userId={}", businessId, currentUserId);

        Business business = validateBusinessExists(businessId);
        if (!business.isActiveBusiness()) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_NOT_ACTIVE);
        }

        UserBusinessRole currentUserRole = validateManagerOrOwnerRole(currentUserId, businessId);

        List<UserBusinessRole> userBusinessRoles = userBusinessRoleRepository
                .findByBusinessIdAndIsActive(businessId, true);
        if (userBusinessRoles.isEmpty()) {
            log.warn("활성화된 구성원이 없음: businessId={}", businessId);
            throw new BusinessException(BusinessErrorCode.NO_ACTIVE_MEMBERS);
        }

        List<BusinessResponseDto.BusinessMember> members =
                businessResponseFactory.createBusinessMembersResponse(userBusinessRoles);
        log.info("업체 구성원 목록 조회 완료: businessId={}, userId={}, memberCount={}",
                businessId, currentUserId, members.size());
        return ResponseData.of(members);
    }

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER만 가능
     */
    @Transactional
    public ResponseData<BusinessResponseDto.InvitationResult> inviteUser(
            UUID businessId, BusinessRequestDto.InviteUser request, UUID inviterUserId) {

        log.info("구성원 초대 시작: businessId={}, inviterUserId={}, inviteeEmail={}",
                businessId, inviterUserId, request.getEmail());

        Business business = validateBusinessExists(businessId);
        if (!business.isActiveBusiness()) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_NOT_ACTIVE);
        }

        UserBusinessRole inviterRole = validateManagerOrOwnerRole(inviterUserId, businessId);
        User inviteeUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        // 이미 업체 구성원인지 확인
        boolean alreadyActiveMember = userBusinessRoleRepository
                .existsByUserIdAndBusinessIdAndIsActive(inviteeUser.getId(), businessId, true);
        if (alreadyActiveMember) {
            throw new BusinessException(BusinessErrorCode.USER_ALREADY_MEMBER);
        }

        // 기존에 비활성화된 역할이 있는지 확인 (재활성 , 새 멤버)
        Optional<UserBusinessRole> existingRole = userBusinessRoleRepository
                .findByUserIdAndBusinessIdAndIsActive(inviteeUser.getId(), businessId, false);

        UserBusinessRole userRole;
        if (existingRole.isPresent()) {
            // 1. 기존 멤버 재활성
            userRole = existingRole.get();
            userRole.activate();
            log.info("기존 구성원 재활성화: userId={}, businessId={}", inviteeUser.getId(), businessId);
        } else {
            // 2. 새 멤버 생성
            userRole = UserBusinessRole.createWithRole(
                    inviteeUser, business, request.getRole(), inviterRole.getUser());
            log.info("새 구성원 생성: userId={}, businessId={}, role={}",
                    inviteeUser.getId(), businessId, request.getRole());
        }
        UserBusinessRole savedRole = userBusinessRoleRepository.save(userRole);

        String inviterName = inviterRole.getUser().getName();
        BusinessResponseDto.InvitationResult response = businessResponseFactory
                .createInvitationResultResponse(savedRole, inviterName,
                        request.getInvitationMessage(), "INVITED! 이것도 클라이언트에서 받아야할텐데");

        log.info("구성원 초대 완료: businessId={}, inviterUserId={}, inviteeUserId={}, role={}",
                businessId, inviterUserId, inviteeUser.getId(), request.getRole());

        return ResponseData.of(response);
    }

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */
    @Transactional
    public ResponseData<Void> changeUserRole(
            UUID businessId, UUID targetUserId, BusinessRequestDto.ChangeRole request, UUID currentUserId) {

        log.info("구성원 권한 변경 요청: businessId={}, targetUserId={}, newRole={}, requesterUserId={}",
                businessId, targetUserId, request.getNewRole(), currentUserId);

        Business business = validateBusinessExists(businessId);
        if (!business.isActiveBusiness()) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_NOT_ACTIVE);
        }

        UserBusinessRole requesterRole = validateOwnerRole(currentUserId, businessId);
        // 대상자 해당 업체의 활성 구성원인지 확인
        UserBusinessRole targetRole = userBusinessRoleRepository
                .findByUserIdAndBusinessIdAndIsActive(targetUserId, businessId, true)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_BUSINESS_MEMBER));

        // 권한 변경
        if (currentUserId.equals(targetUserId)) {
            throw new BusinessException(BusinessErrorCode.CANNOT_CHANGE_OWN_ROLE); // 본인 권한 변경 시도
        }
        if (request.getNewRole() == BusinessRole.OWNER) {
            throw new BusinessException(BusinessErrorCode.CANNOT_CHANGE_TO_OWNER); // OWNER 권한으로 변경 시도
        }
        targetRole.changeRole(request.getNewRole());
        userBusinessRoleRepository.save(targetRole);

        log.info("구성원 권한 변경 완료: businessId={}, targetUserId={}, oldRole={} → newRole={}",
                businessId, targetUserId, targetRole.getRole(), request.getNewRole());
        return ResponseData.of(null);
    }

    /**
     * 구성원 제거
     * 권한: OWNER만 가능 (본인 제거 불가)
     */


    /**
     * 업체 검색 (공개 API - 인증 불필요)
     * 권한: 모든 사용자 (로그인 불필요)
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