-- ============================================================
-- 노쇼 처리
-- ============================================================
-- API:        POST /api/business/{id}/reservation/{rid}/no-show
-- 핵심 쿼리:  UPDATE reservation SET status = 'NO_SHOW' WHERE id = ?
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
    CURRENT_DATE - INTERVAL '1 day',  -- 어제 (노쇼 처리 가능)
    '10:00:00'::time,
    3,
    2,
    true,
    NOW(),
    NOW()
);

-- reservation 1건만 (CONFIRMED 상태)
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
    CURRENT_DATE - INTERVAL '1 day',
    '10:00:00'::time,
    'CONFIRMED',
    50000,
    'Test Service',
    60,
    NOW(),
    NOW()
);

-- ============================================================
-- 🔍 EXPLAIN: 노쇼 처리
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
UPDATE reservation
SET 
    status = 'NO_SHOW',
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
--    - CONFIRMED → NO_SHOW
--    - Application 레이어에서 상태 전이 검증
--    - 예약 시간이 지난 경우에만 처리 가능
--
-- 비교: 08_예약_승인, 09_예약_거절, 10_예약_완료와 비교
--       모두 동일한 패턴 (status 변경)
--       EXPLAIN 결과도 동일

ROLLBACK;
