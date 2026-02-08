package timefit.exception.review;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 리뷰(Review) 관련 에러 코드
 */
@Getter
public enum ReviewErrorCode {

    // 리뷰 조회 관련
    REVIEW_NOT_FOUND("리뷰를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_DELETED("이미 삭제된 리뷰입니다", HttpStatus.BAD_REQUEST),

    // 리뷰 작성 관련
    REVIEW_ALREADY_EXISTS("이미 리뷰를 작성한 예약입니다", HttpStatus.CONFLICT),
    INVALID_RESERVATION_STATUS("완료된 예약만 리뷰를 작성할 수 있습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_OWNER_MISMATCH("본인의 예약에만 리뷰를 작성할 수 있습니다", HttpStatus.BAD_REQUEST),

    // 권한 관련
    REVIEW_ACCESS_DENIED("해당 리뷰에 대한 권한이 없습니다", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus httpStatus;

    ReviewErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}