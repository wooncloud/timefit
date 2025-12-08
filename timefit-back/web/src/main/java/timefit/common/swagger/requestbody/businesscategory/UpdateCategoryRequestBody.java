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
        description = "카테고리 수정 요청 (변경할 필드만 입력)",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BusinessCategoryRequestDto.UpdateCategory.class),
                examples = {
                        @ExampleObject(
                                name = "카테고리명만 변경",
                                value = """
                                        {
                                          "categoryName": "헤어 펌"
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "비활성화",
                                value = """
                                        {
                                          "isActive": false
                                        }
                                        """
                        ),
                        @ExampleObject(
                                name = "여러 필드 동시 변경",
                                value = """
                                        {
                                          "categoryName": "스타일링",
                                          "categoryNotice": "새로운 공지사항입니다.",
                                          "isActive": true
                                        }
                                        """
                        )
                }
        )
)
public @interface UpdateCategoryRequestBody {
}