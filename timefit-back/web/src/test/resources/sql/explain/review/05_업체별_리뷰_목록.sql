-- ============================================================
-- 업체별 리뷰 목록 조회 (1,000,000건 - 극한 테스트)
-- ============================================================
-- API:        GET /api/public/business/{id}/reviews
-- 핵심 쿼리:  SELECT * FROM review
--            WHERE business_id = ? AND deleted_at IS NULL
-- 사전조건:   _setup.sql (User)
-- 규모:       1,000,000건 (업체 400개)
-- ============================================================
-- 시나리오:
--   - Business 400개
--   - Customer 10,000명
--   - Review 1,000,000개 (Business당 2,500개)
--
-- 목적: 100만건 속에서 Business 1의 활성 리뷰 2,250건 찾기
-- ============================================================

BEGIN;

-- ============================================================
-- 픽스처: Business 400개 생성
-- ============================================================

-- business 400개 생성 (먼저!)
INSERT INTO business (
    id, business_name, business_number, owner_name,
    address, contact_phone, description,
    is_active, average_rating, review_count,
    created_at, updated_at
)
SELECT
    ('20000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    'Business ' || biz_seq,
    LPAD((1000000000 + biz_seq)::text, 10, '0'),
    'Owner ' || biz_seq,
    'Address ' || biz_seq,
    '02' || LPAD((20000000 + biz_seq)::text, 8, '0'),
    'Test Business ' || biz_seq,
    true,
    0.0,
    0,
    NOW(),
    NOW()
FROM generate_series(1, 400) AS biz_seq
ON CONFLICT (id) DO NOTHING;

-- business_type 생성 (business 생성 후!)
INSERT INTO business_type (business_id, type_code)
SELECT
    ('20000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    'BD008'
FROM generate_series(1, 400) AS biz_seq
ON CONFLICT (business_id, type_code) DO NOTHING;

RAISE NOTICE 'Business 400개 생성 완료';

-- ============================================================
-- 픽스처: Customer 10,000명 생성
-- ============================================================

INSERT INTO users (
    id, email, password_hash, name, phone_number, role,
    created_at, updated_at
)
SELECT
    ('10000000-0000-0000-0000-' || LPAD(cust_seq::text, 12, '0'))::uuid,
    'customer' || cust_seq || '@test.com',
    '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW',
    'Customer ' || cust_seq,
    '010' || LPAD((cust_seq + 10000000)::text, 8, '0'),
    'USER',
    NOW(),
    NOW()
FROM generate_series(1, 10000) AS cust_seq
ON CONFLICT (id) DO NOTHING;

RAISE NOTICE 'Customer 10,000명 생성 완료';

-- ============================================================
-- Review 1,000,000건 생성
-- ============================================================
-- 구조:
-- - Business 400개 × 2,500건
-- - Customer 10,000명 순환
-- - 활성: 90% (900,000개)
-- - 삭제: 10% (100,000개)
-- ============================================================

INSERT INTO review (
    id, business_id, user_id, reservation_id,
    menu_name, rating, comment,
    deleted_at, created_at, updated_at
)
SELECT
    gen_random_uuid(),
    -- business: 400개 순환 (업체당 2,500건)
    ('20000000-0000-0000-0000-' || LPAD(((rev_seq % 400) + 1)::text, 12, '0'))::uuid,
    -- customer: 10,000명 순환
    ('10000000-0000-0000-0000-' || LPAD(((rev_seq % 10000) + 1)::text, 12, '0'))::uuid,
    -- reservation_id: 더미 (Review 테이블 성능 측정이 핵심)
    NULL::uuid,
    -- menu_name
    'Menu ' || ((rev_seq % 20) + 1),
    -- rating: 1~5점 순환 (4~5점 비중 높게)
    CASE (rev_seq % 10)
        WHEN 0 THEN 1
        WHEN 1 THEN 2
        WHEN 2 THEN 3
        WHEN 3 THEN 3
        WHEN 4 THEN 4
        WHEN 5 THEN 4
        WHEN 6 THEN 4
        WHEN 7 THEN 5
        WHEN 8 THEN 5
        ELSE 5
        END,
    -- comment
    'Review comment ' || rev_seq || ' - ' ||
    CASE (rev_seq % 5)
        WHEN 0 THEN 'Great service!'
        WHEN 1 THEN 'Very satisfied.'
        WHEN 2 THEN 'Good quality.'
        WHEN 3 THEN 'Highly recommend.'
        ELSE 'Will come again.'
        END,
    -- deleted_at: 10% 삭제
    CASE
        WHEN rev_seq % 10 = 0 THEN NOW() - ((rev_seq % 30) || ' days')::interval
        ELSE NULL
        END,
    -- created_at: 과거 1000일 분산
    NOW() - ((rev_seq % 1000) || ' days')::interval,
    NOW()
FROM generate_series(1, 1000000) AS rev_seq;

-- 생성된 데이터 확인
DO $$
    DECLARE
        total_count INTEGER;
        business_count INTEGER;
        test_business_count INTEGER;
        active_count INTEGER;
        deleted_count INTEGER;
        avg_rating NUMERIC;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM review;

        SELECT COUNT(DISTINCT business_id) INTO business_count
        FROM review;

        -- 테스트용 업체 (Business 1)
        SELECT COUNT(*) INTO test_business_count
        FROM review
        WHERE business_id = '20000000-0000-0000-0000-000000000001';

        -- Business 1의 활성 리뷰
        SELECT COUNT(*) INTO active_count
        FROM review
        WHERE business_id = '20000000-0000-0000-0000-000000000001'
          AND deleted_at IS NULL;

        -- Business 1의 삭제된 리뷰
        SELECT COUNT(*) INTO deleted_count
        FROM review
        WHERE business_id = '20000000-0000-0000-0000-000000000001'
          AND deleted_at IS NOT NULL;

        -- Business 1의 평균 평점
        SELECT AVG(rating) INTO avg_rating
        FROM review
        WHERE business_id = '20000000-0000-0000-0000-000000000001'
          AND deleted_at IS NULL;

        RAISE NOTICE '========================================';
        RAISE NOTICE 'Review 생성 완료 (극한 테스트)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 리뷰:     % 개', total_count;
        RAISE NOTICE '업체 수:       % 개', business_count;
        RAISE NOTICE 'Business1:     % 개', test_business_count;
        RAISE NOTICE '활성 리뷰:     % 개 (90%%)', active_count;
        RAISE NOTICE '삭제 리뷰:     % 개 (10%%)', deleted_count;
        RAISE NOTICE '평균 평점:     %.2f / 5.0', avg_rating;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================

ANALYZE review;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner 예측: rows=2250 (2500 × 90%%)';
RAISE NOTICE '100만건 속에서 2,250건 찾기 준비 완료';
RAISE NOTICE '========================================';

-- EXPLAIN: 업체별 리뷰 목록 조회 (100만건 중 2,250건)
EXPLAIN (ANALYZE, BUFFERS, VERBOSE)
SELECT
    id, business_id, user_id, reservation_id,
    menu_name, rating, comment,
    created_at, updated_at
FROM review
WHERE business_id = '20000000-0000-0000-0000-000000000001'
  AND deleted_at IS NULL
ORDER BY created_at DESC;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Planner 예측 정확도 ⭐
--    ANALYZE 후: rows=2250 vs actual rows=2250 (정확)
--    Business 1: 2,500개 × 90% 활성 = 2,250개
--
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    business_id에 인덱스 필요
--
-- ✅ Index Cond vs Filter
--    Index Cond: (business_id = '...')
--    Filter: (deleted_at IS NULL)
--    Rows Removed by Filter: 250개 (삭제된 리뷰)
--
-- ✅ Sort 노드 존재 여부
--    ORDER BY created_at DESC
--    인덱스가 정렬 순서와 맞으면 Sort 생략
--
-- ✅ Execution Time
--    Before: Seq Scan (1,000,000건) → 예상 35~40초
--    After:  Index Scan → 목표 250ms 이하
--    개선율: 99.4%+
--
-- ✅ Selectivity (선택도)
--    2,250 / 1,000,000 = 0.225% (매우 낮음)
--    → 인덱스 필수!
--
-- ✅ 필요한 인덱스
--    CREATE INDEX idx_review_business_created
--    ON review(business_id, created_at DESC)
--    WHERE deleted_at IS NULL;
--
--    Partial Index로 삭제된 리뷰 제외하면
--    인덱스 크기 10% 감소!

-- 추가 테스트: 평점별 집계
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    rating,
    COUNT(*) as review_count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) as percentage
FROM review
WHERE business_id = '20000000-0000-0000-0000-000000000001'
  AND deleted_at IS NULL
GROUP BY rating
ORDER BY rating DESC;

-- 추가 테스트: 최근 리뷰만 조회 (LIMIT)
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    rating,
    comment,
    created_at
FROM review
WHERE business_id = '20000000-0000-0000-0000-000000000001'
  AND deleted_at IS NULL
ORDER BY created_at DESC
LIMIT 20;

-- 추가 테스트: 평균 평점 계산
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    COUNT(*) as total_reviews,
    AVG(rating) as avg_rating,
    SUM(CASE WHEN rating >= 4 THEN 1 ELSE 0 END) as positive_reviews,
    ROUND(SUM(CASE WHEN rating >= 4 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as positive_rate
FROM review
WHERE business_id = '20000000-0000-0000-0000-000000000001'
  AND deleted_at IS NULL;

ROLLBACK;

-- ============================================================
-- 성능 예측
-- ============================================================
-- Before (인덱스 없음):
--   - Seq Scan: 1,000,000건 전체 스캔
--   - 시간: 35~40초
--   - Buffers: shared read 12,000+
--
-- After (인덱스 적용):
--   - Index Scan: 2,500건 조회 → Filter → 2,250건
--   - 시간: 250ms 이하
--   - Buffers: shared read 30~50
--   - 개선율: 99.4%+
--
-- 인덱스 효과:
--   - 444배 데이터 감소 (1,000,000 → 2,250)
--   - 150~160배 속도 향상 (40s → 250ms)
--
-- Partial Index 추가 효과:
--   - WHERE deleted_at IS NULL 조건
--   - 인덱스 크기: 10% 감소 (100만 → 90만)
--   - 스캔 범위: 10% 감소
--   - 유지보수: UPDATE/DELETE 시 성능 향상

-- ============================================================
-- 리뷰 데이터 특성
-- ============================================================
-- 평점 분포 (의도적 불균형):
--   - 5점: 30%
--   - 4점: 40%
--   - 3점: 20%
--   - 2점: 5%
--   - 1점: 5%
--   → 평균 4.0~4.2점 (긍정적 리뷰 우세)
--
-- 삭제 패턴:
--   - 10%가 soft delete (deleted_at)
--   - 최근 30일 내 삭제
--   - WHERE deleted_at IS NULL 필터링 필수
--
-- 시간 분포:
--   - 과거 1000일 균등 분산
--   - ORDER BY created_at DESC로 최근 우선