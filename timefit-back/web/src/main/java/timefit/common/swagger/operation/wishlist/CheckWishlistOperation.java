package timefit.common.swagger.operation.wishlist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "찜 여부 확인",
        description = """
                특정 메뉴가 찜 목록에 있는지 확인합니다.
                
                **응답:**
                - true: 찜한 메뉴
                - false: 찜하지 않은 메뉴
                
                **권한:**
                - 로그인한 고객만 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Boolean.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "success": true,
                                          "data": true
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패 - 로그인 필요"
        )
})
@Parameter(
        name = "menuId",
        description = "메뉴 ID",
        required = true,
        example = "10000000-0000-0000-0000-000000000001"
)
public @interface CheckWishlistOperation {
}
