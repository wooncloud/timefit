package timefit.exception.businesscategory;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import timefit.exception.BaseException;

@Getter
public class BusinessCategoryException extends BaseException {

    private final BusinessCategoryErrorCode errorCode;

    public BusinessCategoryException(BusinessCategoryErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessCategoryException(BusinessCategoryErrorCode errorCode, String customMessage) {
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