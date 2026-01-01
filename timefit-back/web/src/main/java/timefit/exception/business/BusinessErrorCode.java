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
    BUSINESS_NOT_ACTIVE("비활성화된 업체입니다", HttpStatus.BAD_REQUEST),
    BUSINESS_CLOSED("해당 날짜는 영업일이 아닙니다", HttpStatus.BAD_REQUEST),

    // 권한 관련
    INSUFFICIENT_PERMISSION("해당 작업을 수행할 권한이 없습니다", HttpStatus.FORBIDDEN),
    USER_NOT_BUSINESS_MEMBER("해당 업체에 소속된 구성원이 아닙니다", HttpStatus.FORBIDDEN),
    CANNOT_CHANGE_OWN_ROLE("본인의 권한은 변경할 수 없습니다", HttpStatus.BAD_REQUEST),
    CANNOT_CHANGE_TO_OWNER("OWNER 권한으로 변경할 수 없습니다", HttpStatus.BAD_REQUEST),

    // 구성원 관리 관련
    USER_NOT_FOUND("사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    USER_NOT_MEMBER("해당 업체의 구성원이 아닙니다", HttpStatus.NOT_FOUND),
    USER_ALREADY_MEMBER("이미 업체 구성원입니다", HttpStatus.CONFLICT),
    NO_ACTIVE_MEMBERS("활성화된 구성원이 없습니다", HttpStatus.NOT_FOUND),
    CANNOT_REMOVE_SELF("본인을 제거할 수 없습니다", HttpStatus.BAD_REQUEST),
    CANNOT_REMOVE_OWNER("OWNER는 제거할 수 없습니다", HttpStatus.BAD_REQUEST),

    // Page
    INVALID_PAGE_NUMBER("페이지 번호가 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_SIZE("페이지 크기가 올바르지 않습니다", HttpStatus.BAD_REQUEST),

    // 삭제 관련
    DELETE_CONFIRMATION_REQUIRED("삭제 확인이 필요합니다", HttpStatus.BAD_REQUEST),
    BUSINESS_ALREADY_DELETED("이미 삭제된 업체입니다", HttpStatus.BAD_REQUEST),

    // 예약 가능 시간 관련
    OPERATING_HOURS_NOT_FOUND("해당 예약 시간대를 찾을 수 없습니다", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    BusinessErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}