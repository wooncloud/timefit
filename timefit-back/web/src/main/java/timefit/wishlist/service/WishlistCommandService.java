package timefit.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.service.validator.AuthValidator;
import timefit.business.entity.Business;
import timefit.business.service.validator.BusinessValidator;
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
    private final AuthValidator authValidator;
    private final BusinessValidator businessValidator;

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
     * @param businessId 메뉴 ID
     * @return 찜 추가 결과
     */
    public WishlistResponseDto.WishlistAction addWishlist(UUID userId, UUID businessId) {
        log.info("찜 추가 시작: userId={}, businessId={}", userId, businessId);

        // 1. 사용자 검증
        User user = authValidator.validateUserExists(userId);

        // 2. 업체 검증 -> 활성화 상태 체크
        Business business = businessValidator.validateBusinessExists(businessId);
        businessValidator.validateBusinessActive(business);

        // 3. 중복 검증
        wishlistValidator.validateNotDuplicate(userId, businessId);

        // 4. Wishlist 생성 및 저장
        Wishlist wishlist = Wishlist.create(user, business);
        wishlistRepository.save(wishlist);

        log.info("찜 추가 완료: wishlistId={}, userId={}, businessId={}",
                wishlist.getId(), userId, businessId);

        return WishlistResponseDto.WishlistAction.addSuccess(businessId);
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
        log.info("찜 삭제 시작: userId={}, businessId={}", userId, menuId);

        // 1. Wishlist 조회
        Wishlist wishlist = wishlistValidator.validateExistsByUserAndBusiness(userId, menuId);

        // 2. 소유권 검증 (이중 확인)
        wishlistValidator.validateOwnership(wishlist, userId);

        // 3. 삭제 처리
        wishlistRepository.delete(wishlist);

        log.info("찜 삭제 완료: wishlistId={}, userId={}, businessId={}",
                wishlist.getId(), userId, menuId);

        return WishlistResponseDto.WishlistAction.removeSuccess(menuId);
    }
}