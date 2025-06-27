package timefit.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;

/**
 * Auth 관련 요청 DTO들
 */
public class AuthRequestDto {

    /**
     * 업체 회원가입 요청
     */
    @Getter
    public static class BusinessSignUp {

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "유효한 이메일 주소를 입력해주세요")
        private final String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "비밀번호는 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다")
        private final String password;

        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
        private final String name;

        @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$",
                message = "휴대폰 번호 형식이 올바르지 않습니다 (예: 010-1234-5678)")
        private final String phoneNumber;

        @NotBlank(message = "상호명은 필수입니다")
        @Size(min = 2, max = 100, message = "상호명은 2자 이상 100자 이하로 입력해주세요")
        private final String businessName;

        @Size(max = 50, message = "업종은 50자 이하로 입력해주세요")
        private final String businessType;

        @NotBlank(message = "사업자번호는 필수입니다")
        @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$",
                message = "사업자번호 형식이 올바르지 않습니다 (예: 123-45-67890)")
        private final String businessNumber;

        @Size(max = 200, message = "주소는 200자 이하로 입력해주세요")
        private final String address;

        @Pattern(regexp = "^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$",
                message = "연락처 형식이 올바르지 않습니다 (예: 02-1234-5678)")
        private final String contactPhone;

        @Size(max = 1000, message = "설명은 1000자 이하로 입력해주세요")
        private final String description;

        @JsonCreator
        private BusinessSignUp(
                @JsonProperty("email") String email,
                @JsonProperty("password") String password,
                @JsonProperty("name") String name,
                @JsonProperty("phoneNumber") String phoneNumber,
                @JsonProperty("businessName") String businessName,
                @JsonProperty("businessType") String businessType,
                @JsonProperty("businessNumber") String businessNumber,
                @JsonProperty("address") String address,
                @JsonProperty("contactPhone") String contactPhone,
                @JsonProperty("description") String description) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.businessName = businessName;
            this.businessType = businessType;
            this.businessNumber = businessNumber;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
        }

        public static BusinessSignUp of(String email, String password, String name, String phoneNumber,
                                        String businessName, String businessType, String businessNumber,
                                        String address, String contactPhone, String description) {
            return new BusinessSignUp(email, password, name, phoneNumber, businessName,
                    businessType, businessNumber, address, contactPhone, description);
        }
    }

    /**
     * 업체 로그인 요청
     */
    @Getter
    public static class BusinessSignIn {

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "유효한 이메일 주소를 입력해주세요")
        private final String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        private final String password;

        @JsonCreator
        private BusinessSignIn(
                @JsonProperty("email") String email,
                @JsonProperty("password") String password) {
            this.email = email;
            this.password = password;
        }

        public static BusinessSignIn of(String email, String password) {
            return new BusinessSignIn(email, password);
        }
    }

    /**
     * 고객 OAuth 로그인 요청
     */
    @Getter
    public static class CustomerOAuth {

        @NotBlank(message = "OAuth 제공자는 필수입니다")
        @Pattern(regexp = "^(GOOGLE|KAKAO)$", message = "지원하지 않는 OAuth 제공자입니다")
        private final String provider;

        @NotBlank(message = "액세스 토큰은 필수입니다")
        private final String accessToken;

        @NotBlank(message = "OAuth ID는 필수입니다")
        private final String oauthId;

        @JsonCreator
        private CustomerOAuth(
                @JsonProperty("provider") String provider,
                @JsonProperty("accessToken") String accessToken,
                @JsonProperty("oauthId") String oauthId) {
            this.provider = provider;
            this.accessToken = accessToken;
            this.oauthId = oauthId;
        }

        public static CustomerOAuth of(String provider, String accessToken, String oauthId) {
            return new CustomerOAuth(provider, accessToken, oauthId);
        }
    }

    /**
     * 로그아웃 요청
     */
    @Getter
    public static class Logout {

        @NotBlank(message = "임시 토큰은 필수입니다")
        private final String temporaryToken;

        @JsonCreator
        private Logout(@JsonProperty("temporaryToken") String temporaryToken) {
            this.temporaryToken = temporaryToken;
        }

        public static Logout of(String temporaryToken) {
            return new Logout(temporaryToken);
        }
    }

    /**
     * OAuth 사용자 정보 (내부 사용)
     */
    @Getter
    public static class OAuthUserInfo {
        private final String email;
        private final String name;
        private final String profileImageUrl;

        private OAuthUserInfo(String email, String name, String profileImageUrl) {
            this.email = email;
            this.name = name;
            this.profileImageUrl = profileImageUrl;
        }

        public static OAuthUserInfo of(String email, String name, String profileImageUrl) {
            return new OAuthUserInfo(email, name, profileImageUrl);
        }
    }
}