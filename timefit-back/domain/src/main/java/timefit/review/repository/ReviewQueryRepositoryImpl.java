package timefit.review.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import timefit.review.entity.Review;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static timefit.review.entity.QReview.review;

/**
 * Review Query Repository 구현체 (QueryDSL)
 *
 * 복잡한 쿼리 및 동적 쿼리 구현
 * - 평점별 필터링
 * - 평점 분포 조회
 * - 평균 평점 계산
 */
@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 업체별 리뷰 조회 (평점 필터링)
     */
    @Override
    public Page<Review> findByBusinessIdWithRatingFilter(
            UUID businessId,
            Integer rating,
            Pageable pageable) {

        // 데이터 조회
        List<Review> content = queryFactory
                .selectFrom(review)
                .where(
                        businessIdEq(businessId),
                        ratingEq(rating),
                        deletedAtIsNull()
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        businessIdEq(businessId),
                        ratingEq(rating),
                        deletedAtIsNull()
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 업체별 평점 분포 조회
     */
    @Override
    public Map<Integer, Long> getRatingDistributionByBusinessId(UUID businessId) {
        List<RatingCount> results = queryFactory
                .select(review.rating, review.count())
                .from(review)
                .where(
                        businessIdEq(businessId),
                        deletedAtIsNull()
                )
                .groupBy(review.rating)
                .fetch()
                .stream()
                .map(tuple -> new RatingCount(
                        tuple.get(review.rating),
                        tuple.get(review.count())
                ))
                .toList();

        // Map으로 변환 (1~5점 모두 포함, 없는 평점은 0으로)
        Map<Integer, Long> distribution = results.stream()
                .collect(Collectors.toMap(
                        RatingCount::rating,
                        RatingCount::count
                ));

        // 1~5점 모두 포함 (없는 평점은 0)
        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i, 0L);
        }

        return distribution;
    }

    /**
     * 업체별 평균 평점 계산 (QueryDSL)
     * DB에서 AVG 함수를 사용하여 계산
     */
    @Override
    public Double calculateAverageRatingByBusinessId(UUID businessId) {
        return queryFactory
                .select(review.rating.avg())
                .from(review)
                .where(
                        businessIdEq(businessId),
                        deletedAtIsNull()
                )
                .fetchOne();
    }

    // ---------------------- 동적 쿼리 조건

    private BooleanExpression businessIdEq(UUID businessId) {
        return businessId != null ? review.business.id.eq(businessId) : null;
    }

    private BooleanExpression ratingEq(Integer rating) {
        return rating != null ? review.rating.eq(rating) : null;
    }

    private BooleanExpression deletedAtIsNull() {
        return review.deletedAt.isNull();
    }

    // ---------------------- 내부 DTO

    /**
     * 평점-개수 매핑용 내부 record
     */
    private record RatingCount(Integer rating, Long count) {}
}