package timefit.common.swagger.operation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "비밀번호 변경",
        description = """
                사용자 비밀번호를 변경합니다.
                
                **요청 데이터:**
                - currentPassword: 현재 비밀번호 (필수)
                - newPassword: 새 비밀번호 (필수, 8-20자, 영문+숫자+특수문자)
                - newPasswordConfirm: 새 비밀번호 확인 (필수)
                
                **검증:**
                - 현재 비밀번호 일치 확인
                - 새 비밀번호와 확인 일치 확인
                - 비밀번호 규칙 확인
                
                **권한:**
                - 로그인한 고객만 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", content = @Content),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (현재 비밀번호 불일치, 확인 불일치 등)"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
})
public @interface ChangePasswordOperation {
}
