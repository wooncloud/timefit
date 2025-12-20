package timefit.common.swagger.requestbody.businesscategory;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.businesscategory.dto.BusinessCategoryRequestDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "카테고리 생성 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BusinessCategoryRequestDto.CreateCategory.class),
                examples = @ExampleObject(
                        value = """
                                {
                                  "businessType": "BD008",
                                  "categoryName": "헤어 컷",
                                  "categoryNotice": "예약 시 주의사항을 확인해주세요."
                                }
                                """
                )
        )
)
public @interface CreateCategoryRequestBody {
}