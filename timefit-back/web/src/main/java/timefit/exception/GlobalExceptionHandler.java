package timefit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import timefit.common.ErrorResponse;
import timefit.common.ResponseData;
import timefit.exception.system.SystemErrorCode;

import java.io.IOException;

/**
 * 전역 예외 처리 핸들러
 *
 * 애플리케이션에서 발생하는 모든 예외를 일관되게 처리하여
 * 클라이언트에게 표준화된 에러 응답을 제공합니다.
 *
 * 처리하는 예외 카테고리:
 * 1. 도메인 예외: BaseException 및 하위 예외들
 * 2. 유효성 검증: MethodArgumentNotValidException
 * 3. 데이터베이스: Connection, Query, Timeout, Constraint 관련
 * 4. 파일 처리: Upload, Storage 관련
 * 5. 외부 API: RestClient 관련
 * 6. 일반 예외: 기타 예상치 못한 예외
 */
@Slf4j
@RestControllerAdvice(basePackages = "timefit")
public class GlobalExceptionHandler {

//    ------------------------- 도메인 예외

    /**
     * 도메인 예외 처리
     *
     * BaseException을 상속받은 모든 도메인별 예외를 처리합니다.
     * 예: BusinessException, MenuException, ReservationException 등
     *
     * @param e 도메인 예외
     * @return 에러 응답 (도메인별 ErrorCode의 HttpStatus 적용)
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseData<Void>> handleBaseException(BaseException e) {
        log.error("[도메인 예외 핸들러] 예외 발생 - 코드: {}, 메시지: {}", e.getErrorCode(), e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage());

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

//    ---------------------- 유효성 검증 예외

    /**
     * 유효성 검증 실패 예외 처리
     *
     * @Valid, @Validated 어노테이션으로 인한 검증 실패를 처리합니다.
     * RequestBody의 필드 검증 오류를 클라이언트에게 명확히 전달합니다.
     *
     * @param e 유효성 검증 실패 예외
     * @return 에러 응답 (400 Bad Request, 실패한 필드 정보 포함)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<Void>> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String field = fieldError != null ? fieldError.getField() : "unknown";
        String message = fieldError != null ? fieldError.getDefaultMessage() : "입력값이 올바르지 않습니다";

        log.error("[유효성 검증 핸들러] 검증 실패 - 필드: {}, 메시지: {}", field, message);

        ErrorResponse errorResponse = ErrorResponse.of("VALIDATION_ERROR", message, field);

        return ResponseEntity
                .badRequest()
                .body(ResponseData.error(errorResponse));
    }

//    --------------------------- 데이터베이스 예외

    /**
     * 데이터베이스 연결 실패 예외 처리
     *
     * DB 서버 다운, 네트워크 문제, 연결 풀 고갈 등으로 인한 연결 실패를 처리합니다.
     *
     * @param e JDBC 연결 실패 예외
     * @return 에러 응답 (500 Internal Server Error)
     */
    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public ResponseEntity<ResponseData<Void>> handleDatabaseConnectionError(CannotGetJdbcConnectionException e) {
        log.error("[DB 연결 핸들러] 데이터베이스 연결 실패 - 원인: {}", e.getMessage(), e);

        SystemErrorCode errorCode = SystemErrorCode.DATABASE_CONNECTION_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.name(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

    /**
     * 데이터베이스 제약 조건 위반 예외 처리
     *
     * 중복 키, 외래키 제약, 기타 DB 제약 조건 위반을 처리합니다.
     * 예외 메시지를 분석하여 적절한 SystemErrorCode를 선택합니다.
     *
     * 처리하는 제약 조건:
     * - 중복 키 (UNIQUE): DATABASE_DUPLICATE_KEY_ERROR
     * - 외래키 제약 (FOREIGN KEY): DATABASE_FOREIGN_KEY_VIOLATION
     * - 기타 제약 (NOT NULL, CHECK 등): DATABASE_CONSTRAINT_VIOLATION
     *
     * @param e 데이터 무결성 위반 예외
     * @return 에러 응답 (409 Conflict)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseData<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("[DB 제약조건 핸들러] 데이터 무결성 위반 - 원인: {}", e.getMessage());

        SystemErrorCode errorCode = determineConstraintErrorCode(e);

        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.name(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

    /**
     * 데이터베이스 쿼리 타임아웃 예외 처리
     *
     * DB 쿼리 실행 시간이 설정된 제한 시간을 초과한 경우 발생합니다.
     * 복잡한 쿼리, 대량 데이터 조회, DB 성능 문제 등이 원인일 수 있습니다.
     *
     * @param e 쿼리 타임아웃 예외
     * @return 에러 응답 (500 Internal Server Error)
     */
    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<ResponseData<Void>> handleQueryTimeout(QueryTimeoutException e) {
        log.error("[DB 타임아웃 핸들러] 쿼리 실행 시간 초과 - 원인: {}", e.getMessage(), e);

        SystemErrorCode errorCode = SystemErrorCode.DATABASE_TIMEOUT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.name(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

    /**
     * 일반 데이터베이스 접근 오류 처리
     *
     * DB 연결 실패, 쿼리 실행 오류 등 일반적인 DB 접근 문제를 처리합니다.
     * DataIntegrityViolation, QueryTimeout, ConnectionError를 제외한 모든 DB 관련 예외가 해당됩니다.
     *
     * @param e 데이터 접근 예외
     * @return 에러 응답 (500 Internal Server Error)
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ResponseData<Void>> handleDataAccessException(DataAccessException e) {
        log.error("[DB 접근 핸들러] 데이터베이스 접근 오류 - 원인: {}", e.getMessage(), e);

        SystemErrorCode errorCode = SystemErrorCode.DATABASE_QUERY_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.name(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

//    ------------------------ 파일 처리 예외

    /**
     * 파일 업로드 크기 초과 예외 처리
     *
     * 업로드 파일의 크기가 설정된 최대 크기를 초과한 경우 발생합니다.
     * application.yml의 spring.servlet.multipart.max-file-size 설정값 확인 필요
     *
     * @param e 파일 크기 초과 예외
     * @return 에러 응답 (400 Bad Request)
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseData<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.error("[파일 업로드 핸들러] 파일 크기 초과 - 원인: {}", e.getMessage());

        SystemErrorCode errorCode = SystemErrorCode.FILE_UPLOAD_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.name(),
                "업로드 파일 크기가 제한을 초과했습니다"
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

    /**
     * multipart 요청 처리 실패 예외 처리
     *
     * 파일 업로드 형식 오류, 잘못된 multipart 요청 등을 처리합니다.
     *
     * @param e multipart 예외
     * @return 에러 응답 (400 Bad Request)
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ResponseData<Void>> handleMultipartException(MultipartException e) {
        log.error("[파일 업로드 핸들러] 파일 업로드 실패 - 원인: {}", e.getMessage());

        SystemErrorCode errorCode = SystemErrorCode.FILE_UPLOAD_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.name(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

    /**
     * 파일 입출력 예외 처리
     *
     * 파일 저장, 읽기, 삭제 등의 파일 시스템 작업 중 발생하는 오류를 처리합니다.
     * 디스크 용량 부족, 권한 문제, 파일 시스템 오류 등이 원인일 수 있습니다.
     *
     * @param e 입출력 예외
     * @return 에러 응답 (500 Internal Server Error)
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseData<Void>> handleIOException(IOException e) {
        log.error("[파일 저장소 핸들러] 파일 시스템 오류 - 원인: {}", e.getMessage(), e);

        SystemErrorCode errorCode = SystemErrorCode.FILE_STORAGE_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.name(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

//    ---------------------------- 외부 API 연동 예외

    /**
     * 외부 API 호출 실패 예외 처리
     *
     * RestTemplate, RestClient, WebClient 등을 통한 외부 API 호출 실패를 처리합니다.
     * OAuth 인증, 결제 API, SMS 발송 등 외부 서비스 연동 오류가 해당됩니다.
     *
     * @param e REST 클라이언트 예외
     * @return 에러 응답 (500 Internal Server Error)
     */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ResponseData<Void>> handleRestClientException(RestClientException e) {
        log.error("[외부 API 핸들러] 외부 API 호출 실패 - 원인: {}", e.getMessage(), e);

        SystemErrorCode errorCode = SystemErrorCode.EXTERNAL_API_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.name(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

//    ---------------------------- 일반 예외

    /**
     * 예상치 못한 예외 처리
     *
     * 다른 핸들러에서 처리되지 않은 모든 예외를 처리하는 최후의 안전장치입니다.
     * 이 핸들러가 자주 호출된다면 새로운 전용 핸들러 추가를 고려해야 합니다.
     *
     * @param e 예상치 못한 예외
     * @return 에러 응답 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Void>> handleGeneralException(Exception e) {
        log.error("[일반 예외 핸들러] 예상치 못한 오류 발생", e);

        SystemErrorCode errorCode = SystemErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.name(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(errorResponse));
    }

//    ---------------------  Private

    /**
     * DataIntegrityViolationException에서 적절한 SystemErrorCode 결정
     *
     * 예외 메시지를 분석하여 위반된 제약 조건 유형을 판별합니다.
     *
     * 감지 패턴:
     * - "duplicate key" 또는 "Duplicate entry": 중복 키 (PostgreSQL, MySQL)
     * - "foreign key constraint": 외래키 제약
     * - 기타: 일반 제약 조건 위반 (NOT NULL, CHECK 등)
     *
     * @param e 데이터 무결성 위반 예외
     * @return 적절한 SystemErrorCode
     */
    private SystemErrorCode determineConstraintErrorCode(DataIntegrityViolationException e) {
        String message = e.getMessage();

        if (message == null) {
            return SystemErrorCode.DATABASE_CONSTRAINT_VIOLATION;
        }

        // 중복 키 에러 감지
        if (message.contains("duplicate key") || message.contains("Duplicate entry")) {
            return SystemErrorCode.DATABASE_DUPLICATE_KEY_ERROR;
        }

        // 외래키 제약 위반 감지
        if (message.contains("foreign key constraint")) {
            return SystemErrorCode.DATABASE_FOREIGN_KEY_VIOLATION;
        }

        // 기타 제약 조건 위반
        return SystemErrorCode.DATABASE_CONSTRAINT_VIOLATION;
    }
}