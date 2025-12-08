package timefit.common.swagger.requestbody.auth;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.auth.dto.AuthRequestDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 로그인 Request Body 문서화 Annotation
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "로그인 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthRequestDto.UserSignIn.class),
                examples = @ExampleObject(
                        value = """
                    {
                      "email": "user@example.com",
                      "password": "a12345678"
                    }
                    """
                )
        )
)
public @interface SigninRequestBody {
}