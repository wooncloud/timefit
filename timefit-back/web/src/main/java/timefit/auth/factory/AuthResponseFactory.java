package timefit.auth.factory;

import org.springframework.stereotype.Component;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.UserLoginResult;
import timefit.auth.service.UserRegistrationResult;
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
     * 업체 회원가입 응답 생성
     */
    public AuthResponseDto.BusinessSignUp createBusinessSignUpResponse(UserRegistrationResult registrationResult, String token) {
        User user = registrationResult.getUser();
        Business business = registrationResult.getBusiness();
        UserBusinessRole userBusinessRole = registrationResult.getUserBusinessRole();

        AuthResponseDto.BusinessInfo businessInfo = createBusinessInfo(business, userBusinessRole);

        return AuthResponseDto.BusinessSignUp.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getRole().name(),
                user.getProfileImageUrl(),
                List.of(businessInfo),
                token,
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    /**
     * 업체 로그인 응답 생성
     */
    public AuthResponseDto.BusinessSignIn createBusinessSignInResponse(UserLoginResult loginResult, String token) {
        User user = loginResult.getUser();
        List<Business> businesses = loginResult.getBusinesses();
        List<UserBusinessRole> userBusinessRoles = loginResult.getUserBusinessRoles();

        List<AuthResponseDto.BusinessInfo> businessInfos = createBusinessInfos(businesses, userBusinessRoles);

        return AuthResponseDto.BusinessSignIn.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getRole().name(),
                user.getProfileImageUrl(),
                businessInfos,
                token,
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    /**
     * 고객 OAuth 로그인 응답 생성
     */
    public AuthResponseDto.CustomerOAuth createCustomerOAuthResponse(UserLoginResult loginResult, String token) {
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
                token,
                isFirstLogin,
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    // ===== Private Methods =====

    /**
     * 단일 비즈니스 정보 DTO 생성
     */
    private AuthResponseDto.BusinessInfo createBusinessInfo(Business business, UserBusinessRole userBusinessRole) {
        return AuthResponseDto.BusinessInfo.of(
                business.getId(),
                business.getBusinessName(),
                business.getBusinessType(),
                business.getBusinessNumber(),
                business.getAddress(),
                business.getContactPhone(),
                business.getDescription(),
                business.getLogoUrl(),
                userBusinessRole.getRole().name(),
                userBusinessRole.getJoinedAt(),
                business.getCreatedAt(),
                business.getUpdatedAt()
        );
    }

    /**
     * 다중 비즈니스 정보 DTO 리스트 생성
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
     * 비즈니스에 대응하는 UserBusinessRole 찾기
     */
    private UserBusinessRole findCorrespondingRole(Business business, List<UserBusinessRole> userBusinessRoles) {
        return userBusinessRoles.stream()
                .filter(role -> role.getBusiness().getId().equals(business.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("비즈니스에 해당하는 권한을 찾을 수 없습니다: " + business.getId()));
    }
}