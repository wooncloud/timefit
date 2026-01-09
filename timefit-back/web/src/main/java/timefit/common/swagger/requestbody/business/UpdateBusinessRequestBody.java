package timefit.common.swagger.requestbody.business;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.business.dto.BusinessRequestDto;
import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "업체 수정 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BusinessRequestDto.UpdateBusinessRequest.class),
                examples = @ExampleObject(
                        value = """
                    {
                      "businessName": "타임핏 헤어샵",
                      "businessTypes": ["BD008"],
                      "businessNumber": "123-45-67890",
                      "ownerName": "Owner Kim",
                      "address": "Seoul Gangnam 456",
                      "contactPhone": "02-2222-2222",
                      "description": "트렌디한 헤어 디자인 전문",
                      "logoUrl": "https://example.com/new-logo.png",
                      "businessNotice": "주차 가능합니다"
                    }
                    """
                )
        )
)
public @interface UpdateBusinessRequestBody {
}