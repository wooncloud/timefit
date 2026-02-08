package timefit.common.swagger.requestbody.wishlist;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.wishlist.dto.WishlistRequestDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "찜 추가 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = WishlistRequestDto.AddWishlist.class),
                examples = @ExampleObject(
                        value = """
                                {
                                  "menuId": "10000000-0000-0000-0000-000000000001"
                                }
                                """
                )
        )
)
public @interface AddWishlistBody {
}