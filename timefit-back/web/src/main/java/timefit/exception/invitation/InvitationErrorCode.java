package timefit.exception.invitation;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InvitationErrorCode {

    // 400 잘못된 요청
    DUPLICATE_INVITATION("이미 초대가 진행중입니다", HttpStatus.BAD_REQUEST),
    ALREADY_MEMBER("이미 업체 구성원입니다", HttpStatus.BAD_REQUEST),
    INVITATION_ALREADY_PROCESSED("이미 처리된 초대입니다", HttpStatus.BAD_REQUEST),
    INVITATION_EXPIRED("만료된 초대입니다", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("유효하지 않은 토큰입니다", HttpStatus.BAD_REQUEST),

    // 403 권한 문제
    EMAIL_MISMATCH("초대된 이메일과 일치하지 않습니다", HttpStatus.FORBIDDEN),
    INVITATION_NOT_BELONG_TO_BUSINESS("해당 업체의 초대가 아닙니다", HttpStatus.FORBIDDEN),

    // 404 리소스 찾을 수 없음
    INVITATION_NOT_FOUND("초대를 찾을 수 없습니다", HttpStatus.NOT_FOUND);

    // 410 Gone

    private final String message;
    private final HttpStatus httpStatus;

    InvitationErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}