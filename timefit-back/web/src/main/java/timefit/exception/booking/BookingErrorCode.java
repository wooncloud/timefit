package timefit.exception.booking;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BookingErrorCode {


    AVAILABLE_SLOT_NOT_FOUND("예약 슬롯을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    AVAILABLE_SLOT_CONFLICT("예약 가능 시간이 중복됩니다", HttpStatus.CONFLICT),
    AVAILABLE_SLOT_PAST_DATE("과거 날짜에는 슬롯을 생성할 수 없습니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_INVALID_TIME("시작 시간이 종료 시간보다 늦을 수 없습니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_OUTSIDE_BUSINESS_HOURS("영업시간 외에는 슬롯을 생성할 수 없습니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_HAS_ACTIVE_RESERVATIONS("활성 예약이 있는 슬롯은 삭제할 수 없습니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_CAPACITY_EXCEEDED("슬롯 수용 인원을 초과했습니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_NOT_AVAILABLE("해당 슬롯은 현재 예약할 수 없습니다", HttpStatus.BAD_REQUEST),
    BUSINESS_CLOSED("해당 날짜는 영업일이 아닙니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_NOT_MODIFIABLE("활성 예약이 있는 슬롯은 수정할 수 없습니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_TIME_FORMAT_INVALID("슬롯 시간 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_CAPACITY_INVALID("슬롯 용량은 1명 이상이어야 합니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_BATCH_LIMIT_EXCEEDED("한 번에 생성할 수 있는 슬롯 개수를 초과했습니다", HttpStatus.BAD_REQUEST),
    AVAILABLE_SLOT_DATE_RANGE_EXCEEDED("슬롯 생성 가능 기간을 초과했습니다", HttpStatus.BAD_REQUEST),
    SLOT_INVALID_MENU_TYPE("예약형 메뉴만 슬롯을 생성할 수 있습니다", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    BookingErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
