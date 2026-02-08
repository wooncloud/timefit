-- ============================================================
-- 업체별 메뉴 목록 조회 (1,000,000건 - 극한 테스트)
-- ============================================================
-- API:        GET /api/business/{id}/menu
-- 핵심 쿼리:  SELECT * FROM menu WHERE business_id = ?
-- 사전조건:   _setup.sql (User)
-- 규모:       1,000,000건 (업체 400개)
-- ============================================================
-- 시나리오:
--   - Business 400개
--   - Business당 Category 10개 = 4,000개
--   - Category당 Menu 250개 = 1,000,000개
--
-- 목적: 100만건 속에서 Business 1의 메뉴 2,500건 찾기
-- ============================================================

BEGIN;

-- ============================================================
-- 픽스처: Business 400개 생성
-- ============================================================

-- business 400개 생성 (먼저 생성!)
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
-- 픽스처: BusinessCategory 4,000개 생성
-- ============================================================

INSERT INTO business_category (
    id, business_id, business_type, category_name,
    is_active, created_at, updated_at
)
SELECT
    ('50000000-0000-' || LPAD(biz_seq::text, 4, '0') || '-' ||
     LPAD(cat_seq::text, 4, '0') || '-000000000000')::uuid,
    ('20000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    'BD008',
    CASE (cat_seq % 10)
        WHEN 1 THEN '헤어'
        WHEN 2 THEN '네일'
        WHEN 3 THEN '피부'
        WHEN 4 THEN '메이크업'
        WHEN 5 THEN '속눈썹'
        WHEN 6 THEN '마사지'
        WHEN 7 THEN '왁싱'
        WHEN 8 THEN '태닝'
        WHEN 9 THEN '발관리'
        ELSE '기타'
        END || ' ' || cat_seq,
    true,
    NOW(),
    NOW()
FROM
    generate_series(1, 400) AS biz_seq,
    generate_series(1, 10) AS cat_seq
ON CONFLICT (id) DO NOTHING;

RAISE NOTICE 'BusinessCategory 4,000개 생성 완료';

-- ============================================================
-- Menu 1,000,000건 생성
-- ============================================================
-- 구조:
-- - Business 400개 × Category 10개 × Menu 250개
-- - Business당 2,500개
-- - Category당 250개
-- - 총 1,000,000개
-- ============================================================

INSERT INTO menu (
    id, business_id, business_category_id, service_name,
    description, price, duration_minutes, order_type,
    is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(),
    ('20000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    ('50000000-0000-' || LPAD(biz_seq::text, 4, '0') || '-' ||
     LPAD(cat_seq::text, 4, '0') || '-000000000000')::uuid,
    'Service ' || biz_seq || '-' || cat_seq || '-' || menu_seq,
    'Description ' || menu_seq,
    30000 + (cat_seq * 5000) + (menu_seq * 100),
    60,
    CASE
        WHEN menu_seq % 5 = 0 THEN 'ONDEMAND_BASED'
        ELSE 'RESERVATION_BASED'
        END,
    -- 90% 활성
    (biz_seq + cat_seq + menu_seq) % 10 != 0,
    NOW() - ((menu_seq % 365) || ' days')::interval,
    NOW()
FROM
    generate_series(1, 400) AS biz_seq,
    generate_series(1, 10) AS cat_seq,
    generate_series(1, 250) AS menu_seq;

-- 생성된 데이터 확인
DO $$
    DECLARE
        total_count INTEGER;
        business_count INTEGER;
        test_business_count INTEGER;
        active_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM menu;

        SELECT COUNT(DISTINCT business_id) INTO business_count
        FROM menu;

        -- 테스트용 업체 (Business 1)
        SELECT COUNT(*) INTO test_business_count
        FROM menu
        WHERE business_id = '20000000-0000-0000-0000-000000000001';

        -- Business 1의 활성 메뉴
        SELECT COUNT(*) INTO active_count
        FROM menu
        WHERE business_id = '20000000-0000-0000-0000-000000000001'
          AND is_active = true;

        RAISE NOTICE '========================================';
        RAISE NOTICE 'Menu 생성 완료 (극한 테스트)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 메뉴:     % 개', total_count;
        RAISE NOTICE '업체 수:       % 개', business_count;
        RAISE NOTICE 'Business1:     % 개', test_business_count;
        RAISE NOTICE '활성 메뉴:     % 개 (90%%)', active_count;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================

ANALYZE menu;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner 예측: rows=2250 (2500 × 90%%)';
RAISE NOTICE '100만건 속에서 2,250건 찾기 준비 완료';
RAISE NOTICE '========================================';

-- EXPLAIN: 업체별 메뉴 목록 조회 (100만건 중 2,250건)
EXPLAIN (ANALYZE, BUFFERS, VERBOSE)
SELECT
    id,
    business_id,
    business_category_id,
    service_name,
    description,
    price,
    duration_minutes,
    order_type,
    is_active,
    created_at,
    updated_at
FROM menu
WHERE business_id = '20000000-0000-0000-0000-000000000001'
  AND is_active = true
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
-- ✅ Filter vs Index Cond 구분
--    Index Cond: (business_id = '...')
--    Filter: (is_active = true)
--    Rows Removed by Filter: 250개 (비활성)
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
--    CREATE INDEX idx_menu_business_created
--    ON menu(business_id, created_at DESC)
--    WHERE is_active = true;

-- 추가 테스트: 카테고리별 개수
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    business_category_id,
    COUNT(*) as menu_count,
    SUM(CASE WHEN is_active THEN 1 ELSE 0 END) as active_count
FROM menu
WHERE business_id = '20000000-0000-0000-0000-000000000001'
GROUP BY business_category_id;

-- 추가 테스트: 가격 범위 검색
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    service_name,
    price,
    duration_minutes
FROM menu
WHERE business_id = '20000000-0000-0000-0000-000000000001'
  AND is_active = true
  AND price BETWEEN 30000 AND 50000
ORDER BY price ASC;

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