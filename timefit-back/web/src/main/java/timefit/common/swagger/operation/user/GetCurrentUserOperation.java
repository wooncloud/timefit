package timefit.common.swagger.operation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.user.dto.UserResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "현재 사용자 정보 조회",
        description = """
                현재 로그인한 사용자의 전체 정보를 조회합니다.
                
                **응답 데이터:**
                - 사용자 기본 정보 (이름, 이메일, 전화번호 등)
                - 소속 업체 목록 (사업자인 경우)
                - 일반 고객인 경우 businesses는 빈 배열
                
                **사용처:**
                - 네비게이션 바
                - 사용자 프로필 화면
                - 업체 선택 드롭다운
                
                **권한:**
                - 사업자, 고객 모두 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.CurrentUser.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "success": true,
                                          "data": {
                                            "userId": "20000000-0000-0000-0000-000000000001",
                                            "email": "user@example.com",
                                            "name": "홍길동",
                                            "phoneNumber": "010-1234-5678",
                                            "profileImageUrl": "https://example.com/profile.jpg",
                                            "createdAt": "2026-01-01T10:00:00",
                                            "lastLoginAt": "2026-01-30T09:00:00",
                                            "businesses": [
                                              {
                                                "businessId": "30000000-0000-0000-0000-000000000001",
                                                "businessName": "스타일 헤어샵",
                                                "logoUrl": "https://example.com/logo.png",
                                                "myRole": "OWNER",
                                                "isActive": true
                                              }
                                            ]
                                          }
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패 - 로그인 필요"
        )
})
public @interface GetCurrentUserOperation {
}