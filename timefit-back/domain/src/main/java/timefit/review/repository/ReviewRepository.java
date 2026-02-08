package timefit.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.review.entity.Review;

import java.util.Optional;
import java.util.UUID;

/**
 * Review Repository (JPA Data)
 *
 * 주요 기능:
 * - 기본 CRUD 및 간단한 조회
 * - Spring Data JPA 메서드 쿼리
 *
 * 참고:
 * - 복잡한 동적 쿼리는 ReviewQueryRepository 사용
 * - 평균 평점 계산, 평점 분포 등은 ReviewQueryRepository에서 처리
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    /**
     * ID로 활성 리뷰 조회 (Soft Delete 제외)
     *
     * @param id 리뷰 ID
     * @return Review (Optional)
     */
    Optional<Review> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * 업체별 활성 리뷰 목록 조회 (최신순)
     *
     * @param businessId 업체 ID
     * @param pageable 페이지네이션
     * @return 리뷰 목록 (Page)
     */
    Page<Review> findByBusinessIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID businessId, Pageable pageable);

    /**
     * 사용자별 활성 리뷰 목록 조회 (최신순)
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션
     * @return 리뷰 목록 (Page)
     */
    Page<Review> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * 예약별 리뷰 조회 (중복 작성 방지용)
     *
     * @param reservationId 예약 ID
     * @return Review (Optional)
     */
    Optional<Review> findByReservationId(UUID reservationId);

    /**
     * 예약에 리뷰가 존재하는지 확인
     *
     * @param reservationId 예약 ID
     * @return 리뷰 존재 여부
     */
    boolean existsByReservationId(UUID reservationId);

    /**
     * 업체별 활성 리뷰 개수
     *
     * @param businessId 업체 ID
     * @return 리뷰 개수
     */
    long countByBusinessIdAndDeletedAtIsNull(UUID businessId);

    /**
     * 사용자별 활성 리뷰 개수
     *
     * @param userId 사용자 ID
     * @return 리뷰 개수
     */
    long countByUserIdAndDeletedAtIsNull(UUID userId);
}