package timefit.business.dto;

import lombok.Getter;
import timefit.common.entity.BusinessRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BusinessResponseDto {

    /**
     * 업체 상세 정보 응답
     */
    @Getter
    public static class BusinessProfile {
        private final UUID businessId;
        private final String businessName;
        private final String businessType;
        private final String businessNumber;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;
        private final String myRole;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private BusinessProfile(UUID businessId, String businessName, String businessType, String businessNumber,
                                String address, String contactPhone, String description, String logoUrl,
                                String myRole, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessType = businessType;
            this.businessNumber = businessNumber;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
            this.myRole = myRole;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static BusinessProfile of(UUID businessId, String businessName, String businessType, String businessNumber,
                                            String address, String contactPhone, String description, String logoUrl,
                                            String myRole, LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new BusinessProfile(businessId, businessName, businessType, businessNumber, address, contactPhone,
                    description, logoUrl, myRole, createdAt, updatedAt);
        }
    }

    @Getter
    public static class BusinessDetail {
        private final UUID businessId;
        private final String businessName;
        private final String businessType;
        private final String businessNumber;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;
        private final String myRole;
        private final Integer totalMembers;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private BusinessDetail(UUID businessId, String businessName, String businessType, String businessNumber,
                                String address, String contactPhone, String description, String logoUrl,
                                String myRole, Integer totalMembers, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessType = businessType;
            this.businessNumber = businessNumber;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
            this.myRole = myRole;
            this.totalMembers = totalMembers;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static BusinessDetail of(UUID businessId, String businessName, String businessType, String businessNumber,
                                        String address, String contactPhone, String description, String logoUrl,
                                        String myRole, Integer totalMembers, LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new BusinessDetail(businessId, businessName, businessType, businessNumber, address, contactPhone,
                    description, logoUrl, myRole, totalMembers, createdAt, updatedAt);
        }
    }

    @Getter
    public static class PublicBusinessDetail {
        private final UUID businessId;
        private final String businessName;
        private final String businessType;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;
        private final LocalDateTime createdAt;

        private PublicBusinessDetail(UUID businessId, String businessName, String businessType,
                                     String address, String contactPhone, String description,
                                     String logoUrl, LocalDateTime createdAt) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessType = businessType;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
            this.createdAt = createdAt;
        }

        public static PublicBusinessDetail of(UUID businessId, String businessName, String businessType,
                                              String address, String contactPhone, String description,
                                              String logoUrl, LocalDateTime createdAt) {
            return new PublicBusinessDetail(businessId, businessName, businessType, address,
                    contactPhone, description, logoUrl, createdAt);
        }
    }

    /**
     * 업체 요약 정보 응답
     */
    @Getter
    public static class BusinessSummary {
        private final UUID businessId;
        private final String businessName;
        private final String businessType;
        private final String logoUrl;
        private final String myRole;
        private final LocalDateTime joinedAt;
        private final Integer totalMembers;
        private final Boolean isActive;

        private BusinessSummary(UUID businessId, String businessName, String businessType, String logoUrl,
                                String myRole, LocalDateTime joinedAt, Integer totalMembers, Boolean isActive) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessType = businessType;
            this.logoUrl = logoUrl;
            this.myRole = myRole;
            this.joinedAt = joinedAt;
            this.totalMembers = totalMembers;
            this.isActive = isActive;
        }

        public static BusinessSummary of(UUID businessId, String businessName, String businessType, String logoUrl,
                                            String myRole, LocalDateTime joinedAt, Integer totalMembers, Boolean isActive) {
            return new BusinessSummary(businessId, businessName, businessType, logoUrl, myRole, joinedAt, totalMembers, isActive);
        }
    }

    /**
     * 구성원 정보 응답
     */
    @Getter
    public static class BusinessMember {
        private final UUID userId;
        private final String name;
        private final String email;
        private final String phoneNumber;
        private final String profileImageUrl;
        private final BusinessRole role;
        private final LocalDateTime joinedAt;
        private final String invitedByName;
        private final Boolean isActive;
        private final LocalDateTime lastLoginAt;

        private BusinessMember(UUID userId, String name, String email, String phoneNumber, String profileImageUrl,
                                BusinessRole role, LocalDateTime joinedAt, String invitedByName,
                                Boolean isActive, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.profileImageUrl = profileImageUrl;
            this.role = role;
            this.joinedAt = joinedAt;
            this.invitedByName = invitedByName;
            this.isActive = isActive;
            this.lastLoginAt = lastLoginAt;
        }

        public static BusinessMember of(UUID userId, String name, String email, String phoneNumber, String profileImageUrl,
                                        BusinessRole role, LocalDateTime joinedAt, String invitedByName,
                                        Boolean isActive, LocalDateTime lastLoginAt) {
            return new BusinessMember(userId, name, email, phoneNumber, profileImageUrl, role, joinedAt, invitedByName, isActive, lastLoginAt);
        }
    }

    /**
     * 초대 결과 응답
     */
    @Getter
    public static class InvitationResult {
        private final UUID userId;
        private final String email;
        private final String name;
        private final BusinessRole assignedRole;
        private final LocalDateTime invitedAt;
        private final String invitedByName;
        private final String invitationMessage;
        private final String status;

        private InvitationResult(UUID userId, String email, String name, BusinessRole assignedRole,
                                    LocalDateTime invitedAt, String invitedByName, String invitationMessage, String status) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.assignedRole = assignedRole;
            this.invitedAt = invitedAt;
            this.invitedByName = invitedByName;
            this.invitationMessage = invitationMessage;
            this.status = status;
        }

        public static InvitationResult of(UUID userId, String email, String name, BusinessRole assignedRole,
                                            LocalDateTime invitedAt, String invitedByName, String invitationMessage, String status) {
            return new InvitationResult(userId, email, name, assignedRole, invitedAt, invitedByName, invitationMessage, status);
        }
    }

    /**
     * 업체 검색 결과 응답
     */
    @Getter
    public static class BusinessSearchResult {
        private final List<BusinessSearchItem> businesses;
        private final Integer totalElements;
        private final Integer totalPages;
        private final Integer currentPage;
        private final Integer pageSize;
        private final Boolean hasNext;

        private BusinessSearchResult(List<BusinessSearchItem> businesses, Integer totalElements, Integer totalPages,
                                        Integer currentPage, Integer pageSize, Boolean hasNext) {
            this.businesses = businesses;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.hasNext = hasNext;
        }

        public static BusinessSearchResult of(List<BusinessSearchItem> businesses, Integer totalElements, Integer totalPages,
                                                Integer currentPage, Integer pageSize, Boolean hasNext) {
            return new BusinessSearchResult(businesses, totalElements, totalPages, currentPage, pageSize, hasNext);
        }
    }

    /**
     * 검색된 업체 정보
     */
    @Getter
    public static class BusinessSearchItem {
        private final UUID businessId;
        private final String businessName;
        private final String businessType;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;
        private final Integer totalMembers;
        private final Double rating;
        private final Integer reviewCount;
        private final Double distance;

        private BusinessSearchItem(UUID businessId, String businessName, String businessType, String address,
                                    String contactPhone, String description, String logoUrl, Integer totalMembers,
                                    Double rating, Integer reviewCount, Double distance) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessType = businessType;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
            this.totalMembers = totalMembers;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.distance = distance;
        }

        public static BusinessSearchItem of(UUID businessId, String businessName, String businessType, String address,
                                            String contactPhone, String description, String logoUrl, Integer totalMembers,
                                            Double rating, Integer reviewCount, Double distance) {
            return new BusinessSearchItem(businessId, businessName, businessType, address, contactPhone, description,
                    logoUrl, totalMembers, rating, reviewCount, distance);
        }
    }

    /**
     * 삭제 결과 응답
     */
    @Getter
    public static class DeleteResult {
        private final UUID businessId;
        private final String businessName;
        private final LocalDateTime deletedAt;
        private final String deleteReason;
        private final String message;

        private DeleteResult(UUID businessId, String businessName, LocalDateTime deletedAt, String deleteReason, String message) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.deletedAt = deletedAt;
            this.deleteReason = deleteReason;
            this.message = message;
        }

        public static DeleteResult of(UUID businessId, String businessName, LocalDateTime deletedAt, String deleteReason, String message) {
            return new DeleteResult(businessId, businessName, deletedAt, deleteReason, message);
        }
    }

    /**
     * 업체 생성 결과 (내부 서비스 전용)
     */
    @Getter
    public static class BusinessCreationResult {
        private final UUID businessId;
        private final UUID ownerUserId;
        private final String businessName;
        private final LocalDateTime createdAt;
        private final Boolean isSuccessful;
        private final String failureReason;

        private BusinessCreationResult(UUID businessId, UUID ownerUserId, String businessName,
                                       LocalDateTime createdAt, Boolean isSuccessful, String failureReason) {
            this.businessId = businessId;
            this.ownerUserId = ownerUserId;
            this.businessName = businessName;
            this.createdAt = createdAt;
            this.isSuccessful = isSuccessful;
            this.failureReason = failureReason;
        }

        public static BusinessCreationResult success(UUID businessId, UUID ownerUserId, String businessName, LocalDateTime createdAt) {
            return new BusinessCreationResult(businessId, ownerUserId, businessName, createdAt, true, null);
        }

        public static BusinessCreationResult failure(String failureReason) {
            return new BusinessCreationResult(null, null, null, null, false, failureReason);
        }
    }

    /**
     * 권한 검증 결과 (내부 서비스 전용)
     */
    @Getter
    public static class PermissionCheckResult {
        private final Boolean hasPermission;
        private final BusinessRole currentRole;
        private final BusinessRole requiredRole;
        private final String denialReason;

        private PermissionCheckResult(Boolean hasPermission, BusinessRole currentRole, BusinessRole requiredRole, String denialReason) {
            this.hasPermission = hasPermission;
            this.currentRole = currentRole;
            this.requiredRole = requiredRole;
            this.denialReason = denialReason;
        }

        public static PermissionCheckResult allowed(BusinessRole currentRole) {
            return new PermissionCheckResult(true, currentRole, null, null);
        }

        public static PermissionCheckResult denied(BusinessRole currentRole, BusinessRole requiredRole, String denialReason) {
            return new PermissionCheckResult(false, currentRole, requiredRole, denialReason);
        }
    }

    /**
     * 업체 통계 정보
     */
    @Getter
    public static class BusinessStatistics {
        private final Long totalBusinesses;
        private final Long businessesByType;
        private final Long businessesByRegion;
        private final String businessType;
        private final String region;
        private final LocalDateTime queryTime;

        private BusinessStatistics(Long totalBusinesses, Long businessesByType, Long businessesByRegion,
                                    String businessType, String region, LocalDateTime queryTime) {
            this.totalBusinesses = totalBusinesses;
            this.businessesByType = businessesByType;
            this.businessesByRegion = businessesByRegion;
            this.businessType = businessType;
            this.region = region;
            this.queryTime = queryTime;
        }

        public static BusinessStatistics of(Long totalBusinesses, Long businessesByType, Long businessesByRegion) {
            return new BusinessStatistics(totalBusinesses, businessesByType, businessesByRegion,
                    null, null, LocalDateTime.now());
        }

        public static BusinessStatistics of(Long totalBusinesses, Long businessesByType, Long businessesByRegion,
                                            String businessType, String region) {
            return new BusinessStatistics(totalBusinesses, businessesByType, businessesByRegion,
                    businessType, region, LocalDateTime.now());
        }
    }
}