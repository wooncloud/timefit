package timefit.common.swagger.requestbody.invitation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.invitation.dto.InvitationRequestDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "초대 수락 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InvitationRequestDto.AcceptInvitation.class),
                examples = @ExampleObject(
                        value = """
                                {
                                  "token": "141e0d7a-2149-4aa2-bfe1-619ca188d593"
                                }
                                """
                )
        )
)
public @interface AcceptInvitationRequestBody {
}