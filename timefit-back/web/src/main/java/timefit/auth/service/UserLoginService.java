package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.util.AuthTokenHelper;
import timefit.auth.service.validator.AuthValidator;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.List;

/**
 * 사용자 로그인 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLoginService {

    private final UserRepository userRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final OAuthValidationService oAuthValidationService;
    private final AuthValidator authValidator;
    private final AuthTokenHelper authTokenHelper;

    /**
     * 일반 로그인 (인증 + 토큰 발급)
     *
     * @return 로그인 응답 DTO
     */
    @Transactional
    public AuthResponseDto.UserSignIn loginUser(AuthRequestDto.UserSignIn request) {
        log.info("사용자 로그인 처리 시작: email={}", request.getEmail());

        // 1. 사용자 조회 및 검증 (Validator 사용)
        User user = authValidator.validateUserCredentials(
                request.getEmail(),
                request.getPassword()
        );

        // 2. 마지막 로그인 시간 업데이트
        user.updateLastLogin();
        userRepository.save(user);

        // 3. 사용자의 비즈니스 권한 조회
        List<UserBusinessRole> userBusinessRoles = getUserBusinessRoles(user.getId());

        // 4. 토큰 생성 (Helper 사용)
        AuthTokenHelper.TokenPair tokenPair = authTokenHelper.generateTokenPair(user.getId());

        // 5. DTO 변환 (UserLoginResult 없이 직접 DTO 생성)
        log.info("사용자 로그인 완료: userId={}, businessCount={}",
                user.getId(), userBusinessRoles.size());

        return AuthResponseDto.UserSignIn.of(
                user,
                userBusinessRoles,
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken()
        );
    }

    /**
     * OAuth 로그인 (인증 + 토큰 발급)
     *
     * @return OAuth 로그인 응답 DTO
     */
    @Transactional
    public AuthResponseDto.CustomerOAuth loginOAuthUser(AuthRequestDto.CustomerOAuth request) {
        log.info("OAuth 로그인 처리 시작: provider={}", request.getProvider());

        // 1. OAuth 토큰 검증
        AuthRequestDto.OAuthUserInfo oauthUserInfo = oAuthValidationService.validateToken(request);

        // 2. 기존 사용자 확인 또는 신규 생성
        User user = findOrCreateOAuthUser(request, oauthUserInfo);

        boolean isFirstLogin = user.getCreatedAt().equals(user.getLastLoginAt());

        // 3. 사용자의 비즈니스 권한 조회 (OAuth도 비즈니스 가능)
        List<UserBusinessRole> userBusinessRoles = getUserBusinessRoles(user.getId());

        // 4. 토큰 생성 (Helper 사용)
        AuthTokenHelper.TokenPair tokenPair = authTokenHelper.generateTokenPair(user.getId());

        // 5. DTO 변환 (UserLoginResult 없이 직접 DTO 생성)
        log.info("OAuth 로그인 완료: userId={}, isFirstLogin={}",
                user.getId(), isFirstLogin);

        return AuthResponseDto.CustomerOAuth.of(
                user,
                userBusinessRoles,
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken(),
                isFirstLogin
        );
    }

    /**
     * 사용자의 비즈니스 권한 조회
     */
    private List<UserBusinessRole> getUserBusinessRoles(java.util.UUID userId) {
        return userBusinessRoleRepository.findByUserIdAndIsActive(userId, true);
    }

    /**
     * OAuth 사용자 조회 또는 생성
     */
    private User findOrCreateOAuthUser(
            AuthRequestDto.CustomerOAuth request,
            AuthRequestDto.OAuthUserInfo oauthUserInfo) {

        // Validator를 통해 기존 사용자 확인
        User existingUser = authValidator.findOAuthUser(
                request.getProvider(),
                request.getOauthId()
        );

        if (existingUser != null) {
            // 기존 사용자 - 마지막 로그인 시간 업데이트
            existingUser.updateLastLogin();
            return userRepository.save(existingUser);
        } else {
            // 신규 사용자 - OAuth 사용자 생성 (Entity 정적 팩토리)
            User newUser = User.createOAuthUser(
                    oauthUserInfo.getEmail(),
                    oauthUserInfo.getName(),
                    oauthUserInfo.getProfileImageUrl(),
                    request.getProvider(),
                    request.getOauthId()
            );
            return userRepository.save(newUser);
        }
    }
}