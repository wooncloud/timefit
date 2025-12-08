package timefit.common.swagger.requestbody.business;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.business.dto.BusinessRequestDto;
import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "업체 생성 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BusinessRequestDto.CreateBusinessRequest.class),
                examples = @ExampleObject(
                        value = """
                    {
                      "businessName": "홍길동 미용실",
                      "businessTypes": ["BD008"],
                      "businessNumber": "123-45-67890",
                      "ownerName": "홍길동",
                      "address": "서울특별시 강남구 테헤란로 123",
                      "contactPhone": "02-1234-5678",
                      "description": "20년 경력의 전문 미용실입니다.",
                      "logoUrl": "https://example.com/logo.png",
                      "businessNotice": "영업시간 변경: 평일 10:00-20:00"
                    }
                    """
                )
        )
)
public @interface CreateBusinessRequestBody {
}