package timefit.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.service.validator.AuthValidator;
import timefit.menu.entity.Menu;
import timefit.menu.service.validator.MenuValidator;
import timefit.user.entity.User;
import timefit.wishlist.dto.WishlistResponseDto;
import timefit.wishlist.entity.Wishlist;
import timefit.wishlist.repository.WishlistRepository;
import timefit.wishlist.service.validator.WishlistValidator;

import java.util.UUID;

/**
 * Wishlist 데이터 변경 전담 서비스 (CUD)
 *
 * 주요 역할:
 * - 찜 추가
 * - 찜 삭제
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WishlistCommandService {

    private final WishlistRepository wishlistRepository;
    private final WishlistValidator wishlistValidator;
    private final MenuValidator menuValidator;
    private final AuthValidator authValidator;

    /**
     * 찜 추가
     *
     * 프로세스:
     * 1. 사용자 검증
     * 2. 메뉴 검증
     * 3. 중복 검증
     * 4. Wishlist 생성 및 저장
     *
     * @param userId 사용자 ID
     * @param menuId 메뉴 ID
     * @return 찜 추가 결과
     */
    public WishlistResponseDto.WishlistAction addWishlist(UUID userId, UUID menuId) {
        log.info("찜 추가 시작: userId={}, menuId={}", userId, menuId);

        // 1. 사용자 검증
        User user = authValidator.validateUserExists(userId);

        // 2. 메뉴 검증 (존재 및 활성 상태)
        Menu menu = menuValidator.validateMenuExists(menuId);
        menuValidator.validateMenuActive(menu);

        // 3. 중복 검증
        wishlistValidator.validateNotDuplicate(userId, menuId);

        // 4. Wishlist 생성 및 저장
        Wishlist wishlist = Wishlist.create(user, menu);
        wishlistRepository.save(wishlist);

        log.info("찜 추가 완료: wishlistId={}, userId={}, menuId={}",
                wishlist.getId(), userId, menuId);

        return WishlistResponseDto.WishlistAction.addSuccess(menuId);
    }

    /**
     * 찜 삭제
     *
     * 프로세스:
     * 1. Wishlist 조회 및 검증
     * 2. 소유권 검증
     * 3. 삭제 처리
     *
     * @param userId 사용자 ID
     * @param menuId 메뉴 ID
     * @return 찜 삭제 결과
     */
    public WishlistResponseDto.WishlistAction removeWishlist(UUID userId, UUID menuId) {
        log.info("찜 삭제 시작: userId={}, menuId={}", userId, menuId);

        // 1. Wishlist 조회
        Wishlist wishlist = wishlistValidator.validateExistsByUserAndMenu(userId, menuId);

        // 2. 소유권 검증 (이중 확인)
        wishlistValidator.validateOwnership(wishlist, userId);

        // 3. 삭제 처리
        wishlistRepository.delete(wishlist);

        log.info("찜 삭제 완료: wishlistId={}, userId={}, menuId={}",
                wishlist.getId(), userId, menuId);

        return WishlistResponseDto.WishlistAction.removeSuccess(menuId);
    }
}