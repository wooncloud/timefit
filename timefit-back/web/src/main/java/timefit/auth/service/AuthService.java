package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;

/**
 * Auth Facade Service
 *
 * 역할:
 * - 단일 진입점 (Facade 패턴)
 * - 단순 위임만 수행
 * - 트랜잭션 경계 설정
 *
 * 책임 분리:
 * - UserRegistrationService: 회원가입
 * - UserLoginService: 로그인
 * - AuthTokenService: 토큰 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRegistrationService userRegistrationService;
    private final UserLoginService userLoginService;
    private final AuthTokenService authTokenService;

    /**
     * 사용자 회원가입
     */
    @Transactional
    public AuthResponseDto.UserSignUp signup(AuthRequestDto.UserSignUp request) {
        return userRegistrationService.registerUser(request);
    }

    /**
     * 사용자 로그인
     */
    @Transactional
    public AuthResponseDto.UserSignIn signin(AuthRequestDto.UserSignIn request) {
        return userLoginService.loginUser(request);
    }

    /**
     * 고객 OAuth 로그인
     */
    @Transactional
    public AuthResponseDto.CustomerOAuth customerOAuthLogin(AuthRequestDto.CustomerOAuth request) {
        return userLoginService.loginOAuthUser(request);
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public AuthResponseDto.TokenRefresh refreshToken(AuthRequestDto.TokenRefresh request) {
        return authTokenService.refreshToken(request);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(AuthRequestDto.Logout request) {
        authTokenService.invalidateToken(request.getTemporaryToken());
    }
}