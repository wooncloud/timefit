-- ============================================================
-- 슬롯 생성
-- ============================================================
-- API:        POST /api/business/{id}/booking-slot
-- 핵심 쿼리:  INSERT INTO booking_slot
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

-- ============================================================
-- 🔍 EXPLAIN: 슬롯 생성
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
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
    gen_random_uuid(),
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
-- 💡 확인 포인트
-- ============================================================
-- ✅ INSERT 기본 cost
-- ✅ FK 제약 확인 비용
--    - business_id → business 테이블 참조 확인
--    - menu_id → menu 테이블 참조 확인
-- ✅ 인덱스 생성 비용
--    - PK 인덱스
--    - FK 인덱스들
--    - 복합 인덱스 (business_id, slot_date) 등
-- ✅ Buffers: FK 확인 시 참조 테이블도 읽음

ROLLBACK;
