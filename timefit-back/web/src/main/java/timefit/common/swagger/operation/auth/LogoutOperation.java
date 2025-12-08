package timefit.common.swagger.operation.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 로그아웃 API 문서화 Annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "로그아웃",
        description = """
            현재 토큰을 서버에서 무효화합니다.
            
            1. Request Body 선택값
               - currentToken: 무효화할 Access Token 또는 Refresh Token
            
            2. 처리 과정
               - 제공된 토큰을 블랙리스트에 추가
               - 해당 토큰으로는 더 이상 인증 불가
            
            3. 권한
               - 로그인 필요
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "로그아웃 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "TOKEN_INVALID - 유효하지 않은 토큰",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface LogoutOperation {
}