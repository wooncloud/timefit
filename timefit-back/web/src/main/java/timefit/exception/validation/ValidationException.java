package timefit.exception.validation;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import timefit.exception.BaseException;

@Getter
public class ValidationException extends BaseException {

    private final ValidationErrorCode errorCode;

    public ValidationException(ValidationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ValidationException(ValidationErrorCode errorCode, String message) {
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