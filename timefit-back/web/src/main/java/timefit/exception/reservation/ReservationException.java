package timefit.exception.reservation;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import timefit.exception.BaseException;

@Getter
public class ReservationException extends BaseException {

    private final ReservationErrorCode errorCode;

    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ReservationException(ReservationErrorCode errorCode, String message) {
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