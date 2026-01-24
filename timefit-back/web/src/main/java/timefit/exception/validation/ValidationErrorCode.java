package timefit.exception.validation;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ValidationErrorCode {
    // 검증 관련
    INVALID_INPUT("입력값이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    REQUIRED_FIELD_MISSING("필수 항목이 누락되었습니다", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("유효한 이메일 주소를 입력해주세요", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORMAT("올바른 전화번호 형식이 아닙니다", HttpStatus.BAD_REQUEST),
    INVALID_BUSINESS_NUMBER("사업자번호 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK("비밀번호 강도가 부족합니다", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT("날짜 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_TIME_FORMAT("시간 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    ValidationErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}