package timefit.business.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import timefit.business.dto.BusinessResponseDto;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class BusinessResponseFactory {

    private final UserBusinessRoleRepository userBusinessRoleRepository;

    // ===== 1. 업체 기본 정보 응답 생성 =====

    /**
     * 업체 상세 정보 응답 생성
     */
    public BusinessResponseDto.BusinessProfile createBusinessProfileResponse(
            Business business, UserBusinessRole currentUserRole) {

        return BusinessResponseDto.BusinessProfile.of(
                business.getId(),
                business.getBusinessName(),
                business.getBusinessType(),
                business.getBusinessNumber(),
                business.getAddress(),
                business.getContactPhone(),
                business.getDescription(),
                business.getLogoUrl(),
                currentUserRole.getRole().name(),
                business.getCreatedAt(),
                business.getUpdatedAt()
        );
    }

    public BusinessResponseDto.BusinessDetail createBusinessDetailResponse(
            Business business, UserBusinessRole currentUserRole, Integer totalMembers) {

        return BusinessResponseDto.BusinessDetail.of(
                business.getId(),
                business.getBusinessName(),
                business.getBusinessType(),
                business.getBusinessNumber(),
                business.getAddress(),
                business.getContactPhone(),
                business.getDescription(),
                business.getLogoUrl(),
                currentUserRole.getRole().name(),
                totalMembers,
                business.getCreatedAt(),
                business.getUpdatedAt()
        );
    }

    public BusinessResponseDto.PublicBusinessDetail createPublicBusinessDetailResponse(Business business) {
        return BusinessResponseDto.PublicBusinessDetail.of(
                business.getId(),
                business.getBusinessName(),
                business.getBusinessType(),
                business.getAddress(),
                business.getContactPhone(),
                business.getDescription(),
                business.getLogoUrl(),
                business.getCreatedAt()
        );
    }

    /**
     * 업체 요약 정보 응답 생성
     */
    public BusinessResponseDto.BusinessSummary createBusinessSummaryResponse(
            Business business, UserBusinessRole userRole, Integer totalMembers) {

        String myRole = userRole != null ? userRole.getRole().name() : null;
        LocalDateTime joinedAt = userRole != null ? userRole.getJoinedAt() : null;
        Boolean isActive = userRole != null ? userRole.getIsActive() : null;

        return BusinessResponseDto.BusinessSummary.of(
                business.getId(),
                business.getBusinessName(),
                business.getBusinessType(),
                business.getLogoUrl(),
                myRole,
                joinedAt,
                totalMembers,
                isActive
        );
    }

    /**
     * 내가 속한 업체 목록 응답 생성
     */
    public List<BusinessResponseDto.BusinessSummary> createMyBusinessesResponse(
            List<UserBusinessRole> userBusinessRoles) {

        return userBusinessRoles.stream()
                .map(userRole -> {
                    Business business = userRole.getBusiness();
                    Integer totalMembers = getTotalMembersCount(business.getId());

                    return createBusinessSummaryResponse(business, userRole, totalMembers);
                })
                .toList();
    }

    // ===== 2. 구성원 관리 응답 생성 =====

    /**
     * 구성원 정보 응답 생성
     */
    public BusinessResponseDto.BusinessMember createBusinessMemberResponse(
            UserBusinessRole userRole, String invitedByName) {

        User user = userRole.getUser();

        return BusinessResponseDto.BusinessMember.of(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfileImageUrl(),
                userRole.getRole(),
                userRole.getJoinedAt(),
                invitedByName,
                userRole.getIsActive(),
                user.getLastLoginAt()
        );
    }

    /**
     * 업체 구성원 목록 응답 생성
     */
    public List<BusinessResponseDto.BusinessMember> createBusinessMembersResponse(
            List<UserBusinessRole> userBusinessRoles) {

        return userBusinessRoles.stream()
                .map(userRole -> {
                    String invitedByName = getInvitedByName(userRole);
                    return createBusinessMemberResponse(userRole, invitedByName);
                })
                .toList();
    }

    /**
     * 초대 결과 응답 생성
     */
    public BusinessResponseDto.InvitationResult createInvitationResultResponse(
            UserBusinessRole newUserRole, String invitedByName, String invitationMessage, String status) {

        User invitedUser = newUserRole.getUser();

        return BusinessResponseDto.InvitationResult.of(
                invitedUser.getId(),
                invitedUser.getEmail(),
                invitedUser.getName(),
                newUserRole.getRole(),
                newUserRole.getJoinedAt(),
                invitedByName,
                invitationMessage,
                status
        );
    }

    // ===== 3. 검색 결과 응답 생성 =====

    /**
     * 업체 검색 결과 응답 생성
     */
    public BusinessResponseDto.BusinessSearchResult createBusinessSearchResultResponse(
            Page<Business> businessPage) {

        List<BusinessResponseDto.BusinessSearchItem> searchItems = businessPage.getContent().stream()
                .map(this::createBusinessSearchItemResponse)
                .toList();

        return BusinessResponseDto.BusinessSearchResult.of(
                searchItems,
                (int) businessPage.getTotalElements(),
                businessPage.getTotalPages(),
                businessPage.getNumber(),
                businessPage.getSize(),
                businessPage.hasNext()
        );
    }

    /**
     * 검색된 업체 정보 응답 생성
     */
    public BusinessResponseDto.BusinessSearchItem createBusinessSearchItemResponse(Business business) {
        Integer totalMembers = getTotalMembersCount(business.getId());

        return BusinessResponseDto.BusinessSearchItem.of(
                business.getId(),
                business.getBusinessName(),
                business.getBusinessType(),
                business.getAddress(),
                business.getContactPhone(),
                business.getDescription(),
                business.getLogoUrl(),
                totalMembers,
                null,  // 평점 (향후 확장용)
                null,  // 리뷰 수 (향후 확장용)
                null   // 거리 (향후 확장용)
        );
    }

    // ===== 4. 삭제 결과 응답 생성 =====

    /**
     * 삭제 결과 응답 생성
     */
    public BusinessResponseDto.DeleteResult createDeleteResultResponse(
            Business business, String deleteReason) {

        return BusinessResponseDto.DeleteResult.of(
                business.getId(),
                business.getBusinessName(),
                LocalDateTime.now(),
                deleteReason,
                "업체가 성공적으로 삭제되었습니다."
        );
    }

    // ===== 5. 내부 서비스용 응답 생성 (Jackson 불필요) =====

    /**
     * 업체 생성 결과 응답 생성 (내부 서비스용)
     */
    public BusinessResponseDto.BusinessCreationResult createBusinessCreationSuccessResponse(
            Business business, UUID ownerUserId) {

        return BusinessResponseDto.BusinessCreationResult.success(
                business.getId(),
                ownerUserId,
                business.getBusinessName(),
                business.getCreatedAt()
        );
    }

    /**
     * 업체 생성 실패 결과 응답 생성 (내부 서비스용)
     */
    public BusinessResponseDto.BusinessCreationResult createBusinessCreationFailureResponse(
            String failureReason) {

        return BusinessResponseDto.BusinessCreationResult.failure(failureReason);
    }

    /**
     * 권한 검증 허용 결과 응답 생성 (내부 서비스용)
     */
    public BusinessResponseDto.PermissionCheckResult createPermissionAllowedResponse(
            UserBusinessRole userRole) {

        return BusinessResponseDto.PermissionCheckResult.allowed(userRole.getRole());
    }

    /**
     * 권한 검증 거부 결과 응답 생성 (내부 서비스용)
     */
    public BusinessResponseDto.PermissionCheckResult createPermissionDeniedResponse(
            UserBusinessRole userRole, String requiredPermission, String denialReason) {

        return BusinessResponseDto.PermissionCheckResult.denied(
                userRole.getRole(),
                null, // TODO: requiredPermission을 BusinessRole로 변환
                denialReason
        );
    }


    /**
     * 업체의 총 구성원 수 조회
     */
    private Integer getTotalMembersCount(UUID businessId) {
        return userBusinessRoleRepository.findByBusinessIdAndIsActive(businessId, true).size();
    }

    /**
     * 초대자 이름 조회
     */
    private String getInvitedByName(UserBusinessRole userRole) {
        if (userRole.getInvitedBy() == null) {
            return "시스템"; // OWNER의 경우 초대자가 없음
        }
        return userRole.getInvitedBy().getName();
    }

    /**
     * 업체와 사용자 간 권한 관계 조회
     */
    private UserBusinessRole findUserBusinessRole(UUID businessId, UUID userId) {
        return userBusinessRoleRepository.findByUserIdAndBusinessIdAndIsActive(userId, businessId, true)
                .orElse(null);
    }
}