-- ============================================================
-- 메뉴 삭제
-- ============================================================
-- API:        DELETE /api/business/{id}/menu/{mid}
-- 핵심 쿼리:  DELETE FROM menu WHERE id = ?
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
-- 🔍 EXPLAIN: 메뉴 삭제
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
DELETE FROM menu
WHERE id = '99999999-0000-0000-0000-000000000400';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Delete on menu
--                 -> Index Scan using menu_pkey
--    - Delete 노드: "삭제 적용"
--    - Index Scan: "행을 어떻게 찾는가" (여기가 핵심)
--
-- ✅ Index Scan의 Index Cond: (id = '...')
--    - PK로 단건 조회, 테이블 크기 무관
--
-- ✅ actual rows = 0 (Delete 노드는 항상 0)
--    - DELETE는 결과 행을 반환하지 않음
--    - 아래 Index Scan의 actual rows = 1 확인
--
-- ✅ Buffers: dirtied
--    - MVCC에서는 실제 삭제가 아닌 표시만
--    - 참조하는 테이블 확인 (booking_slot, wishlist)
--
-- ✅ FK 제약 체크
--    - booking_slot.menu_id → menu.id (ON DELETE CASCADE 또는 RESTRICT)
--    - wishlist.menu_id → menu.id
--    - FK 제약에 따라 추가 Buffers 발생 가능

ROLLBACK;
