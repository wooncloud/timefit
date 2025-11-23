package timefit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import timefit.common.ErrorResponse;
import timefit.common.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import timefit.exception.system.SystemErrorCode;

@Slf4j
@RestControllerAdvice(basePackages = "timefit")
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

    //    ----------------------- DB

    // DB 제약 조건 위반 (중복 키, 외래키 위반 등)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseData<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Database integrity violation: {}", e.getMessage());

        // 중복 키 에러 감지
        String message = "데이터베이스 제약 조건 위반입니다";
        if (e.getMessage() != null) {
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("Duplicate entry")) {
                message = "이미 존재하는 데이터입니다";
            } else if (e.getMessage().contains("foreign key constraint")) {
                message = "참조 무결성 제약 조건 위반입니다";
            }
        }

        ErrorResponse errorResponse = ErrorResponse.of("DATABASE_CONSTRAINT_VIOLATION", message);
        return ResponseEntity
                .status(409) // CONFLICT
                .body(ResponseData.error(errorResponse));
    }

    // DB 타임아웃
    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<ResponseData<Void>> handleQueryTimeout(QueryTimeoutException e) {
        log.error("Database query timeout: {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(
                SystemErrorCode.DATABASE_TIMEOUT_ERROR.name(),
                SystemErrorCode.DATABASE_TIMEOUT_ERROR.getMessage()
        );
        return ResponseEntity
                .status(500)
                .body(ResponseData.error(errorResponse));
    }

    // 일반 DB 접근 오류
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ResponseData<Void>> handleDataAccessException(DataAccessException e) {
        log.error("Database access error: {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(
                SystemErrorCode.DATABASE_QUERY_ERROR.name(),
                SystemErrorCode.DATABASE_QUERY_ERROR.getMessage()
        );
        return ResponseEntity
                .status(500)
                .body(ResponseData.error(errorResponse));
    }
}