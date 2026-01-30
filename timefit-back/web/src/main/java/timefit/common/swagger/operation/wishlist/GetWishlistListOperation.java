package timefit.common.swagger.operation.wishlist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.wishlist.dto.WishlistResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "찜 목록 조회",
        description = """
                내 찜 목록을 페이징하여 조회합니다.
                
                **응답 데이터:**
                - 찜 목록 (메뉴 정보, 업체 정보 포함)
                - 페이징 정보 (현재 페이지, 전체 개수)
                
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
                        schema = @Schema(implementation = WishlistResponseDto.WishlistList.class),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "success": true,
                                          "data": {
                                            "wishlists": [
                                              {
                                                "wishlistId": "550e8400-e29b-41d4-a716-446655440000",
                                                "menuId": "10000000-0000-0000-0000-000000000001",
                                                "menuName": "커트",
                                                "businessName": "스타일 헤어샵",
                                                "businessId": "30000000-0000-0000-0000-000000000001",
                                                "price": 30000,
                                                "durationMinutes": 60,
                                                "imageUrl": "https://example.com/menu.jpg",
                                                "createdAt": "2026-01-30T10:00:00"
                                              }
                                            ],
                                            "totalCount": 15,
                                            "page": 0,
                                            "size": 20,
                                            "totalPages": 1
                                          }
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
        name = "page",
        description = "페이지 번호 (0부터 시작)",
        example = "0"
)
@Parameter(
        name = "size",
        description = "페이지 크기",
        example = "20"
)
public @interface GetWishlistListOperation {
}