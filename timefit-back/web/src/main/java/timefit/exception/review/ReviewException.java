package timefit.exception.review;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import timefit.exception.BaseException;

/**
 * 리뷰(Review) 관련 예외
 */
@Getter
public class ReviewException extends BaseException {

    private final ReviewErrorCode errorCode;

    public ReviewException(ReviewErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ReviewException(ReviewErrorCode errorCode, String customMessage) {
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