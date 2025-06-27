package timefit.exception.system;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SystemErrorCode {
    // 시스템 관련
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_CONNECTION_ERROR("데이터베이스 연결에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_QUERY_ERROR("데이터베이스 쿼리 실행 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_TIMEOUT_ERROR("데이터베이스 응답 시간이 초과되었습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_API_ERROR("외부 API 호출 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_ERROR("파일 업로드에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_STORAGE_ERROR("파일 저장소 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;

    SystemErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}