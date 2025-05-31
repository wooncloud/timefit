package org.example.timefitback.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetail error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String code;
        private String message;
        private String field;

        public static ErrorDetail of(String code, String message) {
            return new ErrorDetail(code, message, null);
        }

        public static ErrorDetail of(String code, String message, String field) {
            return new ErrorDetail(code, message, field);
        }
    }

    // 성공 응답 (정적 팩토리 메서드)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다", data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    // 실패 응답 (정적 팩토리 메서드)
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, "요청 처리 중 오류가 발생했습니다", null,
                ErrorDetail.of(code, message));
    }

    public static <T> ApiResponse<T> error(String code, String message, String field) {
        return new ApiResponse<>(false, "요청 처리 중 오류가 발생했습니다", null,
                ErrorDetail.of(code, message, field));
    }
}