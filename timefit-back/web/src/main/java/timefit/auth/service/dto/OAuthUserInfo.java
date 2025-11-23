package timefit.auth.service.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * OAuth Provider 로부터 받은 사용자 정보 (Internal DTO)
 * Service 계층에서 OAuth Provider API 응답을 파싱한 결과
 */
public record OAuthUserInfo(
        @NotBlank(message = "이메일은 필수입니다")
        String email,

        @NotBlank(message = "이름은 필수입니다")
        String name,

        String profileImageUrl
) {
    public static OAuthUserInfo of(String email, String name, String profileImageUrl) {
        return new OAuthUserInfo(email, name, profileImageUrl);
    }
}