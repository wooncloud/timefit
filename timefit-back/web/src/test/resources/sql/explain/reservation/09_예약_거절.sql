-- ============================================================
-- 예약 거절
-- ============================================================
-- API:        POST /api/business/{id}/reservation/{rid}/reject
-- 핵심 쿼리:  UPDATE reservation SET status = 'REJECTED' WHERE id = ?
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

-- booking_slot 1건 (체인 부모)
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
    2,
    true,
    NOW(),
    NOW()
);

-- reservation 1건만 (PENDING 상태)
-- 이유: WHERE id (PK 조건) → 테이블 크기 무관하게 Index Scan
INSERT INTO reservation (
    id,
    business_id,
    customer_id,
    menu_id,
    booking_slot_id,
    reservation_date,
    reservation_time,
    status,
    total_price,
    snapshot_service_name,
    snapshot_duration_minutes,
    created_at,
    updated_at
)
VALUES (
    '99999999-0000-0000-0000-000000000600',
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000001',
    '99999999-0000-0000-0000-000000000400',
    '99999999-0000-0000-0000-000000000500',
    CURRENT_DATE + INTERVAL '1 day',
    '10:00:00'::time,
    'PENDING',
    50000,
    'Test Service',
    60,
    NOW(),
    NOW()
);

-- ============================================================
-- 🔍 EXPLAIN: 예약 거절
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
UPDATE reservation
SET 
    status = 'REJECTED',
    updated_at = NOW()
WHERE id = '99999999-0000-0000-0000-000000000600';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Update on reservation
--                 -> Index Scan using reservation_pkey
--
-- ✅ Index Scan의 Index Cond: (id = '...')
--    - PK로 단건 조회
--
-- ✅ actual rows = 0 (Update 노드)
--    - Index Scan의 actual rows = 1 확인
--
-- ✅ Buffers: dirtied
--    - status 컬럼 변경
--
-- ✅ 상태 전이 확인
--    - PENDING → REJECTED
--    - Application 레이어에서 상태 전이 검증
--
-- 참고: 실제 API에서는 추가 로직
--       - booking_slot.remaining_capacity 증가 (자리 반환)

ROLLBACK;
