-- ============================================================
-- 예약 수정
-- ============================================================
-- API:        PUT /api/reservation/{rid}
-- 핵심 쿼리:  UPDATE reservation WHERE id = ?
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

-- booking_slot 2건 (기존 슬롯 + 변경할 슬롯)
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
VALUES 
(
    '99999999-0000-0000-0000-000000000500',
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000400',
    CURRENT_DATE + INTERVAL '1 day',
    '10:00:00'::time,
    3,
    2,  -- 1건 예약됨
    true,
    NOW(),
    NOW()
),
(
    '99999999-0000-0000-0000-000000000501',
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000400',
    CURRENT_DATE + INTERVAL '2 days',
    '14:00:00'::time,
    3,
    3,
    true,
    NOW(),
    NOW()
);

-- reservation 1건만
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
    customer_request,
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
    'Original request',
    NOW(),
    NOW()
);

-- ============================================================
-- 🔍 EXPLAIN: 예약 수정
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
UPDATE reservation
SET 
    booking_slot_id = '99999999-0000-0000-0000-000000000501',
    reservation_date = CURRENT_DATE + INTERVAL '2 days',
    reservation_time = '14:00:00'::time,
    customer_request = 'Updated request',
    updated_at = NOW()
WHERE id = '99999999-0000-0000-0000-000000000600';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Update on reservation
--                 -> Index Scan using reservation_pkey
--    - Update 노드: "변경 적용"
--    - Index Scan: "행을 어떻게 찾는가" (여기가 핵심)
--
-- ✅ Index Scan의 Index Cond: (id = '...')
--    - PK로 단건 조회, 테이블 크기 무관
--
-- ✅ actual rows = 0 (Update 노드는 항상 0)
--    - UPDATE는 결과 행을 반환하지 않음
--    - 아래 Index Scan의 actual rows = 1 확인
--
-- ✅ Buffers: dirtied
--    - 여러 컬럼 변경 (booking_slot_id, dates, request)
--    - FK 인덱스들도 갱신 → 여러 페이지 dirtied
--
-- 참고: 실제 API에서는 추가 로직
--       - 기존 booking_slot.remaining_capacity 증가
--       - 새 booking_slot.remaining_capacity 감소

ROLLBACK;
