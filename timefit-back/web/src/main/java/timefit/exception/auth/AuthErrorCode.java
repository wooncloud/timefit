package timefit.exception.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode {
    // 인증 관련
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다", HttpStatus.CONFLICT),
    BUSINESS_NUMBER_ALREADY_EXISTS("이미 등록된 사업자번호입니다", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED("토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    OAUTH_PROVIDER_ERROR("OAuth 인증 중 오류가 발생했습니다", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    AuthErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}