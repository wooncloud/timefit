-- ============================================================
-- 요일별 휴무 토글
-- ============================================================
-- API:        PATCH /api/business/{id}/operating-hours/{day}/toggle
-- 핵심 쿼리:  UPDATE operating_hours 
--            SET is_closed = NOT is_closed
--            WHERE business_id = ? AND day_of_week = ?
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 🔧 픽스처
-- ============================================================
-- operating_hours 7건 (한 업체의 월~일 영업시간)
-- 이유: WHERE business_id AND day_of_week → 특정 요일 지정하므로 최소 충분

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
    '99999999-0000-0000-0000-000000000100',
    day_num,
    '09:00:00'::time,
    '18:00:00'::time,
    false,
    0,
    NOW(),
    NOW()
FROM generate_series(0, 6) AS day_num;

-- ============================================================
-- 🔍 EXPLAIN: 일요일 휴무 설정 (day_of_week = 6)
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
UPDATE operating_hours
SET 
    is_closed = NOT is_closed,
    updated_at = NOW()
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND day_of_week = 6;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Update on operating_hours
--                 -> Index Scan / Bitmap Heap Scan / Seq Scan
--    - Update 노드: "변경 적용"
--    - Scan 노드: "행을 어떻게 찾는가" (여기가 핵심)
--
-- ✅ Scan 타입
--    - (business_id, day_of_week) 복합 인덱스가 있으면 Index Scan
--    - business_id만 인덱스가 있으면 Index Scan + Filter
--    - 없으면 Seq Scan + Filter
--
-- ✅ Index Cond vs Filter
--    - 이상적: Index Cond (business_id = '...' AND day_of_week = 6)
--    - 현실적: Index Cond (business_id = '...'), Filter (day_of_week = 6)
--
-- ✅ actual rows = 0 (Update 노드는 항상 0)
--    - Scan 노드의 actual rows 확인 (1건 또는 2건 예상)
--    - sequence가 있으면 같은 요일에 2건 (오전/오후)
--
-- ✅ Buffers: dirtied 최소
--    - 컬럼 2개만 변경 (is_closed, updated_at)
--
-- 참고: is_closed를 토글하면
--       false → true (휴무 설정)
--       true → false (영업 재개)

ROLLBACK;
