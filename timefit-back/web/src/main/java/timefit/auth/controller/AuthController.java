package timefit.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.AuthService;
import timefit.common.ResponseData;
import timefit.config.JwtConfig;

/**
 * Auth Controller
 * - 인증 관련 API 엔드포인트
 */
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
    public ResponseEntity<ResponseData<AuthResponseDto.UserSignUp>> signup(
            @Valid @RequestBody AuthRequestDto.UserSignUp request,
            HttpServletRequest httpRequest) {

        log.info("회원가입 요청: email={}", request.getEmail());

        AuthResponseDto.UserSignUp result = authService.signup(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.getAccessToken());

        log.info("회원가입 완료: userId={}", result.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.of(result));
    }

    /**
     * 사용자 로그인
     */
    @PostMapping("/signin")
    public ResponseEntity<ResponseData<AuthResponseDto.UserSignIn>> signin(
            @Valid @RequestBody AuthRequestDto.UserSignIn request,
            HttpServletRequest httpRequest) {

        log.info("로그인 요청: email={}", request.getEmail());

        AuthResponseDto.UserSignIn result = authService.signin(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.getAccessToken());

        log.info("로그인 완료: userId={}", result.getUserId());

        return ResponseEntity.ok(ResponseData.of(result));
    }

    /**
     * 고객 OAuth 로그인
     */
    @PostMapping("/oauth")
    public ResponseEntity<ResponseData<AuthResponseDto.CustomerOAuth>> customerOAuthLogin(
            @Valid @RequestBody AuthRequestDto.CustomerOAuth request,
            HttpServletRequest httpRequest) {

        log.info("고객 OAuth 로그인 요청: provider={}, oauthId={}",
                request.getProvider(), request.getOauthId());

        AuthResponseDto.CustomerOAuth result = authService.customerOAuthLogin(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.getAccessToken());

        log.info("고객 OAuth 로그인 완료: userId={}, isFirstLogin={}",
                result.getUserId(), result.getIsFirstLogin());

        return ResponseEntity.ok(ResponseData.of(result));
    }

    /**
     * JWT 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseData<AuthResponseDto.TokenRefresh>> refreshToken(
            @Valid @RequestBody AuthRequestDto.TokenRefresh request,
            HttpServletRequest httpRequest) {

        log.info("토큰 갱신 요청");

        AuthResponseDto.TokenRefresh result = authService.refreshToken(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.getAccessToken());

        log.info("토큰 갱신 완료");

        return ResponseEntity.ok(ResponseData.of(result));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<Void>> logout(
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
        return ResponseEntity.ok(ResponseData.of(null));
    }

    /**
     * 헬스 체크 (인증 불필요)
     */
    @GetMapping("/health")
    public ResponseEntity<ResponseData<String>> healthCheck() {
        return ResponseEntity.ok(ResponseData.of("OK"));
    }

    /**
     * 토큰 상태 확인 (개발/테스트용)
     */
    @GetMapping("/status-test")
    public ResponseEntity<ResponseData<String>> tokenStatus(
            @RequestHeader(value = JwtConfig.AUTHORIZATION_HEADER, required = false) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtConfig.TOKEN_PREFIX)) {
            return ResponseEntity.ok(ResponseData.of("토큰 없음"));
        }

        String token = authorizationHeader.substring(JwtConfig.TOKEN_PREFIX_LENGTH);
        return ResponseEntity.ok(ResponseData.of("JWT 토큰 있음: " + token.substring(0, Math.min(20, token.length())) + "..."));
    }
}