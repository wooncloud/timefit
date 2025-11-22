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

public class AuthResponseDto {

    // 일반 회원가입 응답
    @Schema(description = "일반 회원가입 성공 응답 데이터")
    public record UserSignUp(
            @Schema(description = "사용자 고유 ID", example = "a1b2c3d4-e5f6-7890-abcd-1234567890ab")
            UUID userId,
            @Schema(description = "사용자 이메일")
            String email,
            @Schema(description = "사용자 이름", minLength = 2, maxLength = 50)
            String name,
            @Schema(description = "사용자 연락처", nullable = true, example = "010-1234-5678")
            String phoneNumber,
            @Schema(description = "사용자 권한 (UserRole)", example = "BUSINESS")
            String role,
            @Schema(description = "프로필 이미지 URL", nullable = true)
            String profileImageUrl,
            @Schema(description = "발급된 Access Token")
            String accessToken,
            @Schema(description = "발급된 Refresh Token")
            String refreshToken,
            @Schema(description = "가입일시")
            LocalDateTime createdAt,
            @Schema(description = "최근 로그인일시")
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
    @Schema(description = "일반 로그인 성공 응답 데이터")
    public record UserSignIn(
            @Schema(description = "사용자 고유 ID", example = "a1b2c3d4-e5f6-7890-abcd-1234567890ab")
            UUID userId,
            @Schema(description = "사용자 이메일")
            String email,
            @Schema(description = "사용자 이름", minLength = 2, maxLength = 50)
            String name,
            @Schema(description = "사용자 연락처", nullable = true)
            String phoneNumber,
            @Schema(description = "사용자 권한 (UserRole)", example = "BUSINESS")
            String role,
            @Schema(description = "프로필 이미지 URL", nullable = true)
            String profileImageUrl,
            @Schema(description = "사용자에게 연결된 업체 목록", nullable = true)
            List<BusinessInfo> businesses,
            @Schema(description = "발급된 Access Token")
            String accessToken,
            @Schema(description = "발급된 Refresh Token")
            String refreshToken,
            @Schema(description = "가입일시")
            LocalDateTime createdAt,
            @Schema(description = "최근 로그인일시")
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
    @Schema(description = "토큰 갱신 성공 응답 데이터")
    public record TokenRefresh(
            @Schema(description = "새로 발급된 Access Token")
            String accessToken,
            @Schema(description = "새로 발급된 Refresh Token")
            String refreshToken,
            @Schema(description = "토큰 타입 (일반적으로 Bearer)", example = "Bearer")
            String tokenType,
            @Schema(description = "Access Token 만료까지 남은 시간 (초)", example = "3600")
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
    @Schema(description = "사용자가 속한 업체 정보")
    public record BusinessInfo(
            @Schema(description = "업체 고유 ID")
            UUID businessId,
            @Schema(description = "상호명", minLength = 2, maxLength = 100)
            String businessName,
            @Schema(
                    description = "업종 타입 코드 목록 (최소 1개 필수). 코드는 BusinessTypeCode Enum을 참조하세요.",
                    example = "[\"BD000\"]",
                    allowableValues = {
                            "BD000 (음식점)", "BD001 (카페)", "BD002 (숙박)", "BD003 (공연/전시)",
                            "BD004 (스포츠/오락)", "BD005 (레저/체험)", "BD006 (여행/명소)",
                            "BD007 (건강/의료)", "BD008 (뷰티)", "BD009 (생활/편의)",
                            "BD010 (쇼핑/유통)", "BD011 (장소 대여)", "BD012 (자연)",
                            "BD013 (기타)"
                    }
            )
            Set<BusinessTypeCode> businessTypes,
            @Schema(description = "업체 주소", maxLength = 200, nullable = true)
            String address,
            @Schema(description = "업체 연락처 (전화번호)", nullable = true)
            String contactPhone,
            @Schema(description = "해당 업체에 대한 사용자 역할 (예: ADMIN, MANAGER)")
            String userRole,
            @Schema(description = "업체 활성화 상태")
            Boolean isBusinessActive,
            @Schema(description = "업체 합류 일시")
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