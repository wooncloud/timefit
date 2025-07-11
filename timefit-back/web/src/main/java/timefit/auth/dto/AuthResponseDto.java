package timefit.auth.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AuthResponseDto {

    @Getter
    public static class BusinessSignUp {
        private final UUID userId;
        private final String email;
        private final String name;
        private final String phoneNumber;
        private final String role;
        private final String profileImageUrl;
        private final List<BusinessInfo> businesses;
        private final String temporaryToken;
        private final LocalDateTime createdAt;
        private final LocalDateTime lastLoginAt;

        private BusinessSignUp(UUID userId, String email, String name, String phoneNumber, String role,
                                String profileImageUrl, List<BusinessInfo> businesses, String temporaryToken,
                                LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.profileImageUrl = profileImageUrl;
            this.businesses = businesses;
            this.temporaryToken = temporaryToken;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        public static BusinessSignUp of(UUID userId, String email, String name, String phoneNumber, String role,
                                        String profileImageUrl, List<BusinessInfo> businesses, String temporaryToken,
                                        LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            return new BusinessSignUp(userId, email, name, phoneNumber, role, profileImageUrl,
                    businesses, temporaryToken, createdAt, lastLoginAt);
        }
    }


    @Getter
    public static class BusinessSignIn {
        private final UUID userId;
        private final String email;
        private final String name;
        private final String phoneNumber;
        private final String role;
        private final String profileImageUrl;
        private final List<BusinessInfo> businesses;
        private final String temporaryToken;
        private final LocalDateTime createdAt;
        private final LocalDateTime lastLoginAt;

        private BusinessSignIn(UUID userId, String email, String name, String phoneNumber, String role,
                                String profileImageUrl, List<BusinessInfo> businesses, String temporaryToken,
                                LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.profileImageUrl = profileImageUrl;
            this.businesses = businesses;
            this.temporaryToken = temporaryToken;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        public static BusinessSignIn of(UUID userId, String email, String name, String phoneNumber, String role,
                                        String profileImageUrl, List<BusinessInfo> businesses, String temporaryToken,
                                        LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            return new BusinessSignIn(userId, email, name, phoneNumber, role, profileImageUrl,
                    businesses, temporaryToken, createdAt, lastLoginAt);
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
        private final String temporaryToken;
        private final Boolean isFirstLogin;
        private final LocalDateTime createdAt;
        private final LocalDateTime lastLoginAt;

        private CustomerOAuth(UUID userId, String email, String name, String phoneNumber, String role,
                                String profileImageUrl, String oauthProvider, String oauthId, String temporaryToken,
                                Boolean isFirstLogin, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.profileImageUrl = profileImageUrl;
            this.oauthProvider = oauthProvider;
            this.oauthId = oauthId;
            this.temporaryToken = temporaryToken;
            this.isFirstLogin = isFirstLogin;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        public static CustomerOAuth of(UUID userId, String email, String name, String phoneNumber, String role,
                                        String profileImageUrl, String oauthProvider, String oauthId, String temporaryToken,
                                        Boolean isFirstLogin, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
            return new CustomerOAuth(userId, email, name, phoneNumber, role, profileImageUrl, oauthProvider,
                    oauthId, temporaryToken, isFirstLogin, createdAt, lastLoginAt);
        }
    }


    @Getter
    public static class BusinessInfo {
        private final UUID businessId;
        private final String businessName;
        private final String businessType;
        private final String businessNumber;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;
        private final String role;
        private final LocalDateTime joinedAt;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private BusinessInfo(UUID businessId, String businessName, String businessType, String businessNumber,
                                String address, String contactPhone, String description, String logoUrl, String role,
                                LocalDateTime joinedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessType = businessType;
            this.businessNumber = businessNumber;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
            this.role = role;
            this.joinedAt = joinedAt;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static BusinessInfo of(UUID businessId, String businessName, String businessType, String businessNumber,
                                        String address, String contactPhone, String description, String logoUrl, String role,
                                        LocalDateTime joinedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new BusinessInfo(businessId, businessName, businessType, businessNumber, address, contactPhone,
                    description, logoUrl, role, joinedAt, createdAt, updatedAt);
        }
    }
}