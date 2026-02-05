-- ============================================================
-- 메뉴 목록 조회
-- ============================================================
-- API:        GET /api/business/{id}/menu
-- 핵심 쿼리:  SELECT * FROM menu WHERE business_id = ?
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 🔧 픽스처
-- ============================================================
-- business_category 1건 (체인 부모)
INSERT INTO business_category (
    id, 
    business_id, 
    business_type, 
    category_name, 
    is_active, 
    created_at, 
    updated_at
)
VALUES (
    '99999999-0000-0000-0000-000000000300',
    '99999999-0000-0000-0000-000000000100',
    'BD008',
    'Test Category',
    true,
    NOW(),
    NOW()
);

-- menu 200건 생성
-- 이유: WHERE business_id (비-PK 조건) → 테이블이 커야 Index Scan 등장
INSERT INTO menu (
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
)
SELECT 
    gen_random_uuid(),
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000300',
    'Service ' || i,
    'Description ' || i,
    30000 + (i * 1000),
    60,
    'RESERVATION_BASED',
    (i % 10 != 0),  -- 10건마다 1건은 비활성
    NOW(),
    NOW()
FROM generate_series(1, 200) AS i;

-- ============================================================
-- 🔍 EXPLAIN: 메뉴 목록 조회 (활성만)
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
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
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND is_active = true
ORDER BY created_at DESC;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    - business_id에 인덱스가 있으면 Index/Bitmap 선택
--    - 없으면 Seq Scan + Filter
--
-- ✅ Filter vs Index Cond 구분
--    - Index Cond: (business_id = '...')  → 인덱스가 조건 처리
--    - Filter: (is_active = true)         → 읽은 후에 거르기
--    - Rows Removed by Filter 확인 (비활성 메뉴 수)
--
-- ✅ Sort 노드 존재 여부
--    - ORDER BY created_at DESC
--    - 인덱스가 정렬 순서와 맞으면 Sort 생략 가능
--
-- ✅ actual rows vs estimated rows
--    - 200건 중 활성 180건 예상
--    - Planner 예측이 정확한지 확인

ROLLBACK;
