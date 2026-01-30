package timefit.wishlist.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.wishlist.WishlistErrorCode;
import timefit.exception.wishlist.WishlistException;
import timefit.wishlist.entity.Wishlist;
import timefit.wishlist.repository.WishlistRepository;

import java.util.UUID;

/**
 * Wishlist 검증 전담 클래스
 * 주요 역할:
 * - Wishlist 존재 여부 검증
 * - Wishlist 중복 검증
 * - 소유권 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WishlistValidator {

    private final WishlistRepository wishlistRepository;

    /**
     * Wishlist 존재 여부 검증 및 조회
     *
     * @param wishlistId Wishlist ID
     * @return 조회된 Wishlist 엔티티
     * @throws WishlistException 존재하지 않는 경우
     */
    public Wishlist validateExists(UUID wishlistId) {
        return wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 찜 ID: {}", wishlistId);
                    return new WishlistException(WishlistErrorCode.WISHLIST_NOT_FOUND);
                });
    }

    /**
     * Wishlist 중복 검증
     * 이미 찜한 메뉴인 경우 예외 발생
     *
     * @param userId 사용자 ID
     * @param menuId 메뉴 ID
     * @throws WishlistException 이미 찜한 메뉴인 경우
     */
    public void validateNotDuplicate(UUID userId, UUID menuId) {
        boolean exists = wishlistRepository.existsByUserIdAndMenuId(userId, menuId);

        if (exists) {
            log.warn("중복 찜 시도: userId={}, menuId={}", userId, menuId);
            throw new WishlistException(WishlistErrorCode.WISHLIST_ALREADY_EXISTS);
        }
    }

    /**
     * Wishlist 소유권 검증
     * 해당 Wishlist가 사용자 소유인지 확인
     *
     * @param wishlist 검증할 Wishlist
     * @param userId 사용자 ID
     * @throws WishlistException 소유권이 없는 경우
     */
    public void validateOwnership(Wishlist wishlist, UUID userId) {
        if (!wishlist.getUser().getId().equals(userId)) {
            log.warn("찜 소유권 없음: wishlistId={}, userId={}", wishlist.getId(), userId);
            throw new WishlistException(WishlistErrorCode.WISHLIST_ACCESS_DENIED);
        }
    }

    /**
     * 사용자와 메뉴로 Wishlist 조회
     *
     * @param userId 사용자 ID
     * @param menuId 메뉴 ID
     * @return 조회된 Wishlist 엔티티
     * @throws WishlistException 찜이 존재하지 않는 경우
     */
    public Wishlist validateExistsByUserAndMenu(UUID userId, UUID menuId) {
        return wishlistRepository.findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> {
                    log.warn("찜을 찾을 수 없음: userId={}, menuId={}", userId, menuId);
                    return new WishlistException(WishlistErrorCode.WISHLIST_NOT_FOUND);
                });
    }
}