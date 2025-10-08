package timefit.auth.dto;

import lombok.Getter;
import timefit.business.entity.BusinessTypeCode;

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

        public static UserSignUp of(UUID userId, String email, String name, String phoneNumber, String role,
                                    String profileImageUrl, String accessToken, String refreshToken,
                                    LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            return new UserSignUp(userId, email, name, phoneNumber, role, profileImageUrl,
                    accessToken, refreshToken, createdAt, lastLoginAt);
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

        public static UserSignIn of(UUID userId, String email, String name, String phoneNumber, String role,
                                    String profileImageUrl, List<BusinessInfo> businesses, String accessToken, String refreshToken,
                                    LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            return new UserSignIn(userId, email, name, phoneNumber, role, profileImageUrl,
                    businesses, accessToken, refreshToken, createdAt, lastLoginAt);
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

        public static CustomerOAuth of(UUID userId, String email, String name, String phoneNumber, String role,
                                        String profileImageUrl, List<BusinessInfo> businesses, String accessToken, String refreshToken,
                                        Boolean isFirstLogin, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            return new CustomerOAuth(userId, email, name, phoneNumber, role, profileImageUrl,
                    businesses, accessToken, refreshToken, isFirstLogin, createdAt, lastLoginAt);
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

        public static BusinessInfo of(UUID businessId, String businessName, Set<BusinessTypeCode> businessTypes,
                                        String address, String contactPhone, String description, String logoUrl, String role,
                                        LocalDateTime joinedAt, Boolean isActive, LocalDateTime createdAt) {
            return new BusinessInfo(businessId, businessName, businessTypes, address, contactPhone,
                    description, logoUrl, role, joinedAt, isActive, createdAt);
        }
    }
}