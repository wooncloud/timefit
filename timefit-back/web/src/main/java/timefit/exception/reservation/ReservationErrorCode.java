package timefit.exception.reservation;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReservationErrorCode {
    // 예약 조회/일반 관련
    RESERVATION_NOT_FOUND("예약 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    RESERVATION_NOT_READY_FOR_COMPLETION("아직 완료 처리할 수 없는 예약입니다", HttpStatus.BAD_REQUEST),
    RESERVATION_ALREADY_EXISTS("해당 시간대에 이미 예약이 있습니다", HttpStatus.CONFLICT),
    RESERVATION_INVALID_STATUS("현재 예약 상태에서는 처리할 수 없습니다", HttpStatus.BAD_REQUEST),

    // 예약 상태 관련
    RESERVATION_NOT_MODIFIABLE("현재 예약 상태에서는 수정할 수 없습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_CANCELLABLE("현재 예약 상태에서는 취소할 수 없습니다", HttpStatus.BAD_REQUEST),

    // 예약 시간/조건 관련
    RESERVATION_TIME_UNAVAILABLE("선택한 시간대는 예약할 수 없습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_SLOT_UNAVAILABLE("영업 시간 내에서만 예약할 수 있습니다",  HttpStatus.BAD_REQUEST),
    RESERVATION_BUSINESS_CLOSED("해당 날짜는 영업하지 않습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_CAPACITY_EXCEEDED("예약 인원을 다시 확인해주세요", HttpStatus.BAD_REQUEST),
    RESERVATION_DEADLINE_PASSED("예약 변경/취소 기한이 지났습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_PAST_DATE("과거 날짜는 예약할 수 없습니다", HttpStatus.BAD_REQUEST),

    MODIFICATION_REASON_REQUIRED("예약 수정 사유는 필수입니다", HttpStatus.BAD_REQUEST),
    MODIFICATION_DEADLINE_PASSED("예약 수정 가능 시간이 지났습니다", HttpStatus.BAD_REQUEST),
    CANCELLATION_DEADLINE_PASSED("예약 취소 가능 시간이 지났습니다", HttpStatus.BAD_REQUEST),
    CALENDAR_DATE_REQUIRED("캘린더 조회를 위한 시작일과 종료일은 필수입니다", HttpStatus.BAD_REQUEST),
    CALENDAR_DATE_RANGE_TOO_LARGE("조회 가능한 최대 기간을 초과했습니다 (최대 1년)", HttpStatus.BAD_REQUEST),

    // 권한 관련
    NOT_BUSINESS_MEMBER("업체 구성원만 접근할 수 있습니다", HttpStatus.FORBIDDEN),
    NOT_RESERVATION_OWNER("예약 소유자만 접근할 수 없습니다", HttpStatus.FORBIDDEN),

    // 유효성 검사
    INVALID_DATE_FORMAT("날짜 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_STATUS("예약 상태 값이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_NUMBER("페이지 번호가 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_SIZE("페이지 크기가 올바르지 않습니다", HttpStatus.BAD_REQUEST);



    private final String message;
    private final HttpStatus httpStatus;

    ReservationErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}