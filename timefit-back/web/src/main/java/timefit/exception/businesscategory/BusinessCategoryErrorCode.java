package timefit.exception.businesscategory;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessCategoryErrorCode {

    // 카테고리 조회 관련
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_ACTIVE("비활성화된 카테고리입니다", HttpStatus.BAD_REQUEST),

    // 카테고리 생성 관련
    DUPLICATE_CATEGORY("이미 존재하는 카테고리입니다", HttpStatus.CONFLICT),
    INVALID_CATEGORY_DATA("잘못된 카테고리 정보입니다", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY_NAME("카테고리명은 한글, 영문, 숫자만 사용 가능하며 2~20자여야 합니다",  HttpStatus.BAD_REQUEST),

    // 권한 관련
    CATEGORY_ACCESS_DENIED("해당 카테고리에 대한 권한이 없습니다", HttpStatus.FORBIDDEN),

    // 비즈니스 로직 관련
    CATEGORY_HAS_ACTIVE_MENUS("활성 메뉴가 있어 삭제할 수 없습니다", HttpStatus.CONFLICT),
    INVALID_BUSINESS_TYPE("잘못된 업종 타입입니다", HttpStatus.BAD_REQUEST),
    CATEGORY_CODE_MISMATCH("카테고리 코드가 업종과 일치하지 않습니다", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    BusinessCategoryErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}