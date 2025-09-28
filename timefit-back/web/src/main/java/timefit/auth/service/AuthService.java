package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import timefit.auth.factory.AuthResponseFactory;
import timefit.common.ResponseData;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRegistrationService userRegistrationService;
    private final UserLoginService userLoginService;
    private final AuthTokenService authTokenService;
    private final AuthResponseFactory authResponseFactory;

    /**
     * 사용자 회원가입
     */
    @Transactional
    public ResponseData<AuthResponseDto.UserSignUp> signup(AuthRequestDto.UserSignUp request) {
        log.info("회원가입 시작: email={}", request.getEmail());

        // 1. 순수 사용자 등록 (User만 생성, 업체 정보 없음)
        UserRegistrationResult registrationResult = userRegistrationService.registerUser(request);

        // 2. JWT 토큰 생성
        String accessToken = authTokenService.generateToken(registrationResult.getUser().getId());
        String refreshToken = authTokenService.generateRefreshToken(registrationResult.getUser().getId());

        // 3. 응답 생성
        AuthResponseDto.UserSignUp response = authResponseFactory.createSignUpResponse(
                registrationResult.getUser(), accessToken, refreshToken);

        log.info("회원가입 완료: userId={}", registrationResult.getUser().getId());
        return ResponseData.of(response);
    }

    /**
     * 사용자 로그인
     */
    @Transactional
    public ResponseData<AuthResponseDto.UserSignIn> signin(AuthRequestDto.UserSignIn request) {
        log.info("로그인 시작: email={}", request.getEmail());

        // 1. 로그인 처리
        UserLoginResult loginResult = userLoginService.loginUser(request);

        // 2. JWT 토큰 생성
        String accessToken = authTokenService.generateToken(loginResult.getUser().getId());
        String refreshToken = authTokenService.generateRefreshToken(loginResult.getUser().getId());

        // 3. 응답 생성
        AuthResponseDto.UserSignIn response = authResponseFactory.createSignInResponse(
                loginResult, accessToken, refreshToken);

        log.info("로그인 완료: userId={}", loginResult.getUser().getId());
        return ResponseData.of(response);
    }

    /**
     * 고객 OAuth 로그인
     */
    @Transactional
    public ResponseData<AuthResponseDto.CustomerOAuth> customerOAuthLogin(AuthRequestDto.CustomerOAuth request) {
        log.info("고객 OAuth 로그인 시작: provider={}", request.getProvider());

        // 1. OAuth 로그인 처리
        UserLoginResult loginResult = userLoginService.loginOAuthUser(request);

        // 2. JWT 토큰 생성 (Access Token + Refresh Token)
        String accessToken = authTokenService.generateToken(loginResult.getUser().getId());
        String refreshToken = authTokenService.generateRefreshToken(loginResult.getUser().getId());

        // 3. 응답 생성
        AuthResponseDto.CustomerOAuth response = authResponseFactory.createOAuthResponse(
                loginResult, accessToken, refreshToken);

        log.info("고객 OAuth 로그인 완료: userId={}", loginResult.getUser().getId());
        return ResponseData.of(response);
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public ResponseData<AuthResponseDto.TokenRefresh> refreshToken(AuthRequestDto.TokenRefresh request) {
        log.info("토큰 갱신 시작");

        // 1. Refresh Token 유효성 검증
        authTokenService.isValidToken(request.getRefreshToken());

        // 2. 사용자 ID 추출
        java.util.UUID userId = authTokenService.getUserIdFromToken(request.getRefreshToken());

        // 3. 토큰 재생성
        String newAccessToken = authTokenService.generateToken(userId);
        String newRefreshToken = authTokenService.generateRefreshToken(userId);

        // 4. 만료 시간 계산
        Date expirationDate = authTokenService.getExpirationDate(newAccessToken);
        long expiresIn = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

        // 5. 응답 생성
        AuthResponseDto.TokenRefresh response = AuthResponseDto.TokenRefresh.of(
                newAccessToken, newRefreshToken, "Bearer", expiresIn);

        log.info("토큰 갱신 완료: userId={}", userId);
        return ResponseData.of(response);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public ResponseData<Void> logout(AuthRequestDto.Logout request) {
        log.info("로그아웃 시작");

        // JWT 토큰 무효화 (현재는 로그만 기록)
        authTokenService.invalidateToken(request.getTemporaryToken());

        log.info("로그아웃 완료");
        return ResponseData.of(null);
    }
}