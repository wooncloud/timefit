-- ============================================================
-- 메뉴 활성 토글
-- ============================================================
-- API:        PATCH /api/business/{id}/menu/{mid}/toggle
-- 핵심 쿼리:  UPDATE menu SET is_active = NOT is_active WHERE id = ?
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
-- 🔍 EXPLAIN: 메뉴 활성 토글
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
UPDATE menu
SET 
    is_active = NOT is_active,
    updated_at = NOW()
WHERE id = '99999999-0000-0000-0000-000000000400';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Update on menu
--                 -> Index Scan using menu_pkey
--
-- ✅ Index Scan의 Index Cond: (id = '...')
--    - PK로 단건 조회
--
-- ✅ actual rows = 0 (Update 노드)
--    - Index Scan의 actual rows = 1 확인
--
-- ✅ Buffers: dirtied 최소
--    - 컬럼 2개만 변경 (is_active, updated_at)
--    - 다른 컬럼 변경보다 cost 낮음
--
-- 비교: 04_메뉴_수정.sql과 비교하면
--       이 파일의 dirtied가 더 적을 것
--       (변경 컬럼이 적어서)

ROLLBACK;
