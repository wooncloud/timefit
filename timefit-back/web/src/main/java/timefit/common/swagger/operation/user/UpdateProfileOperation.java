package timefit.common.swagger.operation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.user.dto.UserResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "프로필 수정",
        description = """
                사용자 프로필을 수정합니다.
                
                **요청 데이터:**
                - name: 이름 (선택, 2-50자)
                - phoneNumber: 전화번호 (선택)
                - profileImageUrl: 프로필 이미지 URL (선택)
                
                **참고:**
                - null이 아닌 필드만 업데이트됩니다
                
                **권한:**
                - 로그인한 고객만 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDto.UserProfile.class)
                )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
})
public @interface UpdateProfileOperation {
}
