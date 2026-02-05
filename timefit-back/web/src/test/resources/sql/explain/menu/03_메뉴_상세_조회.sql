-- ============================================================
-- 메뉴 상세 조회
-- ============================================================
-- API:        GET /api/business/{id}/menu/{mid}
-- 핵심 쿼리:  SELECT * FROM menu WHERE id = ?
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

-- menu 1건만
-- 이유: WHERE id (PK 조건) → 테이블 크기 무관하게 Index Scan
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
VALUES (
    '99999999-0000-0000-0000-000000000400',
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000300',
    'Test Service',
    'Test Description',
    50000,
    60,
    'RESERVATION_BASED',
    true,
    NOW(),
    NOW()
);

-- ============================================================
-- 🔍 EXPLAIN: 메뉴 상세 조회
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
WHERE id = '99999999-0000-0000-0000-000000000400';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: 항상 Index Scan using menu_pkey
--    - PK 조건은 테이블 크기와 무관하게 Index Scan 선택
--
-- ✅ Index Cond: (id = '...')
--    - 조건이 인덱스로 직접 처리됨
--
-- ✅ actual rows = 1 (정확히 1건)
--    - PK는 unique하므로 Planner가 정확히 예측
--
-- ✅ cost가 매우 낮음 (8~10 범위)
--    - 인덱스 루트 → 리프 → 테이블 접근

ROLLBACK;
