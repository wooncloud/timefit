-- ============================================================
-- 영업시간 리셋
-- ============================================================
-- API:        PATCH /api/business/{id}/operating-hours/reset
-- 핵심 쿼리:  DELETE FROM operating_hours WHERE business_id = ?
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 🔧 픽스처
-- ============================================================
-- operating_hours 700건 생성 (100개 업체 × 7일)
-- 이유: DELETE WHERE business_id (비-PK 조건) → 테이블이 커야 Index Scan 등장

-- 업체 100개 생성
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
-- 🔍 EXPLAIN: 특정 업체의 영업시간 리셋 (전체 삭제)
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
DELETE FROM operating_hours
WHERE business_id = '99999999-0000-0000-0000-000000000100';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Delete on operating_hours
--                 -> Index Scan / Bitmap Heap Scan / Seq Scan
--    - Delete 노드: "삭제 적용"
--    - Scan 노드: "삭제할 행들을 어떻게 찾는가" (여기가 핵심)
--
-- ✅ Scan 타입
--    - business_id에 인덱스가 있으면 Index/Bitmap Scan
--    - 없으면 Seq Scan + Filter
--
-- ✅ Index Cond: (business_id = '...')
--    - 인덱스가 조건 처리
--
-- ✅ actual rows = 0 (Delete 노드는 항상 0)
--    - Scan 노드의 actual rows 확인 (7건 예상)
--    - 한 업체의 영업시간 전체 삭제
--
-- ✅ Buffers: dirtied
--    - 7건 삭제이므로 여러 페이지 dirtied
--    - MVCC에서는 실제 삭제가 아닌 표시
--
-- ✅ 700건 중 7건만 삭제
--    - Index Scan이면 7건만 접근
--    - Seq Scan이면 전체 700건 스캔 후 7건 삭제
--    - 성능 차이가 명확히 드러남
--
-- 참고: 실제 API는 리셋 후 기본값 재설정
--       (DELETE 후 INSERT)

ROLLBACK;
