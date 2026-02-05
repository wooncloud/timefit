-- ============================================================
-- 기간별 슬롯 조회
-- ============================================================
-- API:        GET /api/business/{id}/booking-slot/range
-- 핵심 쿼리:  SELECT * FROM booking_slot 
--            WHERE business_id = ? AND slot_date BETWEEN ? AND ?
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
-- 이유: WHERE business_id AND slot_date BETWEEN (비-PK 조건) → 테이블이 커야 Index Scan
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
-- 🔍 EXPLAIN: 7일간의 슬롯 조회
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
  AND slot_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
  AND is_active = true
ORDER BY slot_date ASC, slot_time ASC;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Index Scan vs Bitmap Index Scan
--    - BETWEEN 조건은 범위 검색
--    - (business_id, slot_date) 복합 인덱스가 있으면 Index Range Scan
--
-- ✅ Index Cond vs Filter
--    - Index Cond: (business_id = '...' AND slot_date >= ... AND slot_date <= ...)
--    - Filter: (is_active = true)
--
-- ✅ Sort 노드 존재 여부
--    - ORDER BY slot_date, slot_time
--    - 인덱스 순서와 일치하면 Sort 생략 가능
--
-- ✅ actual rows (약 70건 예상) vs estimated rows
--    - 7일 × 10슬롯/일 = 70건
--    - Planner가 BETWEEN 범위를 정확히 예측하는지 확인
--
-- ✅ Buffers: shared read vs hit
--    - 범위 검색이므로 여러 페이지 접근
--    - 캐시 효율성 확인 (hit 비율)

ROLLBACK;
