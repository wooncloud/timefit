package timefit.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import timefit.review.entity.Review;

import java.util.Map;
import java.util.UUID;

/**
 * Review Query Repository (QueryDSL 전용)
 *
 * 복잡한 동적 쿼리 및 집계 쿼리 처리
 * - 평점별 필터링
 * - 평점 분포 조회
 * - 평균 평점 계산
 * - 동적 검색 조건
 */
public interface ReviewQueryRepository {

    /**
     * 업체별 리뷰 조회 (평점 필터링)
     *
     * @param businessId 업체 ID
     * @param rating 평점 필터 (nullable, null이면 전체 조회)
     * @param pageable 페이지네이션
     * @return 리뷰 목록 (Page)
     */
    Page<Review> findByBusinessIdWithRatingFilter(
            UUID businessId,
            Integer rating,
            Pageable pageable
    );

    /**
     * 업체별 평점 분포 조회
     *
     * 예: {5: 100개, 4: 50개, 3: 20개, 2: 5개, 1: 2개}
     *
     * @param businessId 업체 ID
     * @return Map<평점, 개수>
     */
    Map<Integer, Long> getRatingDistributionByBusinessId(UUID businessId);

    /**
     * 업체별 평균 평점 계산 (QueryDSL)
     * DB에서 AVG 함수를 사용하여 계산
     *
     * @param businessId 업체 ID
     * @return 평균 평점 (null이면 리뷰 없음)
     */
    Double calculateAverageRatingByBusinessId(UUID businessId);
}