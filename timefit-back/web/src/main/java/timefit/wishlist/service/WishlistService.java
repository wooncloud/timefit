package timefit.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.wishlist.dto.WishlistResponseDto;

import java.util.UUID;

/**
 * Wishlist Facade Service
 *
 * 역할:
 * - Controller와 Service 계층 사이의 단일 진입점
 * - 트랜잭션 경계 설정
 * - Command/Query Service에 단순 위임
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistCommandService commandService;
    private final WishlistQueryService queryService;

    /**
     * 찜 목록 조회
     *
     * @param userId 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 찜 목록 응답
     */
    public WishlistResponseDto.WishlistList getWishlistList(
            UUID userId,
            int page,
            int size) {

        log.debug("Facade: 찜 목록 조회 - userId={}, page={}, size={}", userId, page, size);
        return queryService.getWishlistList(userId, page, size);
    }

    /**
     * 찜 추가
     *
     * @param userId 사용자 ID
     * @param menuId 메뉴 ID
     * @return 찜 추가 결과
     */
    @Transactional
    public WishlistResponseDto.WishlistAction addWishlist(UUID userId, UUID menuId) {
        log.debug("Facade: 찜 추가 - userId={}, menuId={}", userId, menuId);
        return commandService.addWishlist(userId, menuId);
    }

    /**
     * 찜 삭제
     *
     * @param userId 사용자 ID
     * @param menuId 메뉴 ID
     * @return 찜 삭제 결과
     */
    @Transactional
    public WishlistResponseDto.WishlistAction removeWishlist(UUID userId, UUID menuId) {
        log.debug("Facade: 찜 삭제 - userId={}, menuId={}", userId, menuId);
        return commandService.removeWishlist(userId, menuId);
    }

    /**
     * 찜 여부 확인
     *
     * @param userId 사용자 ID
     * @param menuId 메뉴 ID
     * @return 찜 여부
     */
    public boolean isWishlisted(UUID userId, UUID menuId) {
        log.debug("Facade: 찜 여부 확인 - userId={}, menuId={}", userId, menuId);
        return queryService.isWishlisted(userId, menuId);
    }

    /**
     * 찜 개수 조회
     *
     * @param userId 사용자 ID
     * @return 찜 개수
     */
    public long getWishlistCount(UUID userId) {
        log.debug("Facade: 찜 개수 조회 - userId={}", userId);
        return queryService.getWishlistCount(userId);
    }
}