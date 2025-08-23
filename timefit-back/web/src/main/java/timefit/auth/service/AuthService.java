package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import timefit.auth.factory.AuthResponseFactory;
import timefit.common.ResponseData;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;

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
     * 유저 회원가입
     */
    @Transactional
    public ResponseData<AuthResponseDto.UserSignUp> signup(AuthRequestDto.UserSignUp request) {
        log.info("회원가입 시작: email={}", request.getEmail());

        // 1. 순수 사용자 등록 (User만 생성, 업체 정보 없음)
        UserRegistrationResult registrationResult = userRegistrationService.registerUser(request);

        // 2. 토큰 생성
        String token = authTokenService.generateToken(registrationResult.getUser().getId());

        // 3. 응답 생성
        AuthResponseDto.UserSignUp response = authResponseFactory.createBusinessSignUpResponse(
                registrationResult, token);

        log.info("회원가입 완료: userId={}", registrationResult.getUser().getId());
        return ResponseData.of(response);
    }

    /**
     * 유저 로그인
     */
    @Transactional
    public ResponseData<AuthResponseDto.UserSignIn> signin(AuthRequestDto.UserSignIn request) {
        log.info("로그인 시작: email={}", request.getEmail());

        // 1. 로그인 처리
        UserLoginResult loginResult = userLoginService.loginUser(request);

        // 2. 토큰 생성
        String token = authTokenService.generateToken(loginResult.getUser().getId());

        // 3. 응답 생성
        AuthResponseDto.UserSignIn response = authResponseFactory.createBusinessSignInResponse(
                loginResult, token);

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

        // 2. 토큰 생성
        String token = authTokenService.generateToken(loginResult.getUser().getId());

        // 3. 응답 생성
        AuthResponseDto.CustomerOAuth response = authResponseFactory.createCustomerOAuthResponse(
                loginResult, token);

        log.info("고객 OAuth 로그인 완료: userId={}", loginResult.getUser().getId());
        return ResponseData.of(response);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public ResponseData<Void> logout(AuthRequestDto.Logout request) {
        log.info("로그아웃 시작");

        // 토큰 무효화
        authTokenService.invalidateToken(request.getTemporaryToken());

        log.info("로그아웃 완료");
        return ResponseData.of(null);
    }
}