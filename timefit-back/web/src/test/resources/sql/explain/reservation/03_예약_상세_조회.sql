-- ============================================================
-- 예약 상세 조회
-- ============================================================
-- API:        GET /api/reservation/{rid}
-- 핵심 쿼리:  SELECT * FROM reservation WHERE id = ?
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
-- 🔍 EXPLAIN: 예약 상세 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT 
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
    customer_name,
    customer_phone,
    customer_request,
    admin_memo,
    created_at,
    updated_at
FROM reservation
WHERE id = '99999999-0000-0000-0000-000000000600';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: 항상 Index Scan using reservation_pkey
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
