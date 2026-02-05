-- ============================================================
-- 영업시간 조회
-- ============================================================
-- API:        GET /api/business/{id}/operating-hours
-- 핵심 쿼리:  SELECT * FROM operating_hours WHERE business_id = ?
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 🔧 픽스처
-- ============================================================
-- operating_hours 700건 생성 (100개 업체 × 7일)
-- 이유: WHERE business_id (비-PK 조건) → 테이블이 커야 Index Scan 등장

-- 업체 100개 생성 (99999999-0000-0000-0000-00000010X 형식)
INSERT INTO business (
    id,
    business_name,
    business_number,
    owner_name,
    address,
    contact_phone,
    description,
    is_active,
    average_rating,
    review_count,
    created_at,
    updated_at
)
SELECT 
    ('99999999-0000-0000-0000-0000000010' || LPAD(i::text, 1, '0'))::uuid,
    'Test Business ' || i,
    '999999999' || i,
    'Test Owner ' || i,
    'Test Address ' || i,
    '0299999' || LPAD(i::text, 3, '0'),
    'Test Description',
    true,
    0.0,
    0,
    NOW(),
    NOW()
FROM generate_series(1, 100) AS i;

-- 각 업체의 영업시간 생성 (월~일 7일)
INSERT INTO operating_hours (
    id,
    business_id,
    day_of_week,
    open_time,
    close_time,
    is_closed,
    sequence,
    created_at,
    updated_at
)
SELECT 
    gen_random_uuid(),
    ('99999999-0000-0000-0000-0000000010' || LPAD(business_num::text, 1, '0'))::uuid,
    day_num,
    '09:00:00'::time,
    '18:00:00'::time,
    false,
    0,
    NOW(),
    NOW()
FROM generate_series(1, 100) AS business_num,
     generate_series(0, 6) AS day_num;

-- ============================================================
-- 🔍 EXPLAIN: 특정 업체의 영업시간 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT 
    id,
    business_id,
    day_of_week,
    open_time,
    close_time,
    is_closed,
    sequence,
    created_at,
    updated_at
FROM operating_hours
WHERE business_id = '99999999-0000-0000-0000-000000000100'
ORDER BY day_of_week ASC, sequence ASC;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    - business_id에 인덱스가 있으면 Index/Bitmap 선택
--    - 없으면 Seq Scan + Filter
--
-- ✅ Index Cond: (business_id = '...')
--    - 인덱스가 조건 처리
--
-- ✅ Sort 노드 존재 여부
--    - ORDER BY day_of_week, sequence
--    - 인덱스가 (business_id, day_of_week, sequence) 순이면 Sort 생략
--
-- ✅ actual rows (7건 예상) vs estimated rows
--    - 한 업체의 영업시간 (월~일 7일)
--
-- ✅ Buffers 효율성
--    - 700건 중 7건만 반환
--    - Index Scan이면 최소한의 페이지만 읽음

ROLLBACK;
