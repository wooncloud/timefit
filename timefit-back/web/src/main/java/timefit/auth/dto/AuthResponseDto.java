package timefit.auth.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
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
        private final LocalDateTime createdAt;
        private final LocalDateTime lastLoginAt;

        private UserSignUp(UUID userId, String email, String name, String phoneNumber, String role,
                           String profileImageUrl, String accessToken,
                           LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.profileImageUrl = profileImageUrl;
            this.accessToken = accessToken;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        public static UserSignUp of(UUID userId, String email, String name, String phoneNumber, String role,
                                    String profileImageUrl, String accessToken,
                                    LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            return new UserSignUp(userId, email, name, phoneNumber, role, profileImageUrl,
                    accessToken, createdAt, lastLoginAt);
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
        private final LocalDateTime createdAt;
        private final LocalDateTime lastLoginAt;

        private UserSignIn(UUID userId, String email, String name, String phoneNumber, String role,
                           String profileImageUrl, List<BusinessInfo> businesses, String accessToken,
                           LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.profileImageUrl = profileImageUrl;
            this.businesses = businesses;
            this.accessToken = accessToken;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        public static UserSignIn of(UUID userId, String email, String name, String phoneNumber, String role,
                                    String profileImageUrl, List<BusinessInfo> businesses, String accessToken,
                                    LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            return new UserSignIn(userId, email, name, phoneNumber, role, profileImageUrl,
                    businesses, accessToken, createdAt, lastLoginAt);
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
        private final String oauthProvider;
        private final String oauthId;
        private final String accessToken;
        private final Boolean isFirstLogin;
        private final LocalDateTime createdAt;
        private final LocalDateTime lastLoginAt;

        private CustomerOAuth(UUID userId, String email, String name, String phoneNumber, String role,
                              String profileImageUrl, String oauthProvider, String oauthId, String accessToken,
                              Boolean isFirstLogin, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.profileImageUrl = profileImageUrl;
            this.oauthProvider = oauthProvider;
            this.oauthId = oauthId;
            this.accessToken = accessToken;
            this.isFirstLogin = isFirstLogin;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        public static CustomerOAuth of(UUID userId, String email, String name, String phoneNumber, String role,
                                       String profileImageUrl, String oauthProvider, String oauthId, String accessToken,
                                       Boolean isFirstLogin, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            return new CustomerOAuth(userId, email, name, phoneNumber, role, profileImageUrl, oauthProvider,
                    oauthId, accessToken, isFirstLogin, createdAt, lastLoginAt);
        }
    }

    @Getter
    public static class BusinessInfo {
        private final UUID businessId;
        private final String businessName;
        private final String businessType;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;
        private final String role;
        private final LocalDateTime joinedAt;
        private final Boolean isActive;
        private final LocalDateTime createdAt;

        private BusinessInfo(UUID businessId, String businessName, String businessType,
                             String address, String contactPhone, String description, String logoUrl, String role,
                             LocalDateTime joinedAt, Boolean isActive, LocalDateTime createdAt) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessType = businessType;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
            this.role = role;
            this.joinedAt = joinedAt;
            this.isActive = isActive;
            this.createdAt = createdAt;
        }

        public static BusinessInfo of(UUID businessId, String businessName, String businessType,
                                      String address, String contactPhone, String description, String logoUrl, String role,
                                      LocalDateTime joinedAt, Boolean isActive, LocalDateTime createdAt) {
            return new BusinessInfo(businessId, businessName, businessType, address, contactPhone,
                    description, logoUrl, role, joinedAt, isActive, createdAt);
        }
    }
}