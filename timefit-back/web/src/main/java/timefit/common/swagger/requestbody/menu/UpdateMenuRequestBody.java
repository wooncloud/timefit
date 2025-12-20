package timefit.common.swagger.requestbody.menu;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.menu.dto.MenuRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "메뉴 수정 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MenuRequestDto.CreateUpdateMenu.class),
                examples = {
                        @ExampleObject(
                                name = "가격 및 설명 변경",
                                value = """
                        {
                            "price": 35000,
                            "description": "업그레이드된 헤어 컷 서비스"
                        }
                        """
                        ),
                        @ExampleObject(
                                name = "카테고리 변경",
                                value = """
                        {
                            "businessType": "BD003",
                            "categoryName": "프리미엄"
                        }
                        """
                        )
                }
        )
)
public @interface UpdateMenuRequestBody {
}