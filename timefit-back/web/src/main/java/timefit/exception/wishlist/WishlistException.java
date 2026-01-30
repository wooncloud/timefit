package timefit.exception.wishlist;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import timefit.exception.BaseException;

/**
 * 찜(Wishlist) 관련 예외
 */
@Getter
public class WishlistException extends BaseException {

    private final WishlistErrorCode errorCode;

    public WishlistException(WishlistErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public WishlistException(WishlistErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorCode() {
        return errorCode.name();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}