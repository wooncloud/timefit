package org.example.timefitback.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessErrorCode errorCode;

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(BusinessErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public enum BusinessErrorCode {
        // 인증 관련
        EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다"),
        BUSINESS_NUMBER_ALREADY_EXISTS("이미 등록된 사업자번호입니다"),
        INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다"),
        USER_NOT_FOUND("사용자를 찾을 수 없습니다"),

        // 비즈니스 관련
        BUSINESS_NOT_FOUND("업체 정보를 찾을 수 없습니다"),
        BUSINESS_ALREADY_EXISTS("이미 등록된 업체가 있습니다"),

        // 일반적인 오류
        INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다");

        private final String message;

        BusinessErrorCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}