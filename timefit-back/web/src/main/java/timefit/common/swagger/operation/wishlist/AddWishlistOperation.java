package timefit.common.swagger.operation.wishlist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.wishlist.dto.WishlistResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "찜 추가",
        description = """
            메뉴를 찜 목록에 추가합니다.
            
            1. Request Body 필수값
               - menuId: 찜할 메뉴 ID (UUID)
            
            2. 검증 규칙
               - 메뉴가 존재해야 함
               - 메뉴가 활성 상태여야 함
               - 이미 찜한 메뉴가 아니어야 함
            
            3. 권한
               - 로그인한 고객
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "찜 추가 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = WishlistResponseDto.WishlistAction.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "MENU_NOT_ACTIVE - 비활성 메뉴",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = """
                USER_NOT_FOUND - 사용자를 찾을 수 없음
                
                MENU_NOT_FOUND - 메뉴를 찾을 수 없음
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "409",
                description = "WISHLIST_ALREADY_EXISTS - 이미 찜한 메뉴",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface AddWishlistOperation {
}
