package timefit.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Wishlist 요청 DTO
 */
@Schema(description = "찜 관리 요청")
public class WishlistRequestDto {

    /**
     * 찜 추가 요청
     */
    @Schema(description = "찜 추가 요청")
    public record AddWishlist(
            @Schema(
                    description = "메뉴 ID",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "메뉴 ID는 필수입니다")
            UUID menuId
    ) {
    }
}