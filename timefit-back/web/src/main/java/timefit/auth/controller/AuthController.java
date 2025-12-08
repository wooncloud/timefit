package timefit.auth.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.AuthFacadeService;
import timefit.common.ResponseData;
import timefit.common.swagger.operation.auth.*;
import timefit.common.swagger.requestbody.auth.*;

@Tag(name = "01. 인증/인가", description = "사용자 회원가입, 로그인, 토큰 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacadeService authFacadeService;

    /**
     * 회원가입
     */
    @SignupOperation
    @PostMapping("/signup")
    public ResponseEntity<ResponseData<AuthResponseDto.UserSignUp>> signup(
            @SignupRequestBody @Valid @RequestBody AuthRequestDto.UserSignUp request,
            @Parameter(hidden = true) HttpServletRequest httpRequest) {

        log.info("회원가입 요청: email={}", request.email());

        AuthResponseDto.UserSignUp result = authFacadeService.signup(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.accessToken());

        log.info("회원가입 완료: userId={}", result.userId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.of(result));
    }

    /**
     * 로그인
     */
    @SigninOperation
    @PostMapping("/signin")
    public ResponseEntity<ResponseData<AuthResponseDto.UserSignIn>> signin(
            @SigninRequestBody @Valid @RequestBody AuthRequestDto.UserSignIn request,
            @Parameter(hidden = true) HttpServletRequest httpRequest) {

        log.info("로그인 요청: email={}", request.email());

        AuthResponseDto.UserSignIn result = authFacadeService.signin(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.accessToken());

        log.info("로그인 완료: userId={}", result.userId());

        return ResponseEntity.ok(ResponseData.of(result));
    }

    /**
     * OAuth 로그인 (고객용)
     */
    @PostMapping("/oauth")
    public ResponseEntity<ResponseData<AuthResponseDto.CustomerOAuth>> customerOAuthLogin(
            @Valid @RequestBody AuthRequestDto.CustomerOAuth request,
            HttpServletRequest httpRequest) {

        log.info("고객 OAuth 로그인 요청: provider={}, oauthId={}",
                request.provider(), request.oauthId());

        AuthResponseDto.CustomerOAuth result = authFacadeService.customerOAuthLogin(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.accessToken());

        log.info("고객 OAuth 로그인 완료: userId={}", result.userId());

        return ResponseEntity.ok(ResponseData.of(result));
    }

    /**
     * 로그아웃
     */
    @LogoutOperation
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<Void>> logout(
            @LogoutRequestBody @Valid @RequestBody AuthRequestDto.Logout request) {

        log.info("로그아웃 요청");

        authFacadeService.logout(request);

        log.info("로그아웃 완료");

        return ResponseEntity.ok(ResponseData.of(null));
    }

    /**
     * 토큰 갱신
     */
    @RefreshTokenOperation
    @PostMapping("/refresh")
    public ResponseEntity<ResponseData<AuthResponseDto.TokenRefresh>> refreshToken(
            @RefreshTokenRequestBody @Valid @RequestBody AuthRequestDto.TokenRefresh request,
            @Parameter(hidden = true) HttpServletRequest httpRequest) {

        log.info("토큰 갱신 요청");

        AuthResponseDto.TokenRefresh result = authFacadeService.refreshToken(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.accessToken());

        log.info("토큰 갱신 완료");

        return ResponseEntity.ok(ResponseData.of(result));
    }
}