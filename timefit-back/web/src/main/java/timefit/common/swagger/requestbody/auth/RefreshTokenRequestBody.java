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
 * 토큰 갱신 Request Body 문서화 Annotation
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "토큰 갱신 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthRequestDto.TokenRefresh.class),
                examples = @ExampleObject(
                        value = """
                    {
                      "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                    }
                    """
                )
        )
)
public @interface RefreshTokenRequestBody {
}