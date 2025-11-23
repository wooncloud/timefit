package timefit.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

@Tag(name = "01. 인증/인가", description = "사용자 회원가입, 로그인, 토큰 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacadeService authFacadeService;

    @Operation(
            summary = "일반 사용자 회원가입",
            description = """
                    이메일, 비밀번호를 사용하여 신규 사용자를 등록합니다.
                    
                    1. Request Body 필수값
                       - email: 이메일 (로그인 ID)
                       - password: 비밀번호 (최소 8자)
                       - name: 사용자 이름
                    
                    2. Request Body 선택값
                       - phoneNumber: 연락처
                    
                    3. 제약사항
                       - email: 유효한 이메일 형식
                       - password: 최소 8자 이상
                       - 중복된 이메일로 가입 불가
                    
                    4. 응답
                       - 가입 성공 시 자동으로 로그인되며 Access Token과 Refresh Token 발급
                       - Response Header에 Authorization: Bearer {accessToken} 포함
                    
                    5. 권한
                       - 인증 불필요 (누구나 가입 가능)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공 및 토큰 발급",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.UserSignUp.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "userId": "550e8400-e29b-41d4-a716-446655440000",
                                                "email": "user@example.com",
                                                "name": "홍길동",
                                                "phoneNumber": "010-1234-5678",
                                                "role": "CUSTOMER",
                                                "profileImageUrl": null,
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                "createdAt": "2025-11-23T10:00:00",
                                                "lastLoginAt": "2025-11-23T10:00:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. email 은(는) 필수 값입니다.
                            2. password 은(는) 필수 값입니다.
                            3. name 은(는) 필수 값입니다.
                            4. 이메일 형식이 올바르지 않습니다.
                            5. 비밀번호는 최소 8자 이상이어야 합니다.
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "EMAIL_ALREADY_EXISTS - 이미 존재하는 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseData<AuthResponseDto.UserSignUp>> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthRequestDto.UserSignUp.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "user@example.com",
                                              "password": "a12345678",
                                              "name": "홍길동",
                                              "phoneNumber": "010-1234-5678"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody AuthRequestDto.UserSignUp request,
            @Parameter(hidden = true)
            HttpServletRequest httpRequest) {

        log.info("회원가입 요청: email={}", request.email());

        AuthResponseDto.UserSignUp result = authFacadeService.signup(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.accessToken());

        log.info("회원가입 완료: userId={}", result.userId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.of(result));
    }

    @Operation(
            summary = "일반 사용자 로그인",
            description = """
                    이메일과 비밀번호를 사용하여 로그인하고 액세스/리프레시 토큰을 발급받습니다.
                    
                    1. Request Body 필수값
                       - email: 이메일 (로그인 ID)
                       - password: 비밀번호
                    
                    2. 인증 방식
                       - 이메일과 비밀번호 일치 여부 확인
                       - 성공 시 Access Token과 Refresh Token 발급
                    
                    3. 응답
                       - 사용자 기본 정보
                       - 연결된 업체 목록 (있는 경우)
                       - Response Header에 Authorization: Bearer {accessToken} 포함
                    
                    4. 권한
                       - 인증 불필요 (로그인 API)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 및 토큰 발급",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.UserSignIn.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "userId": "550e8400-e29b-41d4-a716-446655440000",
                                                "email": "user@example.com",
                                                "name": "홍길동",
                                                "phoneNumber": "010-1234-5678",
                                                "role": "BUSINESS",
                                                "profileImageUrl": null,
                                                "businesses": [
                                                  {
                                                    "businessId": "550e8400-e29b-41d4-a716-446655440001",
                                                    "businessName": "강남 헤어샵",
                                                    "businessTypes": ["BD008"],
                                                    "address": "서울시 강남구",
                                                    "contactPhone": "02-1234-5678",
                                                    "userRole": "OWNER",
                                                    "isBusinessActive": true,
                                                    "joinedAt": "2025-11-01T10:00:00"
                                                  }
                                                ],
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                "createdAt": "2025-11-01T10:00:00",
                                                "lastLoginAt": "2025-11-23T10:00:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. email 은(는) 필수 값입니다.
                            2. password 은(는) 필수 값입니다.
                            3. 이메일 형식이 올바르지 않습니다.
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "INVALID_CREDENTIALS - 이메일 또는 비밀번호가 올바르지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "USER_NOT_FOUND - 사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/signin")
    public ResponseEntity<ResponseData<AuthResponseDto.UserSignIn>> signin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthRequestDto.UserSignIn.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "user@example.com",
                                              "password": "a12345678"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody AuthRequestDto.UserSignIn request,
            @Parameter(hidden = true)
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

    @Operation(
            summary = "로그아웃",
            description = """
                    현재 토큰을 서버에서 무효화합니다.
                    
                    1. Request Body 선택값
                       - currentToken: 무효화할 Access Token 또는 Refresh Token
                    
                    2. 처리 과정
                       - 제공된 토큰을 블랙리스트에 추가
                       - 해당 토큰으로는 더 이상 인증 불가
                    
                    3. 권한
                       - 로그인 필요
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "TOKEN_INVALID - 유효하지 않은 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<Void>> logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그아웃 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthRequestDto.Logout.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "currentToken": "eyJhbGciOiJIUzI1NiJ9..."
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody AuthRequestDto.Logout request) {

        log.info("로그아웃 요청");

        authFacadeService.logout(request);

        log.info("로그아웃 완료");

        return ResponseEntity.ok(ResponseData.of(null));
    }

    @Operation(
            summary = "JWT 토큰 갱신",
            description = """
                    Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.
                    
                    1. Request Body 필수값
                        - refreshToken: 만료되지 않은 Refresh Token
                    
                    2. 처리 과정
                        - Refresh Token 유효성 검증
                        - 새로운 Access Token 발급
                        - 새로운 Refresh Token 발급 (Refresh Token Rotation)
                        - 기존 Refresh Token은 무효화
                    
                    3. 응답
                        - accessToken: 새로운 Access Token
                        - refreshToken: 새로운 Refresh Token
                        - tokenType: Bearer
                        - expiresIn: Access Token 만료까지 남은 시간(초)
                    
                    4. 권한
                        - 유효한 Refresh Token 필요
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공 및 새로운 토큰 발급",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.TokenRefresh.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                "tokenType": "Bearer",
                                                "expiresIn": 3600
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. refreshToken 은(는) 필수 값입니다.
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            TOKEN_INVALID - 유효하지 않거나 만료된 Refresh Token
                            
                            TOKEN_EXPIRED - 토큰 만료
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ResponseData<AuthResponseDto.TokenRefresh>> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "토큰 갱신 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthRequestDto.TokenRefresh.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody AuthRequestDto.TokenRefresh request,
            @Parameter(hidden = true)
            HttpServletRequest httpRequest) {

        log.info("토큰 갱신 요청");

        AuthResponseDto.TokenRefresh result = authFacadeService.refreshToken(request);

        // Interceptor가 자동으로 처리하도록 Request Attribute에 토큰 설정
        httpRequest.setAttribute("accessToken", result.accessToken());

        log.info("토큰 갱신 완료");

        return ResponseEntity.ok(ResponseData.of(result));
    }
}