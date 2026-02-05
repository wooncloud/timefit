-- ============================================================
-- 향후 슬롯 조회
-- ============================================================
-- API:        GET /api/business/{id}/booking-slot/upcoming
-- 핵심 쿼리:  SELECT * FROM booking_slot 
--            WHERE business_id = ? AND slot_date >= CURRENT_DATE
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

-- booking_slot 400건 생성 (과거 10일 + 미래 30일, 각 10슬롯/일)
-- 이유: WHERE slot_date >= (비-PK 조건) → 테이블이 커야 Index Scan 등장
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
FROM generate_series(-10, 29) AS day_offset,  -- 과거 10일 + 미래 30일
     generate_series(0, 9) AS slot_offset;

-- ============================================================
-- 🔍 EXPLAIN: 향후 슬롯 조회 (오늘 포함)
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
  AND slot_date >= CURRENT_DATE
  AND is_active = true
ORDER BY slot_date ASC, slot_time ASC
LIMIT 50;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Index Scan vs Bitmap Index Scan
--    - (business_id, slot_date) 복합 인덱스가 있으면 Index Range Scan
--    - slot_date >= 조건은 범위 검색
--
-- ✅ Index Cond vs Filter
--    - Index Cond: (business_id = '...' AND slot_date >= ...)
--    - Filter: (is_active = true)
--
-- ✅ LIMIT 노드 존재 여부
--    - LIMIT 50이 있으면 상위 노드로 Limit 등장
--    - Index Scan이라면 50건만 읽고 중단 (효율적)
--
-- ✅ Sort 노드 vs Index 순서
--    - ORDER BY slot_date, slot_time
--    - 인덱스가 (business_id, slot_date, slot_time) 순이면 Sort 생략
--    - 아니면 Sort 노드 등장
--
-- ✅ actual rows (50건) vs estimated rows
--    - LIMIT으로 인해 실제 읽은 행 수 확인
--    - 300건 중 50건만 반환 (과거 100건 제외)
--
-- ✅ Buffers 효율성
--    - Index Scan + LIMIT이면 최소한의 페이지만 읽음
--    - Seq Scan이면 전체 테이블 스캔 후 LIMIT

ROLLBACK;
