package timefit.exception.reservation;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReservationErrorCode {
    // 예약 관리 관련
    RESERVATION_NOT_FOUND("예약 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    RESERVATION_SLOT_UNAVAILABLE("선택한 시간대는 예약할 수 없습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_ALREADY_EXISTS("해당 시간대에 이미 예약이 있습니다", HttpStatus.CONFLICT),
    RESERVATION_CAPACITY_EXCEEDED("예약 가능 인원을 초과했습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_DEADLINE_PASSED("예약 변경/취소 기한이 지났습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_INVALID_STATUS("현재 예약 상태에서는 처리할 수 없습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_PAST_DATE("과거 날짜는 예약할 수 없습니다", HttpStatus.BAD_REQUEST),
    NOT_BUSINESS_MEMBER("업체 구성원만 접근할 수 있습니다", HttpStatus.FORBIDDEN),
    NOT_RESERVATION_OWNER("예약 소유자만 접근할 수 있습니다", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus httpStatus;

    ReservationErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}