package timefit.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증/인가", description = "사용자 회원가입, 로그인, 토큰 관리 API")
public class AuthController {

    private final AuthFacadeService authFacadeService;

    // 사용자 회원가입
    @Operation(summary = "일반 사용자 회원가입", description = "이메일, 비밀번호 등을 사용하여 신규 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공 및 토큰 발급",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.UserSignUp.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패 (필수 필드 누락 등)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "409", description = "비즈니스 로직 오류: 이미 존재하는 이메일입니다 (EMAIL_ALREADY_EXISTS)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseData<AuthResponseDto.UserSignUp>> signup(
            @Valid @RequestBody AuthRequestDto.UserSignUp request,
            HttpServletRequest httpRequest) {

        log.info("회원가입 요청: email={}", request.email());

        AuthResponseDto.UserSignUp result = authFacadeService.signup(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.accessToken());

        log.info("회원가입 완료: userId={}", result.userId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.of(result));
    }

    // 사용자 로그인
    @Operation(summary = "일반 사용자 로그인", description = "이메일과 비밀번호를 사용하여 로그인하고 액세스/리프레시 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.UserSignIn.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패: 이메일 또는 비밀번호가 올바르지 않습니다 (INVALID_CREDENTIALS)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다 (USER_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PostMapping("/signin")
    public ResponseEntity<ResponseData<AuthResponseDto.UserSignIn>> signin(
            @Valid @RequestBody AuthRequestDto.UserSignIn request,
            HttpServletRequest httpRequest) {

        log.info("로그인 요청: email={}", request.email());

        AuthResponseDto.UserSignIn result = authFacadeService.signin(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.accessToken());

        log.info("로그인 완료: userId={}", result.userId());

        return ResponseEntity.ok(ResponseData.of(result));
    }

    // 고객 OAuth 로그인
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

    // 로그아웃
    @Operation(summary = "로그아웃", description = "Refresh Token 또는 Access Token을 서버에서 무효화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 (TOKEN_INVALID) 등",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<Void>> logout(
            @Valid @RequestBody AuthRequestDto.Logout request) {

        log.info("로그아웃 요청");

        authFacadeService.logout(request);

        log.info("로그아웃 완료");

        return ResponseEntity.ok(ResponseData.of(null));
    }

    // JWT 토큰 갱신
    @Operation(summary = "JWT 토큰 갱신", description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공 및 새로운 토큰 발급",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.TokenRefresh.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패 (리프레시 토큰 누락)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패: 유효하지 않거나 만료된 Refresh Token (TOKEN_INVALID)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<ResponseData<AuthResponseDto.TokenRefresh>> refreshToken(
            @Valid @RequestBody AuthRequestDto.TokenRefresh request,
            HttpServletRequest httpRequest) {

        log.info("토큰 갱신 요청");

        AuthResponseDto.TokenRefresh result = authFacadeService.refreshToken(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.accessToken());

        log.info("토큰 갱신 완료");

        return ResponseEntity.ok(ResponseData.of(result));
    }
}