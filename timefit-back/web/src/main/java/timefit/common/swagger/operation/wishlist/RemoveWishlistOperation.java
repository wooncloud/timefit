package timefit.common.swagger.operation.wishlist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.wishlist.dto.WishlistResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "찜 삭제",
        description = """
                찜 목록에서 메뉴를 삭제합니다.
                
                **검증:**
                - 찜 존재 여부 확인
                - 본인의 찜인지 확인
                
                **권한:**
                - 로그인한 고객만 사용 가능
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "찜 삭제 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = WishlistResponseDto.WishlistAction.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "success": true,
                                          "data": {
                                            "success": true,
                                            "message": "찜 목록에서 제거되었습니다",
                                            "menuId": "10000000-0000-0000-0000-000000000001"
                                          }
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패 - 로그인 필요"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "찜을 찾을 수 없음"
        )
})
@Parameter(
        name = "menuId",
        description = "메뉴 ID",
        required = true,
        example = "10000000-0000-0000-0000-000000000001"
)
public @interface RemoveWishlistOperation {
}
