package timefit.common.swagger.requestbody.business;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.business.dto.BusinessRequestDto;
import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "구성원 권한 변경 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BusinessRequestDto.ChangeMemberRoleRequest.class),
                examples = @ExampleObject(
                        value = """
                    {
                      "newRole": "MANAGER"
                    }
                    """
                )
        )
)
public @interface ChangeMemberRoleRequestBody {
}