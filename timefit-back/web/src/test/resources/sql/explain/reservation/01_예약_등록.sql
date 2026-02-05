-- ============================================================
-- 예약 등록
-- ============================================================
-- API:        POST /api/reservation
-- 핵심 쿼리:  INSERT INTO reservation
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

-- ============================================================
-- 🔍 EXPLAIN: 예약 등록
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
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
    gen_random_uuid(),
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
-- 💡 확인 포인트
-- ============================================================
-- ✅ INSERT 기본 cost
-- ✅ FK 제약 확인 비용 (4개)
--    - business_id → business 테이블
--    - customer_id → users 테이블
--    - menu_id → menu 테이블
--    - booking_slot_id → booking_slot 테이블
-- ✅ 인덱스 생성 비용
--    - PK 인덱스 + 여러 FK 인덱스들
-- ✅ Buffers: FK 확인 시 참조 테이블들도 읽음
--    - 4개 테이블 접근 → shared hit 증가
--
-- 참고: 실제 API에서는 동시성 제어
--       booking_slot.remaining_capacity 감소 (별도 UPDATE)

ROLLBACK;
