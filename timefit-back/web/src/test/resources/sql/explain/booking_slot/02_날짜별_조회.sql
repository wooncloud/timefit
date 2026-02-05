-- ============================================================
-- 날짜별 슬롯 조회
-- ============================================================
-- API:        GET /api/business/{id}/booking-slot?date={}
-- 핵심 쿼리:  SELECT * FROM booking_slot 
--            WHERE business_id = ? AND slot_date = ?
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

-- booking_slot 300건 생성 (30일 × 10슬롯/일)
-- 이유: WHERE business_id AND slot_date (비-PK 조건) → 테이블이 커야 Index Scan 등장
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
SELECT 
    gen_random_uuid(),
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000400',
    CURRENT_DATE + (day_offset || ' days')::interval,
    ('09:00:00'::time + (slot_offset || ' hours')::interval),
    3,
    3,
    true,
    NOW(),
    NOW()
FROM generate_series(0, 29) AS day_offset,
     generate_series(0, 9) AS slot_offset;

-- ============================================================
-- 🔍 EXPLAIN: 특정 날짜의 슬롯 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT 
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
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date = CURRENT_DATE + INTERVAL '7 days'
  AND is_active = true
ORDER BY slot_time ASC;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    - (business_id, slot_date) 복합 인덱스가 있으면 Index/Bitmap 선택
--    - 없으면 Seq Scan + Filter
--
-- ✅ Filter vs Index Cond 구분
--    - Index Cond: (business_id = '...' AND slot_date = '...')
--    - Filter: (is_active = true)
--
-- ✅ Sort 노드 존재 여부
--    - ORDER BY slot_time
--    - 인덱스에 slot_time까지 포함되어 있으면 Sort 생략 가능
--
-- ✅ actual rows (약 10건 예상) vs estimated rows
--    - 특정 날짜의 슬롯 수 (시간대별로 10개)

ROLLBACK;
