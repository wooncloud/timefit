package timefit.exception.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// AuthErrorCode enum에 추가해야 할 코드들

@Getter
public enum AuthErrorCode {
    // 회원가입/로그인 관련
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다", HttpStatus.CONFLICT),
    BUSINESS_NUMBER_ALREADY_EXISTS("이미 등록된 사업자번호입니다", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    // 토큰 관련 (기존)
    TOKEN_EXPIRED("토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),

    // 토큰 관련 (새로 추가)
    TOKEN_NOT_PROVIDED("토큰이 제공되지 않았습니다", HttpStatus.UNAUTHORIZED),

    // 권한 관련
    ACCESS_DENIED("접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    NO_BUSINESS_ASSOCIATION("업체 정보가 없습니다", HttpStatus.FORBIDDEN),

    // OAuth 관련
    OAUTH_PROVIDER_ERROR("OAuth 인증 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_OAUTH_PROVIDER("지원하지 않는 OAuth 제공자입니다", HttpStatus.BAD_REQUEST),

    // 일반 인증 관련
    AUTHENTICATION_FAILED("인증에 실패했습니다", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus httpStatus;

    AuthErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}