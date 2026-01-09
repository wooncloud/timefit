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
 * 회원가입 API 문서화 Annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "일반 사용자 회원가입",
        description = """
            이메일, 비밀번호를 사용하여 신규 사용자를 등록합니다.
            
            1. Request Body 필수값
               - email: 이메일 (로그인 ID)
               - password: 비밀번호 (최소 8자)
               - name: 사용자 이름
            
            2. Request Body 선택값
               - phoneNumber: 연락처
            
            3. 제약사항
               - email: 유효한 이메일 형식
               - password: 최소 8자 이상
               - 중복된 이메일로 가입 불가
            
            4. 응답
               - 가입 성공 시 자동으로 로그인되며 Access Token과 Refresh Token 발급
               - Response Header에 Authorization: Bearer {accessToken} 포함
            
            5. 권한
               - 인증 불필요 (누구나 가입 가능)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "회원가입 성공 및 토큰 발급",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AuthResponseDto.UserSignUp.class),
                        examples = @ExampleObject(
                                value = """
                        {
                          "success": true,
                          "data": {
                            "userId": "10000000-0000-0000-0000-000000000001",
                            "email": "owner1@timefit.com",
                            "name": "Owner Kim",
                            "phoneNumber": "010-1111-1111",
                            "role": "CUSTOMER",
                            "profileImageUrl": null,
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "createdAt": "2025-11-23T10:00:00",
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
                3. name 은(는) 필수 값입니다.
                4. 이메일 형식이 올바르지 않습니다.
                5. 비밀번호는 최소 8자 이상이어야 합니다.
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "409",
                description = "EMAIL_ALREADY_EXISTS - 이미 존재하는 이메일",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface SignupOperation {
}