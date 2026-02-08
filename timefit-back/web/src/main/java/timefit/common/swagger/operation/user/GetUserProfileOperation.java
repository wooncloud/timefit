package timefit.common.swagger.operation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.user.dto.UserResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "내 프로필 조회",
        description = """
                현재 로그인한 고객의 프로필을 조회합니다 (통계 포함).
                
                **응답 데이터:**
                - 사용자 기본 정보
                - 통계 (예약 수, 찜 개수, 리뷰 수)
                
                **권한:**
                - 로그인한 고객만 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.UserProfile.class),
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
                                            "statistics": {
                                              "totalReservations": 15,
                                              "wishlistCount": 8,
                                              "reviewCount": 5
                                            }
                                          }
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패")
})
public @interface GetUserProfileOperation {
}