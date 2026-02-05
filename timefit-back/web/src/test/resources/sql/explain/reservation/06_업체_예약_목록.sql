-- ============================================================
-- 업체 예약 목록 조회
-- ============================================================
-- API:        GET /api/business/{id}/reservations
-- 핵심 쿼리:  SELECT * FROM reservation WHERE business_id = ?
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

-- reservation 500건 생성
-- 이유: WHERE business_id (비-PK 조건) → 테이블이 커야 Index Scan 등장
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
SELECT 
    gen_random_uuid(),
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000001',
    '99999999-0000-0000-0000-000000000400',
    '99999999-0000-0000-0000-000000000500',
    CURRENT_DATE + (day_offset || ' days')::interval,
    '10:00:00'::time,
    CASE 
        WHEN day_offset < -20 THEN 'COMPLETED'
        WHEN day_offset < -10 THEN 'CONFIRMED'
        WHEN day_offset < 0 THEN 'PENDING'
        ELSE 'PENDING'
    END,
    50000,
    'Test Service',
    60,
    NOW() - (day_offset || ' days')::interval,
    NOW()
FROM generate_series(-50, 449) AS day_offset;

-- ============================================================
-- 🔍 EXPLAIN: 업체 예약 목록 조회 (PENDING 상태만)
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
    created_at,
    updated_at
FROM reservation
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND status = 'PENDING'
ORDER BY reservation_date ASC, reservation_time ASC;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    - business_id에 인덱스가 있으면 Index/Bitmap 선택
--    - (business_id, status) 복합 인덱스면 더 효율적
--
-- ✅ Index Cond vs Filter
--    - Index Cond: (business_id = '...')
--    - Filter: (status = 'PENDING')
--    - status가 인덱스에 없으면 Filter로 처리
--
-- ✅ Sort 노드 존재 여부
--    - ORDER BY reservation_date, reservation_time
--    - 인덱스 순서와 다르면 Sort 노드 등장
--
-- ✅ actual rows vs estimated rows
--    - PENDING 상태 약 460건 예상 (미래 + 최근 과거)
--    - Planner가 status 분포를 정확히 예측하는지 확인
--
-- ✅ Buffers: shared read vs hit
--    - 대량 조회이므로 여러 페이지 접근
--    - 캐시 효율성 확인

ROLLBACK;
