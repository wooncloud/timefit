package timefit.exception.customer;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomerErrorCode {
    // 고객 관리 관련
    CUSTOMER_NOT_FOUND("고객 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    CUSTOMER_NOT_BUSINESS_CUSTOMER("해당 업체의 고객이 아닙니다", HttpStatus.NOT_FOUND),
    CUSTOMER_NOTE_TOO_LONG("고객 메모가 너무 깁니다 (최대 500자)", HttpStatus.BAD_REQUEST),
    CUSTOMER_TAG_LIMIT_EXCEEDED("고객 태그 개수 제한을 초과했습니다 (최대 10개)", HttpStatus.BAD_REQUEST),
    CUSTOMER_TAG_TOO_LONG("태그명이 너무 깁니다 (최대 20자)", HttpStatus.BAD_REQUEST),
    PRIVATE_NOTE_ACCESS_DENIED("비공개 메모에 접근할 권한이 없습니다", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus httpStatus;

    CustomerErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}