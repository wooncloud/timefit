package timefit.common.swagger.requestbody.user;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.user.dto.UserRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "프로필 수정 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserRequestDto.UpdateProfile.class),
                examples = @ExampleObject(
                        value = """
                                {
                                  "name": "홍길동",
                                  "phoneNumber": "010-9999-8888",
                                  "profileImageUrl": "https://example.com/new-profile.jpg"
                                }
                                """
                )
        )
)
public @interface UpdateProfileBody {
}