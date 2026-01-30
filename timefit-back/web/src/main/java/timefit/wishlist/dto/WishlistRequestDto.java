package timefit.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Wishlist 요청 DTO
 */
@Schema(description = "찜 요청")
public class WishlistRequestDto {

    /**
     * 찜 추가 요청
     */
    @Schema(description = "찜 추가 요청")
    public record AddWishlistRequest(
            @Schema(description = "메뉴 ID", required = true, example = "10000000-0000-0000-0000-000000000001")
            @NotNull(message = "메뉴 ID는 필수입니다")
            UUID menuId
    ) {
    }
}