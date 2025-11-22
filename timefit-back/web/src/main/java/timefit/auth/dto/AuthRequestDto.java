package timefit.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequestDto {

    // 일반 회원가입 요청
    @Schema(description = "일반 사용자 회원가입 요청 데이터")
    public record UserSignUp(
            @Schema(description = "사용자 이메일 (로그인 ID)", example = "user@example.com")
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "이메일 형식이 올바르지 않습니다")
            String email,

            @Schema(description = "사용자 비밀번호", example = "a12345678", minLength = 8)
            @NotBlank(message = "비밀번호는 필수입니다")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
            String password,

            @Schema(description = "사용자 이름", example = "홍길동")
            @NotBlank(message = "이름은 필수입니다")
            String name,

            @Schema(description = "사용자 연락처 (선택 사항)", example = "010-1234-5678", nullable = true)
            String phoneNumber
    ) {}

    // 일반 로그인 요청
    @Schema(description = "일반 사용자 로그인 요청 데이터")
    public record UserSignIn(
            @Schema(description = "사용자 이메일", example = "user@example.com")
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "이메일 형식이 올바르지 않습니다")
            String email,

            @Schema(description = "사용자 비밀번호", example = "a12345678")
            @NotBlank(message = "비밀번호는 필수입니다")
            String password
    ) {}

    // OAuth 로그인 요청
    public record CustomerOAuth(
            @NotBlank(message = "OAuth 제공자는 필수입니다")
            String provider,

            @NotBlank(message = "액세스 토큰은 필수입니다")
            String accessToken,

            @NotBlank(message = "OAuth ID는 필수입니다")
            String oauthId
    ) {}

    // 로그아웃 요청
    @Schema(description = "로그아웃 요청 데이터")
    public record Logout(
            @Schema(description = "무효화할 Access Token 또는 Refresh Token", example = "eyJhbGciOiJIUzI1NiI...")
            String currentToken
    ) {}

    // 토큰 갱신 요청
    @Schema(description = "토큰 갱신 요청 데이터")
    public record TokenRefresh(
            @Schema(description = "만료되지 않은 Refresh Token", example = "eyJhbGciOiJIUzI1NiI...")
            @NotBlank(message = "리프레시 토큰은 필수입니다")
            String refreshToken
    ) {}
}