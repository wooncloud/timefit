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
 * 로그인 API 문서화 Annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "일반 사용자 로그인",
        description = """
            이메일과 비밀번호를 사용하여 로그인하고 액세스/리프레시 토큰을 발급받습니다.
            
            1. Request Body 필수값
               - email: 이메일 (로그인 ID)
               - password: 비밀번호
            
            2. 인증 방식
               - 이메일과 비밀번호 일치 여부 확인
               - 성공 시 Access Token과 Refresh Token 발급
            
            3. 응답
               - 사용자 기본 정보
               - 연결된 업체 목록 (있는 경우)
               - Response Header에 Authorization: Bearer {accessToken} 포함
            
            4. 권한
               - 인증 불필요 (로그인 API)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "로그인 성공 및 토큰 발급",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AuthResponseDto.UserSignIn.class),
                        examples = @ExampleObject(
                                value = """
                        {
                          "success": true,
                          "data": {
                            "userId": "550e8400-e29b-41d4-a716-446655440000",
                            "email": "user@example.com",
                            "name": "홍길동",
                            "phoneNumber": "010-1234-5678",
                            "role": "BUSINESS",
                            "profileImageUrl": null,
                            "businesses": [
                              {
                                "businessId": "550e8400-e29b-41d4-a716-446655440001",
                                "businessName": "강남 헤어샵",
                                "businessTypes": ["BD008"],
                                "address": "서울시 강남구",
                                "contactPhone": "02-1234-5678",
                                "userRole": "OWNER",
                                "isBusinessActive": true,
                                "joinedAt": "2025-11-01T10:00:00"
                              }
                            ],
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "createdAt": "2025-11-01T10:00:00",
                            "lastLoginAt": "2025-11-23T10:00:00"
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
                1. email 은(는) 필수 값입니다.
                2. password 은(는) 필수 값입니다.
                3. 이메일 형식이 올바르지 않습니다.
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "INVALID_CREDENTIALS - 이메일 또는 비밀번호가 올바르지 않음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "USER_NOT_FOUND - 사용자를 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface SigninOperation {
}