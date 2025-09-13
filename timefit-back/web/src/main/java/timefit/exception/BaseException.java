package timefit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }

    public abstract String getErrorCode();
    public abstract HttpStatus getHttpStatus();
}