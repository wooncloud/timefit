package timefit.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.service.validator.AuthValidator;
import timefit.wishlist.dto.WishlistResponseDto;
import timefit.wishlist.entity.Wishlist;
import timefit.wishlist.repository.WishlistRepository;

import java.util.List;
import java.util.UUID;

/**
 * Wishlist 조회 전담 서비스 (Read)
 *
 * 주요 역할:
 * - 찜 목록 조회
 * - 찜 개수 조회
 * - 찜 여부 확인
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistQueryService {

    private final WishlistRepository wishlistRepository;
    private final AuthValidator authValidator;

    /**
     * 사용자의 찜 목록 조회 (페이징)
     *
     * 프로세스:
     * 1. 사용자 검증
     * 2. Wishlist 조회 (최신순)
     * 3. DTO 변환
     *
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 찜 목록 응답
     */
    public WishlistResponseDto.WishlistList getWishlistList(
            UUID userId,
            int page,
            int size) {

        log.info("찜 목록 조회 시작: userId={}, page={}, size={}", userId, page, size);

        // 1. 사용자 검증
        authValidator.validateUserExists(userId);

        // 2. Wishlist 조회 (최신순)
        Pageable pageable = PageRequest.of(page, size);
        Page<Wishlist> wishlistPage = wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        // 3. DTO 변환
        List<WishlistResponseDto.WishlistItem> wishlistItems = wishlistPage.getContent()
                .stream()
                .map(WishlistResponseDto.WishlistItem::from)
                .toList();

        log.info("찜 목록 조회 완료: userId={}, count={}, totalElements={}",
                userId, wishlistItems.size(), wishlistPage.getTotalElements());

        return WishlistResponseDto.WishlistList.of(
                wishlistItems,
                wishlistPage.getTotalElements(),
                wishlistPage.getNumber(),
                wishlistPage.getSize(),
                wishlistPage.getTotalPages()
        );
    }

    /**
     * 특정 메뉴를 찜했는지 확인
     *
     * 프로세스:
     * 1. 사용자 검증
     * 2. 찜 여부 확인
     *
     * @param userId 사용자 ID
     * @param menuId 메뉴 ID
     * @return 찜 여부
     */
    public boolean isWishlisted(UUID userId, UUID menuId) {
        log.debug("찜 여부 확인 시작: userId={}, menuId={}", userId, menuId);

        // 1. 사용자 검증
        authValidator.validateUserExists(userId);

        // 2. 찜 여부 확인
        boolean exists = wishlistRepository.existsByUserIdAndMenuId(userId, menuId);

        log.debug("찜 여부 확인 완료: userId={}, menuId={}, exists={}",
                userId, menuId, exists);

        return exists;
    }

    /**
     * 사용자의 찜 개수 조회
     *
     * @param userId 사용자 ID
     * @return 찜 개수
     */
    public long getWishlistCount(UUID userId) {
        log.debug("찜 개수 조회: userId={}", userId);

        long count = wishlistRepository.countByUserId(userId);

        log.debug("찜 개수 조회 결과: userId={}, count={}", userId, count);

        return count;
    }
}