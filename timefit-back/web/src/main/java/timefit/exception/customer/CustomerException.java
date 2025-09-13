package timefit.exception.customer;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import timefit.exception.BaseException;

@Getter
public class CustomerException extends BaseException {

    private final CustomerErrorCode errorCode;

    public CustomerException(CustomerErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomerException(CustomerErrorCode errorCode, String message) {
        super(message);
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