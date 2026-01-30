package timefit.common.swagger.requestbody.user;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.user.dto.UserRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "비밀번호 변경 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserRequestDto.ChangePassword.class),
                examples = @ExampleObject(
                        value = """
                                {
                                  "currentPassword": "oldPassword123!",
                                  "newPassword": "newPassword123!",
                                  "newPasswordConfirm": "newPassword123!"
                                }
                                """
                )
        )
)
public @interface ChangePasswordBody {
}
