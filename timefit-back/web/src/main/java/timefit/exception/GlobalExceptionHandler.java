package timefit.exception;

import lombok.extern.slf4j.Slf4j;
import timefit.common.ErrorResponse;
import timefit.common.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 도메인 Exception
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseData<Void>> handleBaseException(BaseException e) {
        log.error("BaseException: code={}, message={}", e.getErrorCode(), e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage());

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }
    // 유효성 검사 실패 Exception (ex. @Valid, @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());

        FieldError fieldError = e.getBindingResult().getFieldError();
        String field = fieldError != null ? fieldError.getField() : "unknown";
        String message = fieldError != null ? fieldError.getDefaultMessage() : "입력값이 올바르지 않습니다";

        ErrorResponse errorResponse = ErrorResponse.of("VALIDATION_ERROR", message, field);

        return ResponseEntity
                .badRequest()
                .body(ResponseData.error(errorResponse));
    }
    // Unexpect exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Void>> handleGeneralException(Exception e) {
        log.error("Unexpected error: ", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다"
        );

        return ResponseEntity.internalServerError()
                .body(ResponseData.error(errorResponse));
    }
}