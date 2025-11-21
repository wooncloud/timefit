package timefit.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequestDto {

    // 일반 회원가입 요청
    public record UserSignUp(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "이메일 형식이 올바르지 않습니다")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다")
            @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
            String password,

            @NotBlank(message = "이름은 필수입니다")
            String name,

            String phoneNumber
    ) {}

    // 일반 로그인 요청
    public record UserSignIn(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "이메일 형식이 올바르지 않습니다")
            String email,

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
    public record Logout(
            String currentToken
    ) {}

    // 토큰 갱신 요청
    public record TokenRefresh(
            @NotBlank(message = "리프레시 토큰은 필수입니다")
            String refreshToken
    ) {}
}