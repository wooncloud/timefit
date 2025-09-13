package timefit.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 성공 응답
//  return ResponseEntity.ok(ResponseData.of(userList));
// 에러 응답
// return ResponseEntity.badRequest()
//    .body(ResponseData.error("USER_NOT_FOUND", "사용자를 찾을 수 없습니다"));

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {

    private T data;
    private ErrorResponse errorResponse;

    public boolean hasError() {
        return errorResponse != null;
    }

    // 성공 응답
    public static <T> ResponseData<T> of(T data) {
        return new ResponseData<>(data, null);
    }

    // 실패 응답
    public static <T> ResponseData<T> error(ErrorResponse errorResponse) {
        return new ResponseData<>(null, errorResponse);
    }
    public static <T> ResponseData<T> error(String code, String message) {
        return error(ErrorResponse.of(code, message));
    }
    public static <T> ResponseData<T> error(String code, String message, String field) {
        return error(ErrorResponse.of(code, message, field));
    }
}