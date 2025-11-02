package timefit.auth.dto;

import lombok.Getter;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.UserBusinessRole;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AuthResponseDto {

    @Getter
    public static class UserSignUp {
        private final UUID userId;
        private final String email;
        private final String name;
        private final String phoneNumber;
        private final String role;
        private final String profileImageUrl;
        private final String accessToken;
        private final String refreshToken;
        private final LocalDateTime createdAt;
        private final LocalDateTime lastLoginAt;

        private UserSignUp(UUID userId, String email, String name, String phoneNumber, String role,
                           String profileImageUrl, String accessToken, String refreshToken,
                           LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.profileImageUrl = profileImageUrl;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         * 회원가입 응답 생성
         */
        public static UserSignUp of(User user, String accessToken, String refreshToken) {
            return new UserSignUp(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getRole().name(),
                    user.getProfileImageUrl(),
                    accessToken,
                    refreshToken,
                    user.getCreatedAt(),
                    user.getLastLoginAt()
            );
        }
    }

    @Getter
    public static class UserSignIn {
        private final UUID userId;
        private final String email;
        private final String name;
        private final String phoneNumber;
        private final String role;
        private final String profileImageUrl;
        private final List<BusinessInfo> businesses;
        private final String accessToken;
        private final String refreshToken;
        private final LocalDateTime createdAt;
        private final LocalDateTime lastLoginAt;

        private UserSignIn(UUID userId, String email, String name, String phoneNumber, String role,
                           String profileImageUrl, List<BusinessInfo> businesses, String accessToken, String refreshToken,
                           LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.profileImageUrl = profileImageUrl;
            this.businesses = businesses;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         * 로그인 응답 생성 (비즈니스 정보 포함)
         */
        public static UserSignIn of(
                User user,
                List<UserBusinessRole> userBusinessRoles,
                String accessToken,
                String refreshToken) {

            List<BusinessInfo> businessInfos = createBusinessInfos(userBusinessRoles);

            return new UserSignIn(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getRole().name(),
                    user.getProfileImageUrl(),
                    businessInfos,
                    accessToken,
                    refreshToken,
                    user.getCreatedAt(),
                    user.getLastLoginAt()
            );
        }

        /**
         * BusinessInfo 목록 생성 (Helper 메서드)
         */
        private static List<BusinessInfo> createBusinessInfos(List<UserBusinessRole> userBusinessRoles) {
            return userBusinessRoles.stream()
                    .map(role -> BusinessInfo.of(role.getBusiness(), role))
                    .toList();
        }
    }

    @Getter
    public static class CustomerOAuth {
        private final UUID userId;
        private final String email;
        private final String name;
        private final String phoneNumber;
        private final String role;
        private final String profileImageUrl;
        private final List<BusinessInfo> businesses;
        private final String accessToken;
        private final String refreshToken;
        private final Boolean isFirstLogin;
        private final LocalDateTime createdAt;
        private final LocalDateTime lastLoginAt;

        private CustomerOAuth(UUID userId, String email, String name, String phoneNumber, String role,
                              String profileImageUrl, List<BusinessInfo> businesses, String accessToken, String refreshToken,
                              Boolean isFirstLogin, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.profileImageUrl = profileImageUrl;
            this.businesses = businesses;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.isFirstLogin = isFirstLogin;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         * OAuth 로그인 응답 생성
         */
        public static CustomerOAuth of(
                User user,
                List<UserBusinessRole> userBusinessRoles,
                String accessToken,
                String refreshToken,
                boolean isFirstLogin) {

            List<BusinessInfo> businessInfos = createBusinessInfos(userBusinessRoles);

            return new CustomerOAuth(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getRole().name(),
                    user.getProfileImageUrl(),
                    businessInfos,
                    accessToken,
                    refreshToken,
                    isFirstLogin,
                    user.getCreatedAt(),
                    user.getLastLoginAt()
            );
        }

        /**
         * BusinessInfo 목록 생성 (Helper 메서드)
         */
        private static List<BusinessInfo> createBusinessInfos(List<UserBusinessRole> userBusinessRoles) {
            return userBusinessRoles.stream()
                    .map(role -> BusinessInfo.of(role.getBusiness(), role))
                    .toList();
        }
    }

    @Getter
    public static class TokenRefresh {
        private final String accessToken;
        private final String refreshToken;
        private final String tokenType;
        private final Long expiresIn;

        private TokenRefresh(String accessToken, String refreshToken, String tokenType, Long expiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
        }

        public static TokenRefresh of(String accessToken, String refreshToken, String tokenType, Long expiresIn) {
            return new TokenRefresh(accessToken, refreshToken, tokenType, expiresIn);
        }
    }

    @Getter
    public static class BusinessInfo {
        private final UUID businessId;
        private final String businessName;
        private final Set<BusinessTypeCode> businessTypes;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;
        private final String role;
        private final LocalDateTime joinedAt;
        private final Boolean isActive;
        private final LocalDateTime createdAt;

        private BusinessInfo(UUID businessId, String businessName, Set<BusinessTypeCode> businessTypes,
                             String address, String contactPhone, String description, String logoUrl, String role,
                             LocalDateTime joinedAt, Boolean isActive, LocalDateTime createdAt) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessTypes = businessTypes;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
            this.role = role;
            this.joinedAt = joinedAt;
            this.isActive = isActive;
            this.createdAt = createdAt;
        }

        /**
         * Entity → DTO 변환 (정적 팩토리)
         */
        public static BusinessInfo of(Business business, UserBusinessRole userBusinessRole) {
            return new BusinessInfo(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getAddress(),
                    business.getContactPhone(),
                    business.getDescription(),
                    business.getLogoUrl(),
                    userBusinessRole.getRole().name(),
                    userBusinessRole.getJoinedAt(),
                    business.getIsActive(),
                    business.getCreatedAt()
            );
        }
    }
}