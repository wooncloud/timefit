package timefit.common.swagger.operation.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.auth.dto.AuthResponseDto;
import timefit.common.ResponseData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 토큰 갱신 API 문서화 Annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "JWT 토큰 갱신",
        description = """
            Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.
            
            1. Request Body 필수값
                - refreshToken: 만료되지 않은 Refresh Token
            
            2. 처리 과정
                - Refresh Token 유효성 검증
                - 새로운 Access Token 발급
                - 새로운 Refresh Token 발급 (Refresh Token Rotation)
                - 기존 Refresh Token은 무효화
            
            3. 응답
                - accessToken: 새로운 Access Token
                - refreshToken: 새로운 Refresh Token
                - tokenType: Bearer
                - expiresIn: Access Token 만료까지 남은 시간(초)
            
            4. 권한
                - 유효한 Refresh Token 필요
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "토큰 갱신 성공 및 새로운 토큰 발급",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AuthResponseDto.TokenRefresh.class),
                        examples = @ExampleObject(
                                value = """
                        {
                            "success": true,
                            "data": {
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "tokenType": "Bearer",
                            "expiresIn": 3600
                            }
                        }
                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                1. refreshToken 은(는) 필수 값입니다.
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = """
                TOKEN_INVALID - 유효하지 않거나 만료된 Refresh Token
                
                TOKEN_EXPIRED - 토큰 만료
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface RefreshTokenOperation {
}