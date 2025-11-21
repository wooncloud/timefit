package timefit.auth.dto;

import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.UserBusinessRole;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AuthResponseDto {

    // 일반 회원가입 응답
    public record UserSignUp(
            UUID userId,
            String email,
            String name,
            String phoneNumber,
            String role,
            String profileImageUrl,
            String accessToken,
            String refreshToken,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
    ) {
        public static UserSignUp of(
                User user,
                String accessToken,
                String refreshToken) {

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

    // 일반 로그인 응답
    public record UserSignIn(
            UUID userId,
            String email,
            String name,
            String phoneNumber,
            String role,
            String profileImageUrl,
            List<BusinessInfo> businesses,
            String accessToken,
            String refreshToken,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
    ) {
        public static UserSignIn of(
                User user,
                List<BusinessInfo> businesses,
                String accessToken,
                String refreshToken) {

            return new UserSignIn(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getRole().name(),
                    user.getProfileImageUrl(),
                    businesses,
                    accessToken,
                    refreshToken,
                    user.getCreatedAt(),
                    user.getLastLoginAt()
            );
        }
    }

    // OAuth 로그인 응답
    public record CustomerOAuth(
            UUID userId,
            String email,
            String name,
            String phoneNumber,
            String role,
            String profileImageUrl,
            List<BusinessInfo> businesses,
            String accessToken,
            String refreshToken,
            Boolean isFirstLogin,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
    ) {
        public static CustomerOAuth of(
                User user,
                List<BusinessInfo> businesses,
                String accessToken,
                String refreshToken,
                boolean isFirstLogin) {

            return new CustomerOAuth(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getRole().name(),
                    user.getProfileImageUrl(),
                    businesses,
                    accessToken,
                    refreshToken,
                    isFirstLogin,
                    user.getCreatedAt(),
                    user.getLastLoginAt()
            );
        }
    }

    // 토큰 갱신 응답
    public record TokenRefresh(
            String accessToken,
            String refreshToken,
            String tokenType,
            Long expiresIn
    ) {
        public static TokenRefresh of(
                String accessToken,
                String refreshToken,
                String tokenType,
                Long expiresIn) {
            return new TokenRefresh(accessToken, refreshToken, tokenType, expiresIn);
        }
    }

    // 업체 정보
    public record BusinessInfo(
            UUID businessId,
            String businessName,
            Set<BusinessTypeCode> businessTypes,
            String address,
            String contactPhone,
            String userRole,
            Boolean isBusinessActive,
            LocalDateTime joinedAt
    ) {
        public static BusinessInfo of(Business business, UserBusinessRole userBusinessRole) {
            return new BusinessInfo(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getAddress(),
                    business.getContactPhone(),
                    userBusinessRole.getRole().name(),
                    business.getIsActive(),
                    userBusinessRole.getCreatedAt()
            );
        }
    }
}