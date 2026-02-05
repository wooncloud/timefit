-- ============================================================
-- 메뉴별 슬롯 조회
-- ============================================================
-- API:        GET /api/business/{id}/booking-slot/menu/{mid}
-- 핵심 쿼리:  SELECT * FROM booking_slot WHERE menu_id = ?
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
-- 이유: WHERE menu_id (비-PK 조건) → 테이블이 커야 Index Scan 등장
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
-- 🔍 EXPLAIN: 특정 메뉴의 향후 슬롯 조회
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
WHERE menu_id = '99999999-0000-0000-0000-000000000400'
  AND slot_date >= CURRENT_DATE
  AND is_active = true
ORDER BY slot_date ASC, slot_time ASC;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    - menu_id에 인덱스가 있으면 Index/Bitmap 선택
--    - 없으면 Seq Scan + Filter
--
-- ✅ Index Cond vs Filter
--    - Index Cond: (menu_id = '...')
--    - Filter: (slot_date >= ... AND is_active = true)
--    - slot_date가 인덱스에 포함되지 않으면 Filter로 처리
--
-- ✅ Sort 노드 존재 여부
--    - ORDER BY slot_date, slot_time
--    - 인덱스 순서와 다르면 Sort 노드 등장
--
-- ✅ actual rows (약 300건 예상) vs estimated rows
--    - 모든 슬롯이 해당 메뉴용이므로 전체 반환
--
-- ✅ FK 인덱스의 효과
--    - menu_id는 FK이므로 기본적으로 인덱스 존재 가능
--    - 하지만 정렬 순서는 (menu_id)만 있어서 Sort 필요

ROLLBACK;
