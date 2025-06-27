package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;
import timefit.user.entity.User;
import timefit.user.entity.UserRole;
import timefit.user.repository.UserRepository;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.factory.UserFactory;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLoginService {

    private final UserRepository userRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final OAuthValidationService oAuthValidationService;

    /**
     * 업체 사용자 로그인
     */
    @Transactional
    public UserLoginResult loginBusinessUser(AuthRequestDto.BusinessSignIn request) {

        // 1. 사용자 조회 및 검증
        User user = findAndValidateBusinessUser(request);

        // 2. 사용자의 비즈니스 권한 조회
        List<UserBusinessRole> userBusinessRoles = getUserBusinessRoles(user.getId());

        // 3. 로그인 시간 업데이트
        UserFactory.updateLastLogin(user);
        userRepository.save(user);

        return UserLoginResult.ofBusinessUser(user, userBusinessRoles);
    }

    /**
     * OAuth 사용자 로그인/가입
     */
    @Transactional
    public UserLoginResult loginOAuthUser(AuthRequestDto.CustomerOAuth request) {

        // 1. OAuth 토큰 검증
        AuthRequestDto.OAuthUserInfo oauthUserInfo = oAuthValidationService.validateToken(request);

        // 2. 기존 사용자 확인 또는 신규 생성
        User user = findOrCreateOAuthUser(request, oauthUserInfo);

        boolean isFirstLogin = user.getCreatedAt().equals(user.getLastLoginAt());

        return UserLoginResult.ofOAuthUser(user, isFirstLogin);
    }

    /**
     * 업체 사용자 조회 및 검증
     */
    private User findAndValidateBusinessUser(AuthRequestDto.BusinessSignIn request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!request.getPassword().equals(user.getPasswordHash())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        // 업체 역할 확인
        if (user.getRole() != UserRole.BUSINESS) {
            throw new AuthException(AuthErrorCode.ACCESS_DENIED);
        }

        return user;
    }

    /**
     * 사용자의 비즈니스 권한 조회
     */
    private List<UserBusinessRole> getUserBusinessRoles(java.util.UUID userId) {
        List<UserBusinessRole> userBusinessRoles = userBusinessRoleRepository.findByUserIdAndIsActive(userId, true);

        if (userBusinessRoles.isEmpty()) {
            throw new AuthException(AuthErrorCode.ACCESS_DENIED);
        }

        return userBusinessRoles;
    }

    /**
     * OAuth 사용자 조회 또는 생성
     */
    private User findOrCreateOAuthUser(AuthRequestDto.CustomerOAuth request, AuthRequestDto.OAuthUserInfo oauthUserInfo) {
        return userRepository.findByOauthProviderAndOauthId(request.getProvider(), request.getOauthId())
                .map(existingUser -> {
                    UserFactory.updateLastLogin(existingUser);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = UserFactory.createOAuthUser(
                            oauthUserInfo.getEmail(),
                            oauthUserInfo.getName(),
                            oauthUserInfo.getProfileImageUrl(),
                            request.getProvider(),
                            request.getOauthId()
                    );
                    return userRepository.save(newUser);
                });
    }
}