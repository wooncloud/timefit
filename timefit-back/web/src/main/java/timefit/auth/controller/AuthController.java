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

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 임시 헤더 이름
    private static final String TEMP_TOKEN_HEADER = "x-client-token";

    /**
     * 업체 회원가입
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<AuthResponseDto.UserSignUp> businessSignUp(
            @Valid @RequestBody AuthRequestDto.UserSignUp request,
            HttpServletResponse response) {

        log.info("회원가입 요청: email={}", request.getEmail());

        ResponseData<AuthResponseDto.UserSignUp> responseData = authService.signup(request);

        // 응답 헤더에 임시 토큰 추가
        String temporaryToken = responseData.getData().getTemporaryToken();
        response.setHeader(TEMP_TOKEN_HEADER, temporaryToken);

        log.info("회원가입 완료: userId={}, token={}",
                responseData.getData().getUserId(), temporaryToken);

        return responseData;
    }

    /**
     * 업체 로그인
     */
    @PostMapping("/signin")
    public ResponseData<AuthResponseDto.UserSignIn> businessSignIn(
            @Valid @RequestBody AuthRequestDto.UserSignIn request,
            HttpServletResponse response) {

        log.info("로그인 요청: email={}", request.getEmail());

        ResponseData<AuthResponseDto.UserSignIn> responseData = authService.signin(request);

        // 응답 헤더에 임시 토큰 추가
        String temporaryToken = responseData.getData().getTemporaryToken();
        response.setHeader(TEMP_TOKEN_HEADER, temporaryToken);

        log.info("로그인 완료: userId={}, token={}",
                responseData.getData().getUserId(), temporaryToken);

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

        // 응답 헤더에 임시 토큰 추가
        String temporaryToken = responseData.getData().getTemporaryToken();
        response.setHeader(TEMP_TOKEN_HEADER, temporaryToken);

        log.info("고객 OAuth 로그인 완료: userId={}, isFirstLogin={}, token={}",
                responseData.getData().getUserId(),
                responseData.getData().getIsFirstLogin(),
                temporaryToken);

        return responseData;
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseData<Void> logout(
            @RequestHeader(value = TEMP_TOKEN_HEADER, required = false) String token) {

        log.info("로그아웃 요청: token={}", token != null ? "있음" : "없음");

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
            @RequestHeader(value = TEMP_TOKEN_HEADER, required = false) String token) {

        if (token == null) {
            return ResponseData.of("토큰 없음");
        }

        // 임시 구현 - 실제로는 AuthTokenService에서 검증
        return ResponseData.of("토큰 있음: " + token.substring(0, Math.min(10, token.length())) + "...");
    }
}