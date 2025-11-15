package timefit.business.dto;

import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.UserBusinessRole;
import timefit.common.entity.BusinessRole;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BusinessResponseDto {

    // 사업자용 업체 상세 정보
    public record BusinessResponse(
            UUID businessId,
            String businessName,
            Set<BusinessTypeCode> businessTypes,
            String businessNumber,
            String ownerName,
            String address,
            String contactPhone,
            String description,
            String logoUrl,
            String businessNotice,
            Boolean isActive,
            BusinessRole myRole,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static BusinessResponse of(Business business, UserBusinessRole userRole) {
            return new BusinessResponse(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getBusinessNumber(),
                    business.getOwnerName(),
                    business.getAddress(),
                    business.getContactPhone(),
                    business.getDescription(),
                    business.getLogoUrl(),
                    business.getBusinessNotice(),
                    business.getIsActive(),
                    userRole.getRole(),
                    business.getCreatedAt(),
                    business.getUpdatedAt()
            );
        }
    }

    // 고객용 업체 공개 정보
    public record PublicBusinessResponse(
            UUID businessId,
            String businessName,
            Set<BusinessTypeCode> businessTypes,
            String ownerName,
            String address,
            String contactPhone,
            String description,
            String logoUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static PublicBusinessResponse of(Business business) {
            return new PublicBusinessResponse(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getOwnerName(),
                    business.getAddress(),
                    business.getContactPhone(),
                    business.getDescription(),
                    business.getLogoUrl(),
                    business.getCreatedAt(),
                    business.getUpdatedAt()
            );
        }
    }

    // 내 업체 목록 응답
    public record BusinessListResponse(
            List<BusinessItem> businesses,
            Integer totalCount
    ) {
        public record BusinessItem(
                UUID businessId,
                String businessName,
                Set<BusinessTypeCode> businessTypes,
                String logoUrl,
                BusinessRole myRole,
                LocalDateTime joinedAt,
                Boolean isActive
        ) {
            public static BusinessItem of(Business business, UserBusinessRole userRole) {
                return new BusinessItem(
                        business.getId(),
                        business.getBusinessName(),
                        business.getBusinessTypes(),
                        business.getLogoUrl(),
                        userRole.getRole(),
                        userRole.getJoinedAt(),
                        userRole.getIsActive()
                );
            }
        }

        public static BusinessListResponse of(List<UserBusinessRole> userRoles) {
            List<BusinessItem> items = userRoles.stream()
                    .map(userRole -> BusinessItem.of(userRole.getBusiness(), userRole))
                    .toList();

            return new BusinessListResponse(items, items.size());
        }
    }

    // 팀원 목록 응답
    public record MemberListResponse(
            UUID businessId,
            String businessName,
            List<MemberResponse> members,
            Integer totalCount
    ) {
        public record MemberResponse(
                UUID userId,
                String email,
                String name,
                BusinessRole role,
                LocalDateTime joinedAt,
                Boolean isActive,
                String invitedByName,
                LocalDateTime lastLoginAt
        ) {
            public static MemberResponse of(UserBusinessRole userRole) {
                User user = userRole.getUser();
                User invitedBy = userRole.getInvitedBy();

                return new MemberResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        userRole.getRole(),
                        userRole.getJoinedAt(),
                        userRole.getIsActive(),
                        invitedBy != null ? invitedBy.getName() : null,
                        user.getLastLoginAt()
                );
            }
        }

        public static MemberListResponse of(Business business, List<UserBusinessRole> userRoles) {
            List<MemberResponse> members = userRoles.stream()
                    .map(MemberResponse::of)
                    .toList();

            return new MemberListResponse(
                    business.getId(),
                    business.getBusinessName(),
                    members,
                    members.size()
            );
        }
    }

    // 업체 삭제 결과
    public record DeleteBusinessResponse(
            UUID businessId,
            String businessName,
            LocalDateTime deletedAt,
            String deleteReason
    ) {
        public static DeleteBusinessResponse of(Business business, String deleteReason) {
            return new DeleteBusinessResponse(
                    business.getId(),
                    business.getBusinessName(),
                    LocalDateTime.now(),
                    deleteReason
            );
        }
    }
}