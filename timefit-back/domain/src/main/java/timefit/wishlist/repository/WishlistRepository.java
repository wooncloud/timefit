package timefit.wishlist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.wishlist.entity.Wishlist;

import java.util.Optional;
import java.util.UUID;

/**
 * Wishlist Repository
 *
 * 주요 기능:
 * - 사용자별 찜 목록 조회 (최신순)
 * - 찜 중복 확인
 * - 찜 삭제
 * - 찜 개수 조회
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {

    /**
     * 사용자의 찜 목록 조회 (최신순 정렬)
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션 정보
     * @return 찜 목록 (Page)
     */
    Page<Wishlist> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * 특정 사용자가 특정 메뉴를 찜했는지 확인
     *
     * @param userId 사용자 ID
     * @param businessId 메뉴 ID
     * @return 찜 존재 여부
     */
    boolean existsByUserIdAndBusinessId(UUID userId, UUID businessId);

    /**
     * 특정 사용자의 특정 메뉴 찜 조회
     *
     * @param userId 사용자 ID
     * @param businessId 메뉴 ID
     * @return Wishlist (Optional)
     */
    Optional<Wishlist> findByUserIdAndBusinessId(UUID userId, UUID businessId);

    /**
     * 특정 사용자의 특정 메뉴 찜 삭제
     *
     * @param userId 사용자 ID
     * @param businessId 메뉴 ID
     * @return 삭제된 행 수
     */
    int deleteByUserIdAndBusinessId(UUID userId, UUID businessId);

    /**
     * 특정 사용자의 찜 개수 조회
     *
     * @param userId 사용자 ID
     * @return 찜 개수
     */
    long countByUserId(UUID userId);
}