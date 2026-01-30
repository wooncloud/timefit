package timefit.exception.wishlist;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 찜(Wishlist) 관련 에러 코드
 */
@Getter
public enum WishlistErrorCode {

    // 찜 조회 관련
    WISHLIST_NOT_FOUND("찜을 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    // 찜 생성 관련
    WISHLIST_ALREADY_EXISTS("이미 찜한 메뉴입니다", HttpStatus.CONFLICT),
    MENU_NOT_ACTIVE("비활성화된 메뉴는 찜할 수 없습니다", HttpStatus.BAD_REQUEST),

    // 권한 관련
    WISHLIST_ACCESS_DENIED("해당 찜에 대한 권한이 없습니다", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus httpStatus;

    WishlistErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}