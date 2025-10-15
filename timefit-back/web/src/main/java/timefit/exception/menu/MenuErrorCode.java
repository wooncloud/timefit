package timefit.exception.menu;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MenuErrorCode {

    // 메뉴 조회 관련
    MENU_NOT_FOUND("메뉴를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    MENU_NOT_ACTIVE("비활성화된 메뉴입니다", HttpStatus.BAD_REQUEST),

    // 메뉴 생성 관련
    MENU_ALREADY_EXISTS("이미 존재하는 메뉴명입니다", HttpStatus.CONFLICT),
    INVALID_MENU_DATA("잘못된 메뉴 정보입니다", HttpStatus.BAD_REQUEST),

    // 권한 관련
    MENU_ACCESS_DENIED("해당 메뉴에 대한 권한이 없습니다", HttpStatus.FORBIDDEN),

    // 비즈니스 로직 관련
    MENU_HAS_ACTIVE_SLOTS("예약 가능한 슬롯이 있어 삭제할 수 없습니다", HttpStatus.CONFLICT),
    INVALID_SLOT_TYPE("잘못된 서비스 타입입니다", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    MenuErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}