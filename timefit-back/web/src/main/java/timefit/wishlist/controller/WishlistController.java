package timefit.wishlist.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.common.swagger.operation.wishlist.*;
import timefit.common.swagger.requestbody.wishlist.*;
import timefit.wishlist.dto.WishlistRequestDto;
import timefit.wishlist.dto.WishlistResponseDto;
import timefit.wishlist.service.WishlistService;

import java.util.UUID;

/**
 * Wishlist Controller (고객용)
 *
 * 주요 기능:
 * - 찜 목록 조회 (페이징)
 * - 찜 추가
 * - 찜 삭제
 * - 찜 여부 확인
 */
@Tag(name = "10. 찜 관리 (고객)", description = "고객의 찜(업체 위시 리스트) 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/customer/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * 찜 목록 조회 (페이징)
     * GET /api/customer/wishlist?page=0&size=20
     */
    @GetWishlistListOperation
    @GetMapping
    public ResponseEntity<ResponseData<WishlistResponseDto.WishlistList>> getWishlistList(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("찜 목록 조회 요청: userId={}, page={}, size={}", userId, page, size);

        WishlistResponseDto.WishlistList response =
                wishlistService.getWishlistList(userId, page, size);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 찜 추가
     * POST /api/customer/wishlist
     */
    @AddWishlistOperation
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseData<WishlistResponseDto.WishlistAction>> addWishlist(
            @AddWishlistBody
            @Valid @RequestBody WishlistRequestDto.AddWishlist request,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("찜 추가 요청: userId={}, businessId={}", userId, request.businessId());

        WishlistResponseDto.WishlistAction response =
                wishlistService.addWishlist(userId, request.businessId());

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    /**
     * 찜 삭제
     * DELETE /api/customer/wishlist/{businessId}
     */
    @RemoveWishlistOperation
    @DeleteMapping("/{businessId}")
    public ResponseEntity<ResponseData<WishlistResponseDto.WishlistAction>> removeWishlist(
            @Parameter(description = "메뉴 ID", required = true, example = "10000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("찜 삭제 요청: userId={}, businessId={}", userId, businessId);

        WishlistResponseDto.WishlistAction response =
                wishlistService.removeWishlist(userId, businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 찜 여부 확인
     * GET /api/customer/wishlist/check/{businessId}
     */
    @CheckWishlistOperation
    @GetMapping("/check/{businessId}")
    public ResponseEntity<ResponseData<Boolean>> isWishlisted(
            @Parameter(description = "메뉴 ID", required = true, example = "10000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.debug("찜 여부 확인 요청: userId={}, businessId={}", userId, businessId);

        boolean isWishlisted = wishlistService.isWishlisted(userId, businessId);

        return ResponseEntity.ok(ResponseData.of(isWishlisted));
    }
}