package timefit.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.UserBusinessRole;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Schema(description = "인증 응답")
public class AuthResponseDto {

    // 일반 회원가입 응답
    @Schema(description = "일반 회원가입 성공 응답 데이터")
    public record UserSignUp(
            @Schema(
                    description = "사용자 고유 ID",
                    example = "10000000-0000-0000-0000-000000000001"
            )
            UUID userId,

            @Schema(
                    description = "사용자 이메일",
                    example = "owner1@timefit.com"
            )
            String email,

            @Schema(
                    description = "사용자 이름",
                    example = "Owner Kim",
                    minLength = 2,
                    maxLength = 50
            )
            String name,

            @Schema(
                    description = "사용자 연락처",
                    example = "010-1111-1111",
                    nullable = true
            )
            String phoneNumber,

            @Schema(
                    description = "사용자 권한 (UserRole)",
                    example = "CUSTOMER",
                    allowableValues = {"CUSTOMER", "BUSINESS", "ADMIN"}
            )
            String role,

            @Schema(
                    description = "프로필 이미지 URL",
                    example = "https://example.com/profile.jpg",
                    nullable = true
            )
            String profileImageUrl,

            @Schema(
                    description = "발급된 Access Token",
                    example = "eyJhbGciOiJIUzI1NiJ9..."
            )
            String accessToken,

            @Schema(
                    description = "발급된 Refresh Token",
                    example = "eyJhbGciOiJIUzI1NiJ9..."
            )
            String refreshToken,

            @Schema(
                    description = "가입일시",
                    example = "2025-11-23T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "최근 로그인일시",
                    example = "2025-11-23T10:00:00"
            )
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
    @Schema(description = "일반 로그인 성공 응답")
    public record UserSignIn(
            @Schema(
                    description = "사용자 고유 ID",
                    example = "10000000-0000-0000-0000-000000000001"
            )
            UUID userId,

            @Schema(
                    description = "사용자 이메일",
                    example = "owner1@timefit.com"
            )
            String email,

            @Schema(
                    description = "사용자 이름",
                    example = "Owner Kim",
                    minLength = 2,
                    maxLength = 50
            )
            String name,

            @Schema(
                    description = "사용자 연락처",
                    example = "010-1111-1111",
                    nullable = true
            )
            String phoneNumber,

            @Schema(
                    description = "사용자 권한 (UserRole)",
                    example = "BUSINESS",
                    allowableValues = {"CUSTOMER", "BUSINESS", "ADMIN"}
            )
            String role,

            @Schema(
                    description = "프로필 이미지 URL",
                    example = "https://example.com/profile.jpg",
                    nullable = true
            )
            String profileImageUrl,

            @Schema(
                    description = "사용자에게 연결된 업체 목록",
                    nullable = true
            )
            List<BusinessInfo> businesses,

            @Schema(
                    description = "발급된 Access Token",
                    example = "eyJhbGciOiJIUzI1NiJ9..."
            )
            String accessToken,

            @Schema(
                    description = "발급된 Refresh Token",
                    example = "eyJhbGciOiJIUzI1NiJ9..."
            )
            String refreshToken,

            @Schema(
                    description = "가입일시",
                    example = "2025-11-01T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "최근 로그인일시",
                    example = "2025-11-23T10:00:00"
            )
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
    @Schema(description = "OAuth 로그인 성공 응답")
    public record CustomerOAuth(
            @Schema(
                    description = "사용자 고유 ID",
                    example = "10000000-0000-0000-0000-000000000001"
            )
            UUID userId,

            @Schema(
                    description = "사용자 이메일",
                    example = "owner1@timefit.com",
                    nullable = true
            )
            String email,

            @Schema(
                    description = "사용자 이름",
                    example = "Owner Kim"
            )
            String name,

            @Schema(
                    description = "사용자 연락처",
                    example = "010-1111-1111",
                    nullable = true
            )
            String phoneNumber,

            @Schema(
                    description = "사용자 권한",
                    example = "CUSTOMER",
                    allowableValues = {"CUSTOMER", "BUSINESS", "ADMIN"}
            )
            String role,

            @Schema(
                    description = "프로필 이미지 URL",
                    example = "https://example.com/profile.jpg",
                    nullable = true
            )
            String profileImageUrl,

            @Schema(
                    description = "사용자에게 연결된 업체 목록",
                    nullable = true
            )
            List<BusinessInfo> businesses,

            @Schema(
                    description = "발급된 Access Token",
                    example = "eyJhbGciOiJIUzI1NiJ9..."
            )
            String accessToken,

            @Schema(
                    description = "발급된 Refresh Token",
                    example = "eyJhbGciOiJIUzI1NiJ9..."
            )
            String refreshToken,

            @Schema(
                    description = "최초 로그인 여부 (회원가입 여부)",
                    example = "false"
            )
            Boolean isFirstLogin,

            @Schema(
                    description = "가입일시",
                    example = "2025-11-01T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "최근 로그인일시",
                    example = "2025-11-23T10:00:00"
            )
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

    @Schema(description = "토큰 갱신 성공 응답")
    public record TokenRefresh(
            @Schema(
                    description = "새로 발급된 Access Token",
                    example = "eyJhbGciOiJIUzI1NiJ9..."
            )
            String accessToken,

            @Schema(
                    description = "새로 발급된 Refresh Token",
                    example = "eyJhbGciOiJIUzI1NiJ9..."
            )
            String refreshToken,

            @Schema(
                    description = "토큰 타입",
                    example = "Bearer"
            )
            String tokenType,

            @Schema(
                    description = "Access Token 만료까지 남은 시간 (초)",
                    example = "3600"
            )
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

    @Schema(description = "사용자가 속한 업체 정보")
    public record BusinessInfo(
            @Schema(
                    description = "업체 고유 ID",
                    example = "30000000-0000-0000-0000-000000000001"
            )
            UUID businessId,

            @Schema(
                    description = "상호명",
                    example = "강남 헤어샵",
                    minLength = 2,
                    maxLength = 100
            )
            String businessName,

            @Schema(
                    description = "업종 코드 목록 (최소 1개 필수)",
                    example = "[\"BD008\"]",
                    allowableValues = {"BD000", "BD001", "BD002", "BD003", "BD004", "BD005", "BD006", "BD007", "BD008", "BD009", "BD010", "BD011", "BD012", "BD013"}
            )
            Set<BusinessTypeCode> businessTypes,

            @Schema(
                    description = "업체 주소",
                    example = "서울시 강남구 강남대로 123",
                    maxLength = 200,
                    nullable = true
            )
            String address,

            @Schema(
                    description = "업체 연락처",
                    example = "02-1111-1111",
                    nullable = true
            )
            String contactPhone,

            @Schema(
                    description = "해당 업체에 대한 사용자 역할",
                    example = "OWNER",
                    allowableValues = {"OWNER", "MANAGER", "MEMBER"}
            )
            String userRole,

            @Schema(
                    description = "업체 활성화 상태",
                    example = "true"
            )
            Boolean isBusinessActive,

            @Schema(
                    description = "업체 합류 일시",
                    example = "2025-11-01T10:00:00"
            )
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