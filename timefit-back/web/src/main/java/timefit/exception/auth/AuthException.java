package timefit.exception.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import timefit.exception.BaseException;

@Getter
public class AuthException extends BaseException {

    private final AuthErrorCode errorCode;

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode.getMessage());
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