-- ============================================================
-- 카테고리별 메뉴 목록 조회 (1,000,000건 - 극한 테스트)
-- ============================================================
-- API:        GET /api/business/{id}/category/{cid}/menus
-- 핵심 쿼리:  SELECT * FROM menu
--            WHERE business_category_id = ?
-- 사전조건:   _setup.sql (User)
-- 규모:       1,000,000건 (카테고리 4,000개)
-- ============================================================
-- 시나리오:
--   - Business 400개
--   - Category 4,000개 (Business당 10개)
--   - Menu 1,000,000개 (Category당 250개)
--
-- 목적: 100만건 속에서 Category 1의 메뉴 250건 찾기
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
        category_count INTEGER;
        test_category_count INTEGER;
        active_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM menu;

        SELECT COUNT(DISTINCT business_category_id) INTO category_count
        FROM menu;

        -- 테스트용 카테고리 (Business 1, Category 1)
        SELECT COUNT(*) INTO test_category_count
        FROM menu
        WHERE business_category_id = '50000000-0000-0001-0001-000000000000';

        -- Category 1의 활성 메뉴
        SELECT COUNT(*) INTO active_count
        FROM menu
        WHERE business_category_id = '50000000-0000-0001-0001-000000000000'
          AND is_active = true;

        RAISE NOTICE '========================================';
        RAISE NOTICE 'Menu 생성 완료 (극한 테스트)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 메뉴:     % 개', total_count;
        RAISE NOTICE '카테고리 수:   % 개', category_count;
        RAISE NOTICE 'Category1:     % 개', test_category_count;
        RAISE NOTICE '활성 메뉴:     % 개 (90%%)', active_count;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================

ANALYZE menu;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner 예측: rows=225 (250 × 90%%)';
RAISE NOTICE '100만건 속에서 225건 찾기 준비 완료';
RAISE NOTICE '========================================';

-- EXPLAIN: 카테고리별 메뉴 목록 조회 (100만건 중 225건)
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
WHERE business_category_id = '50000000-0000-0001-0001-000000000000'
  AND is_active = true
ORDER BY service_name ASC;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Planner 예측 정확도 ⭐
--    ANALYZE 후: rows=225 vs actual rows=225 (정확)
--    Category 1: 250개 × 90% 활성 = 225개
--
-- ✅ Scan 타입: Seq Scan vs Index Scan
--    business_category_id에 FK 인덱스 자동 생성 여부 확인
--
-- ✅ Filter vs Index Cond
--    Index Cond: (business_category_id = '...')
--    Filter: (is_active = true)
--
-- ✅ Sort 노드
--    ORDER BY service_name ASC
--    인덱스 순서와 다르면 Sort 필요
--
-- ✅ Execution Time
--    Before: Seq Scan (1,000,000건) → 예상 35~40초
--    After:  Index Scan → 목표 150ms 이하
--    개선율: 99.6%+
--
-- ✅ Selectivity
--    225 / 1,000,000 = 0.0225% (극도로 낮음)
--    → 인덱스 필수!
--
-- ✅ 필요한 인덱스
--    CREATE INDEX idx_menu_category_name
--    ON menu(business_category_id, service_name)
--    WHERE is_active = true;

-- 추가 테스트: 가격대별 메뉴
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    CASE
        WHEN price < 40000 THEN '저가'
        WHEN price < 70000 THEN '중가'
        ELSE '고가'
        END as price_range,
    COUNT(*) as menu_count,
    AVG(price) as avg_price
FROM menu
WHERE business_category_id = '50000000-0000-0001-0001-000000000000'
  AND is_active = true
GROUP BY
    CASE
        WHEN price < 40000 THEN '저가'
        WHEN price < 70000 THEN '중가'
        ELSE '고가'
        END;

-- 추가 테스트: 주문 유형별 개수
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    order_type,
    COUNT(*) as count,
    AVG(duration_minutes) as avg_duration
FROM menu
WHERE business_category_id = '50000000-0000-0001-0001-000000000000'
  AND is_active = true
GROUP BY order_type;

ROLLBACK;

-- ============================================================
-- 성능 예측
-- ============================================================
-- Before (인덱스 없음):
--   - Seq Scan: 1,000,000건 전체 스캔
--   - 시간: 35~40초
--
-- After (인덱스 적용):
--   - Index Scan: 250건 조회 → Filter → 225건
--   - 시간: 150ms 이하
--   - 개선율: 99.6%+
--
-- 인덱스 효과:
--   - 4,444배 데이터 감소 (1,000,000 → 225)
--   - 250~270배 속도 향상 (40s → 150ms)