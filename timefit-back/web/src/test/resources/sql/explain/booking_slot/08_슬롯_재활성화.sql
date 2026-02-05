-- ============================================================
-- 슬롯 재활성화
-- ============================================================
-- API:        PATCH /api/business/{id}/booking-slot/{sid}/activate
-- 핵심 쿼리:  UPDATE booking_slot SET is_active = true WHERE id = ?
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

-- menu 1건 (체인 부모)
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

-- booking_slot 1건만 (비활성 상태)
-- 이유: WHERE id (PK 조건) → 테이블 크기 무관하게 Index Scan
INSERT INTO booking_slot (
    id,
    business_id,
    menu_id,
    slot_date,
    slot_time,
    capacity,
    remaining_capacity,
    is_active,
    created_at,
    updated_at
)
VALUES (
    '99999999-0000-0000-0000-000000000500',
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000400',
    CURRENT_DATE + INTERVAL '1 day',
    '10:00:00'::time,
    3,
    3,
    false,  -- 비활성 상태
    NOW(),
    NOW()
);

-- ============================================================
-- 🔍 EXPLAIN: 슬롯 재활성화
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
UPDATE booking_slot
SET 
    is_active = true,
    updated_at = NOW()
WHERE id = '99999999-0000-0000-0000-000000000500';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Update on booking_slot
--                 -> Index Scan using booking_slot_pkey
--
-- ✅ Index Scan의 Index Cond: (id = '...')
--    - PK로 단건 조회
--
-- ✅ actual rows = 0 (Update 노드)
--    - Index Scan의 actual rows = 1 확인
--
-- ✅ Buffers: dirtied
--    - 07번 파일과 동일한 패턴 (is_active 변경)
--    - cost와 buffers가 거의 동일할 것
--
-- 비교: 07_슬롯_비활성화.sql과 EXPLAIN 결과 비교
--       is_active를 true → false vs false → true
--       두 작업의 cost가 동일한지 확인

ROLLBACK;
