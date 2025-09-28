package timefit.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.AuthService;
import timefit.common.ResponseData;
import timefit.config.JwtConfig;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 사용자 회원가입
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<AuthResponseDto.UserSignUp> signup(
            @Valid @RequestBody AuthRequestDto.UserSignUp request,
            HttpServletResponse response) {

        log.info("회원가입 요청: email={}", request.getEmail());

        ResponseData<AuthResponseDto.UserSignUp> responseData = authService.signup(request);

        // JWT 토큰을 Authorization 헤더에 설정
        String accessToken = responseData.getData().getAccessToken();
        response.setHeader(JwtConfig.AUTHORIZATION_HEADER, JwtConfig.TOKEN_PREFIX + accessToken);

        log.info("회원가입 완료: userId={}", responseData.getData().getUserId());

        return responseData;
    }

    /**
     * 사용자 로그인
     */
    @PostMapping("/signin")
    public ResponseData<AuthResponseDto.UserSignIn> signin(
            @Valid @RequestBody AuthRequestDto.UserSignIn request,
            HttpServletResponse response) {

        log.info("로그인 요청: email={}", request.getEmail());

        ResponseData<AuthResponseDto.UserSignIn> responseData = authService.signin(request);

        // JWT 토큰을 Authorization 헤더에 설정
        String accessToken = responseData.getData().getAccessToken();
        response.setHeader(JwtConfig.AUTHORIZATION_HEADER, JwtConfig.TOKEN_PREFIX + accessToken);

        log.info("로그인 완료: userId={}", responseData.getData().getUserId());

        return responseData;
    }

    /**
     * 고객 OAuth 로그인
     */
    @PostMapping("/oauth")
    public ResponseData<AuthResponseDto.CustomerOAuth> customerOAuthLogin(
            @Valid @RequestBody AuthRequestDto.CustomerOAuth request,
            HttpServletResponse response) {

        log.info("고객 OAuth 로그인 요청: provider={}, oauthId={}",
                request.getProvider(), request.getOauthId());

        ResponseData<AuthResponseDto.CustomerOAuth> responseData = authService.customerOAuthLogin(request);

        // JWT 토큰을 Authorization 헤더에 설정
        String accessToken = responseData.getData().getAccessToken();
        response.setHeader(JwtConfig.AUTHORIZATION_HEADER, JwtConfig.TOKEN_PREFIX + accessToken);

        log.info("고객 OAuth 로그인 완료: userId={}, isFirstLogin={}",
                responseData.getData().getUserId(),
                responseData.getData().getIsFirstLogin());

        return responseData;
    }

    // JWT 토큰 갱신
    @PostMapping("/refresh")
    public ResponseData<AuthResponseDto.TokenRefresh> refreshToken(
            @Valid @RequestBody AuthRequestDto.TokenRefresh request) {

        log.info("토큰 갱신 요청");

        ResponseData<AuthResponseDto.TokenRefresh> responseData = authService.refreshToken(request);

        log.info("토큰 갱신 완료");

        return responseData;
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseData<Void> logout(
            @RequestHeader(value = JwtConfig.AUTHORIZATION_HEADER, required = false) String authorizationHeader) {

        log.info("로그아웃 요청");

        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith(JwtConfig.TOKEN_PREFIX)) {
            token = authorizationHeader.substring(JwtConfig.TOKEN_PREFIX_LENGTH);
        }

        if (token != null) {
            AuthRequestDto.Logout logoutRequest = AuthRequestDto.Logout.of(token);
            authService.logout(logoutRequest);
        }

        log.info("로그아웃 완료");
        return ResponseData.of(null);
    }

    /**
     * 헬스 체크 (인증 불필요)
     */
    @GetMapping("/health")
    public ResponseData<String> healthCheck() {
        return ResponseData.of("OK");
    }

    /**
     * 토큰 상태 확인 (개발/테스트용)
     */
    @GetMapping("/status-test")
    public ResponseData<String> tokenStatus(
            @RequestHeader(value = JwtConfig.AUTHORIZATION_HEADER, required = false) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtConfig.TOKEN_PREFIX)) {
            return ResponseData.of("토큰 없음");
        }

        String token = authorizationHeader.substring(JwtConfig.TOKEN_PREFIX_LENGTH);
        return ResponseData.of("JWT 토큰 있음: " + token.substring(0, Math.min(20, token.length())) + "...");
    }
}