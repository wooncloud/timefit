package timefit.business.dto;

import lombok.Getter;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.UserBusinessRole;
import timefit.common.entity.BusinessRole;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Business Response DTO
 * - 업체 조회/생성/수정/삭제 응답
 */
public class BusinessResponse {

    /**
     * 업체 상세 정보 (생성 응답용)
     */
    @Getter
    public static class BusinessDetail {
        private final UUID businessId;
        private final String businessName;
        private final Set<BusinessTypeCode> businessTypes;
        private final String businessNumber;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;
        private final Boolean isActive;
        private final BusinessRole myRole;
        private final Integer memberCount;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private BusinessDetail(
                UUID businessId,
                String businessName,
                Set<BusinessTypeCode> businessTypes,
                String businessNumber,
                String address,
                String contactPhone,
                String description,
                String logoUrl,
                Boolean isActive,
                BusinessRole myRole,
                Integer memberCount,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {

            this.businessId = businessId;
            this.businessName = businessName;
            this.businessTypes = businessTypes;
            this.businessNumber = businessNumber;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
            this.isActive = isActive;
            this.myRole = myRole;
            this.memberCount = memberCount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         */
        public static BusinessDetail of(Business business, UserBusinessRole userRole, int memberCount) {
            return new BusinessDetail(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getBusinessNumber(),
                    business.getAddress(),
                    business.getContactPhone(),
                    business.getDescription(),
                    business.getLogoUrl(),
                    business.getIsActive(),
                    userRole.getRole(),
                    memberCount,
                    business.getCreatedAt(),
                    business.getUpdatedAt()
            );
        }
    }

    /**
     * 업체 프로필 (수정 응답용)
     */
    @Getter
    public static class BusinessProfile {
        private final UUID businessId;
        private final String businessName;
        private final Set<BusinessTypeCode> businessTypes;
        private final String businessNumber;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;
        private final BusinessRole myRole;
        private final LocalDateTime updatedAt;

        private BusinessProfile(
                UUID businessId,
                String businessName,
                Set<BusinessTypeCode> businessTypes,
                String businessNumber,
                String address,
                String contactPhone,
                String description,
                String logoUrl,
                BusinessRole myRole,
                LocalDateTime updatedAt) {

            this.businessId = businessId;
            this.businessName = businessName;
            this.businessTypes = businessTypes;
            this.businessNumber = businessNumber;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
            this.myRole = myRole;
            this.updatedAt = updatedAt;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         */
        public static BusinessProfile of(Business business, UserBusinessRole userRole) {
            return new BusinessProfile(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getBusinessNumber(),
                    business.getAddress(),
                    business.getContactPhone(),
                    business.getDescription(),
                    business.getLogoUrl(),
                    userRole.getRole(),
                    business.getUpdatedAt()
            );
        }
    }

    /**
     * 공개 업체 상세 정보 (인증 불필요)
     */
    @Getter
    public static class PublicBusinessDetail {
        private final UUID businessId;
        private final String businessName;
        private final Set<BusinessTypeCode> businessTypes;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;

        private PublicBusinessDetail(
                UUID businessId,
                String businessName,
                Set<BusinessTypeCode> businessTypes,
                String address,
                String contactPhone,
                String description,
                String logoUrl) {

            this.businessId = businessId;
            this.businessName = businessName;
            this.businessTypes = businessTypes;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         */
        public static PublicBusinessDetail of(Business business) {
            return new PublicBusinessDetail(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getAddress(),
                    business.getContactPhone(),
                    business.getDescription(),
                    business.getLogoUrl()
            );
        }
    }

    /**
     * 내 업체 목록용 요약 정보
     */
    @Getter
    public static class BusinessSummary {
        private final UUID businessId;
        private final String businessName;
        private final Set<BusinessTypeCode> businessTypes;
        private final String address;
        private final String logoUrl;
        private final BusinessRole myRole;
        private final Boolean isActive;

        private BusinessSummary(
                UUID businessId,
                String businessName,
                Set<BusinessTypeCode> businessTypes,
                String address,
                String logoUrl,
                BusinessRole myRole,
                Boolean isActive) {

            this.businessId = businessId;
            this.businessName = businessName;
            this.businessTypes = businessTypes;
            this.address = address;
            this.logoUrl = logoUrl;
            this.myRole = myRole;
            this.isActive = isActive;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         */
        public static BusinessSummary of(Business business, BusinessRole role) {
            return new BusinessSummary(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getAddress(),
                    business.getLogoUrl(),
                    role,
                    business.getIsActive()
            );
        }
    }

    /**
     * 업체 삭제 결과
     */
    @Getter
    public static class DeleteResult {
        private final UUID businessId;
        private final String businessName;
        private final LocalDateTime deletedAt;
        private final String message;

        private DeleteResult(
                UUID businessId,
                String businessName,
                LocalDateTime deletedAt,
                String message) {

            this.businessId = businessId;
            this.businessName = businessName;
            this.deletedAt = deletedAt;
            this.message = message;
        }

        /**
         * 삭제 결과 생성 (정적 팩토리)
         */
        public static DeleteResult of(Business business) {
            return new DeleteResult(
                    business.getId(),
                    business.getBusinessName(),
                    business.getUpdatedAt(),
                    "업체가 성공적으로 비활성화되었습니다"
            );
        }
    }

    /**
     * 구성원 정보
     */
    @Getter
    public static class MemberInfo {
        private final UUID userId;
        private final String email;
        private final String name;
        private final BusinessRole role;
        private final LocalDateTime joinedAt;
        private final Boolean isActive;

        private MemberInfo(
                UUID userId,
                String email,
                String name,
                BusinessRole role,
                LocalDateTime joinedAt,
                Boolean isActive) {

            this.userId = userId;
            this.email = email;
            this.name = name;
            this.role = role;
            this.joinedAt = joinedAt;
            this.isActive = isActive;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         */
        public static MemberInfo of(UserBusinessRole userBusinessRole) {
            User user = userBusinessRole.getUser();
            return new MemberInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    userBusinessRole.getRole(),
                    userBusinessRole.getCreatedAt(),
                    userBusinessRole.getIsActive()
            );
        }
    }

    /**
     * 구성원 목록 결과
     */
    @Getter
    public static class MembersListResult {
        private final UUID businessId;
        private final String businessName;
        private final List<MemberInfo> members;
        private final Integer totalMembers;

        private MembersListResult(
                UUID businessId,
                String businessName,
                List<MemberInfo> members,
                Integer totalMembers) {

            this.businessId = businessId;
            this.businessName = businessName;
            this.members = members;
            this.totalMembers = totalMembers;
        }

        /**
         * Entity List → DTO 변환 (정적 팩토리)
         */
        public static MembersListResult of(Business business, List<UserBusinessRole> userBusinessRoles) {
            List<MemberInfo> memberInfos = userBusinessRoles.stream()
                    .map(MemberInfo::of)
                    .collect(Collectors.toList());

            return new MembersListResult(
                    business.getId(),
                    business.getBusinessName(),
                    memberInfos,
                    memberInfos.size()
            );
        }
    }

    /**
     * 초대 결과
     */
    @Getter
    public static class InvitationResult {
        private final UUID businessId;
        private final String businessName;
        private final UUID invitedUserId;
        private final String invitedUserEmail;
        private final BusinessRole assignedRole;
        private final LocalDateTime invitedAt;
        private final String message;

        private InvitationResult(
                UUID businessId,
                String businessName,
                UUID invitedUserId,
                String invitedUserEmail,
                BusinessRole assignedRole,
                LocalDateTime invitedAt,
                String message) {

            this.businessId = businessId;
            this.businessName = businessName;
            this.invitedUserId = invitedUserId;
            this.invitedUserEmail = invitedUserEmail;
            this.assignedRole = assignedRole;
            this.invitedAt = invitedAt;
            this.message = message;
        }

        /**
         * 초대 결과 생성 (정적 팩토리)
         */
        public static InvitationResult of(Business business, User invitedUser, UserBusinessRole userBusinessRole) {
            return new InvitationResult(
                    business.getId(),
                    business.getBusinessName(),
                    invitedUser.getId(),
                    invitedUser.getEmail(),
                    userBusinessRole.getRole(),
                    userBusinessRole.getCreatedAt(),
                    "구성원 초대가 완료되었습니다"
            );
        }
    }

    /**
     * 업체 검색 결과 (페이징)
     */
    @Getter
    public static class BusinessSearchResult {
        private final List<SearchedBusiness> businesses;
        private final Integer totalElements;
        private final Integer totalPages;
        private final Integer currentPage;
        private final Integer pageSize;

        private BusinessSearchResult(
                List<SearchedBusiness> businesses,
                Integer totalElements,
                Integer totalPages,
                Integer currentPage,
                Integer pageSize) {

            this.businesses = businesses;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        /**
         * Page<Business> → DTO 변환 (정적 팩토리)
         */
        public static BusinessSearchResult of(
                List<Business> businesses,
                int totalElements,
                int totalPages,
                int currentPage,
                int pageSize) {

            List<SearchedBusiness> searchedBusinesses = businesses.stream()
                    .map(SearchedBusiness::of)
                    .collect(Collectors.toList());

            return new BusinessSearchResult(
                    searchedBusinesses,
                    totalElements,
                    totalPages,
                    currentPage,
                    pageSize
            );
        }

        /**
         * 빈 검색 결과 생성
         */
        public static BusinessSearchResult empty(int currentPage, int pageSize) {
            return new BusinessSearchResult(
                    List.of(),
                    0,
                    0,
                    currentPage,
                    pageSize
            );
        }
    }

    /**
     * 검색된 업체 정보
     */
    @Getter
    public static class SearchedBusiness {
        private final UUID businessId;
        private final String businessName;
        private final Set<BusinessTypeCode> businessTypes;
        private final String address;
        private final String logoUrl;

        private SearchedBusiness(
                UUID businessId,
                String businessName,
                Set<BusinessTypeCode> businessTypes,
                String address,
                String logoUrl) {

            this.businessId = businessId;
            this.businessName = businessName;
            this.businessTypes = businessTypes;
            this.address = address;
            this.logoUrl = logoUrl;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         */
        public static SearchedBusiness of(Business business) {
            return new SearchedBusiness(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getAddress(),
                    business.getLogoUrl()
            );
        }
    }
}