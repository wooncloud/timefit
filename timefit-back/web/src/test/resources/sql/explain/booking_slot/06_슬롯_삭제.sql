-- ============================================================
-- 슬롯 삭제
-- ============================================================
-- API:        DELETE /api/business/{id}/booking-slot/{sid}
-- 핵심 쿼리:  DELETE FROM booking_slot WHERE id = ?
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

-- booking_slot 1건만
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
    true,
    NOW(),
    NOW()
);

-- ============================================================
-- 🔍 EXPLAIN: 슬롯 삭제
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
DELETE FROM booking_slot
WHERE id = '99999999-0000-0000-0000-000000000500';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Delete on booking_slot
--                 -> Index Scan using booking_slot_pkey
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
--
-- ✅ FK 제약 체크 (reservation 테이블)
--    - reservation.booking_slot_id → booking_slot.id
--    - 참조하는 예약이 있으면 삭제 불가 (Application 레이어 체크)
--    - 또는 ON DELETE CASCADE/RESTRICT 설정에 따라 동작
--
-- 참고: 실제 API는 예약이 없는 슬롯만 삭제 가능

ROLLBACK;
