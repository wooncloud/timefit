package timefit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import timefit.exception.system.SystemErrorCode;
import timefit.exception.system.SystemException;

import java.util.function.Supplier;

@Slf4j
public class DatabaseExceptionHandler {

    /**
     * DB 작업을 안전하게 실행 (반환값 있음)
     *
     * @param operation 실행할 DB 작업
     * @param context 에러 발생 시 로그에 포함할 컨텍스트 정보
     * @param <T> 반환 타입
     * @return 작업 실행 결과
     * @throws SystemException DB 작업 실패 시
     */
    public static <T> T executeSafely(Supplier<T> operation, String context) {
        try {
            return operation.get();

        } catch (DataIntegrityViolationException e) {
            log.error("DB 제약조건 위반: context={}, error={}", context, e.getMessage());

            // 구체적인 에러 메시지 생성
            String detailMessage = buildConstraintViolationMessage(e, context);
            throw new SystemException(SystemErrorCode.DATABASE_QUERY_ERROR, detailMessage);

        } catch (QueryTimeoutException e) {
            log.error("DB 쿼리 타임아웃: context={}, error={}", context, e.getMessage());
            throw new SystemException(SystemErrorCode.DATABASE_TIMEOUT_ERROR);

        } catch (DataAccessException e) {
            log.error("DB 접근 오류: context={}, error={}", context, e.getMessage(), e);
            throw new SystemException(SystemErrorCode.DATABASE_CONNECTION_ERROR);
        }
    }

    /**
     * DB 작업을 안전하게 실행 (반환값 없음)
     *
     * @param operation 실행할 DB 작업
     * @param context 에러 발생 시 로그에 포함할 컨텍스트 정보
     * @throws SystemException DB 작업 실패 시
     */
    public static void executeSafelyVoid(Runnable operation, String context) {
        executeSafely(() -> {
            operation.run();
            return null;
        }, context);
    }

    /**
     * 제약조건 위반 상세 메시지 생성
     */
    private static String buildConstraintViolationMessage(
            DataIntegrityViolationException e,
            String context) {

        String errorMsg = e.getMessage();
        if (errorMsg == null) {
            return context + " 처리 중 데이터베이스 오류가 발생했습니다";
        }

        // 중복 키 에러
        if (errorMsg.contains("duplicate key") || errorMsg.contains("Duplicate entry")) {
            return context + " 처리 중 중복된 데이터가 발견되었습니다";
        }

        // 외래키 제약조건 위반
        if (errorMsg.contains("foreign key constraint")) {
            return context + " 처리 중 참조 무결성 제약조건 위반이 발생했습니다";
        }

        // NULL 제약조건 위반
        if (errorMsg.contains("not-null") || errorMsg.contains("NULL")) {
            return context + " 처리 중 필수 값이 누락되었습니다";
        }

        return context + " 처리 중 데이터베이스 제약조건 위반이 발생했습니다";
    }
}