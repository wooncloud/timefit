package timefit.exception.business;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCode {
    // 업체 관리 관련
    BUSINESS_NOT_FOUND("업체 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    BUSINESS_ALREADY_EXISTS("이미 등록된 업체가 있습니다", HttpStatus.CONFLICT),
    BUSINESS_INFO_INCOMPLETE("업체 기본 정보가 완성되지 않았습니다", HttpStatus.BAD_REQUEST),
    BUSINESS_HOURS_INVALID("영업시간 설정이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_CONFLICT("예약 가능 시간이 중복됩니다", HttpStatus.CONFLICT),
    INSUFFICIENT_PERMISSION("해당 작업을 수행할 권한이 없습니다", HttpStatus.FORBIDDEN),
    USER_NOT_BUSINESS_MEMBER("해당 업체에 소속된 구성원이 아닙니다", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus httpStatus;

    BusinessErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}