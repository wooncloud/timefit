package timefit.auth.factory;

import org.springframework.stereotype.Component;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.UserLoginResult;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.user.entity.User;

import java.util.List;

/**
 * Auth 관련 응답 DTO 생성을 담당하는 Factory 클래스
 */
@Component
public class AuthResponseFactory {

    /**
     * 회원가입 응답 생성
     */
    public AuthResponseDto.UserSignUp createSignUpResponse(User user, String accessToken) {
        return AuthResponseDto.UserSignUp.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getRole().name(),
                user.getProfileImageUrl(),
                accessToken,
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    /**
     * 로그인 응답 생성
     */
    public AuthResponseDto.UserSignIn createSignInResponse(UserLoginResult loginResult, String accessToken) {
        User user = loginResult.getUser();
        List<Business> businesses = loginResult.getBusinesses();
        List<UserBusinessRole> userBusinessRoles = loginResult.getUserBusinessRoles();

        List<AuthResponseDto.BusinessInfo> businessInfos = createBusinessInfos(businesses, userBusinessRoles);

        return AuthResponseDto.UserSignIn.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getRole().name(),
                user.getProfileImageUrl(),
                businessInfos,
                accessToken,
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    /**
     * OAuth 로그인 응답 생성
     */
    public AuthResponseDto.CustomerOAuth createOAuthResponse(UserLoginResult loginResult, String accessToken) {
        User user = loginResult.getUser();
        boolean isFirstLogin = loginResult.isFirstLogin();

        return AuthResponseDto.CustomerOAuth.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getRole().name(),
                user.getProfileImageUrl(),
                user.getOauthProvider(),
                user.getOauthId(),
                accessToken,
                isFirstLogin,
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    // ========== Private Helper Methods ==========

    /**
     * BusinessInfo 목록 생성
     */
    private List<AuthResponseDto.BusinessInfo> createBusinessInfos(List<Business> businesses, List<UserBusinessRole> userBusinessRoles) {
        return businesses.stream()
                .map(business -> {
                    UserBusinessRole correspondingRole = findCorrespondingRole(business, userBusinessRoles);
                    return createBusinessInfo(business, correspondingRole);
                })
                .toList();
    }

    /**
     * 단일 BusinessInfo 생성
     */
    private AuthResponseDto.BusinessInfo createBusinessInfo(Business business, UserBusinessRole userBusinessRole) {
        return AuthResponseDto.BusinessInfo.of(
                business.getId(),
                business.getBusinessName(),
                business.getBusinessType(),
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

    /**
     * Business에 대응하는 UserBusinessRole 찾기
     */
    private UserBusinessRole findCorrespondingRole(Business business, List<UserBusinessRole> userBusinessRoles) {
        return userBusinessRoles.stream()
                .filter(role -> role.getBusiness().getId().equals(business.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("비즈니스에 해당하는 권한을 찾을 수 없습니다: " + business.getId()));
    }
}