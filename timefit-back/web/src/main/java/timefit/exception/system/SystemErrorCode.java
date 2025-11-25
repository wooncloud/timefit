package timefit.exception.system;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 시스템 레벨 에러 코드
 * 도메인 예외가 아닌 시스템, 인프라, 외부 연동 관련 에러를 정의합니다.
 * 모든 에러 코드는 HttpStatus와 사용자 메시지를 포함합니다.
 */
@Getter
public enum SystemErrorCode {

    /**
     * 시스템 관련
     */

    // 다른 핸들러에서 처리되지 않은 모든 예외의 기본 응답
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * 데이터베이스 관련
     */

    // DB 서버 다운, 네트워크 문제, 연결 풀 고갈 등
    DATABASE_CONNECTION_ERROR("데이터베이스 연결에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    // SQL 문법 오류, 권한 문제 등
    DATABASE_QUERY_ERROR("데이터베이스 쿼리 실행 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    // 복잡한 쿼리, 대량 데이터 조회, 인덱스 미사용 등
    DATABASE_TIMEOUT_ERROR("데이터베이스 응답 시간이 초과되었습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    // UNIQUE 제약 조건 위반 (이메일 중복, 사업자번호 중복 등)
    DATABASE_DUPLICATE_KEY_ERROR("이미 존재하는 데이터입니다", HttpStatus.CONFLICT),
    // 참조 무결성 위반 (존재하지 않는 외래키 참조, 참조되는 데이터 삭제 시도 등)
    DATABASE_FOREIGN_KEY_VIOLATION("참조 무결성 제약 조건 위반입니다", HttpStatus.CONFLICT),
    // NOT NULL, CHECK 제약 등
    DATABASE_CONSTRAINT_VIOLATION("데이터베이스 제약 조건 위반입니다", HttpStatus.CONFLICT),

    /**
     * 외부 API 연동 관련
     */

    // OAuth 인증, 결제 API, SMS 발송 등 외부 서비스 연동 오류
    EXTERNAL_API_ERROR("외부 API 호출 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * 파일 처리 관련
     */

    // 파일 크기 초과, 지원하지 않는 형식, 업로드 중 네트워크 오류 등
    FILE_UPLOAD_ERROR("파일 업로드에 실패했습니다", HttpStatus.BAD_REQUEST),
    // 디스크 용량 부족, 저장소 접근 권한 문제, 파일 시스템 오류 등
    FILE_STORAGE_ERROR("파일 저장소 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;

    SystemErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}