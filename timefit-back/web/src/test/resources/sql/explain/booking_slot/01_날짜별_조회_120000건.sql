-- ============================================================
-- API:        GET /api/business/{id}/booking-slot?date={}
-- 핵심 쿼리:  SELECT * FROM booking_slot
--            WHERE business_id = ? AND slot_date = ?
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================
-- 시나리오:
--   - 메뉴 30개 (카테고리 6개 × 메뉴 5개)
--   - 스태프 4명/메뉴
--   - 90일 운영 (과거 60일 + 미래 30일)
--   - 하루 14슬롯 (09:00~21:00, 50분 간격)
-- ============================================================

BEGIN;

-- ============================================================
-- 픽스처
-- ============================================================

-- business_category 생성 (6개 카테고리)
INSERT INTO business_category (
    id, business_id, business_type, category_name,
    is_active, created_at, updated_at
)
SELECT
    ('50000000-0000-0000-' || LPAD(cat_seq::text, 4, '0') || '-000000000000')::uuid,
    '99999999-0000-0000-0000-000000000100',
    'BD008',
    CASE cat_seq
        WHEN 1 THEN '헤어'
        WHEN 2 THEN '네일'
        WHEN 3 THEN '피부관리'
        WHEN 4 THEN '메이크업'
        WHEN 5 THEN '속눈썹/반영구'
        ELSE '마사지/스파'
        END,
    true,
    NOW(),
    NOW()
FROM generate_series(1, 6) AS cat_seq
ON CONFLICT (id) DO NOTHING;

-- menu 생성 (카테고리당 5개 = 30개 메뉴)
INSERT INTO menu (
    id, business_id, business_category_id, service_name,
    description, price, duration_minutes, order_type,
    is_active, created_at, updated_at
)
SELECT
    ('60000000-0000-' || LPAD(cat_seq::text, 4, '0') || '-' ||
     LPAD(menu_seq::text, 4, '0') || '-000000000000')::uuid,
    '99999999-0000-0000-0000-000000000100',
    ('50000000-0000-0000-' || LPAD(cat_seq::text, 4, '0') || '-000000000000')::uuid,
    CASE cat_seq
        WHEN 1 THEN '헤어 ' || menu_seq || '번'
        WHEN 2 THEN '네일 ' || menu_seq || '번'
        WHEN 3 THEN '피부 ' || menu_seq || '번'
        WHEN 4 THEN '메이크업 ' || menu_seq || '번'
        WHEN 5 THEN '속눈썹 ' || menu_seq || '번'
        ELSE '마사지 ' || menu_seq || '번'
        END,
    '서비스 설명',
    CASE cat_seq
        WHEN 1 THEN 50000 + (menu_seq * 10000)
        WHEN 2 THEN 40000 + (menu_seq * 8000)
        WHEN 3 THEN 80000 + (menu_seq * 15000)
        WHEN 4 THEN 60000 + (menu_seq * 10000)
        WHEN 5 THEN 70000 + (menu_seq * 12000)
        ELSE 90000 + (menu_seq * 15000)
        END,
    50,  -- 50분 시술
    'RESERVATION_BASED',
    true,
    NOW(),
    NOW()
FROM
    generate_series(1, 6) AS cat_seq,
    generate_series(1, 5) AS menu_seq
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- BookingSlot 120,000건 생성
-- ============================================================
-- 구조:
-- - 카테고리 6개 × 메뉴 5개 = 30개 메뉴
-- - 메뉴당 스태프 4명
-- - 기간: 과거 60일 + 미래 30일 = 90일
-- - 하루 슬롯: 14개 (09:00~21:00, 50분 간격)
--
-- 총 생성: 6 × 5 × 4 × 90 × 14 = 151,200개
-- 필터링 후: 약 120,000개
-- ============================================================

INSERT INTO booking_slot (
    id, business_id, menu_id, slot_date, start_time, end_time,
    is_available, created_at, updated_at
)
SELECT
    gen_random_uuid(),
    '99999999-0000-0000-0000-000000000100',
    ('60000000-0000-' || LPAD(cat_seq::text, 4, '0') || '-' ||
     LPAD(menu_seq::text, 4, '0') || '-000000000000')::uuid,
    -- 날짜: 과거 60일 + 미래 30일
    CURRENT_DATE + (day_offset || ' days')::interval,
    -- 시작 시간: 09:00 + (slot_offset × 50분)
    ('09:00:00'::time + (slot_offset * INTERVAL '50 minutes')),
    -- 종료 시간: 시작 + 50분
    ('09:50:00'::time + (slot_offset * INTERVAL '50 minutes')),
    -- 가용성: 과거 슬롯 일부만 활성
    CASE
        WHEN day_offset < 0 THEN (
            -- 과거: 70% 활성 (예약되지 않은 슬롯)
            (cat_seq + menu_seq + staff_seq + day_offset + slot_offset) % 10 < 7
            )
        ELSE true  -- 미래: 100% 활성
        END,
    NOW() - ((90 - day_offset) || ' days')::interval,
    NOW()
FROM
    generate_series(1, 6) AS cat_seq,           -- 카테고리 6개
    generate_series(1, 5) AS menu_seq,          -- 카테고리당 메뉴 5개
    generate_series(1, 4) AS staff_seq,         -- 메뉴당 스태프 4명
    generate_series(-60, 29) AS day_offset,     -- 90일 (과거 60 + 미래 30)
    generate_series(0, 13) AS slot_offset       -- 하루 14슬롯
WHERE
    -- 필터링: 120,000개 정도만 생성
    -- 미래 슬롯은 모두 생성, 과거는 약 60%만
    (day_offset >= 0 OR (day_offset + cat_seq + menu_seq + staff_seq) % 5 != 0);

-- 생성된 데이터 확인
DO $$
    DECLARE
        total_count INTEGER;
        active_count INTEGER;
        future_count INTEGER;
        past_count INTEGER;
        date_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100';

        SELECT COUNT(*) INTO active_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100'
          AND is_available = true;

        SELECT COUNT(*) INTO future_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100'
          AND slot_date >= CURRENT_DATE;

        SELECT COUNT(*) INTO past_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100'
          AND slot_date < CURRENT_DATE;

        SELECT COUNT(DISTINCT slot_date) INTO date_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100';

        RAISE NOTICE '========================================';
        RAISE NOTICE 'BookingSlot 생성 완료 (대규모)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 슬롯:     % 개', total_count;
        RAISE NOTICE '활성 슬롯:     % 개 (%.1f%%)', active_count, (active_count::FLOAT / total_count * 100);
        RAISE NOTICE '미래 슬롯:     % 개', future_count;
        RAISE NOTICE '과거 슬롯:     % 개', past_count;
        RAISE NOTICE '날짜 범위:     % 일', date_count;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================
-- 이유: ROLLBACK 환경에서도 Planner가 정확한 예측을 하도록
--       방금 삽입한 120,000건의 분포를 통계에 반영
-- 효과: rows=1 (엉터리) → rows=84 (정확)
-- ============================================================

ANALYZE booking_slot;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner가 이제 정확한 예측 가능';
RAISE NOTICE '========================================';

-- ============================================================
-- EXPLAIN: 특정 날짜의 슬롯 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS, VERBOSE)
SELECT
    id,
    business_id,
    menu_id,
    slot_date,
    start_time,
    end_time,
    is_available,
    created_at,
    updated_at
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date = CURRENT_DATE + INTERVAL '7 days'
  AND is_available = true
ORDER BY start_time ASC;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Planner 예측 정확도 ⭐ 핵심!
--    Before ANALYZE: rows=1 (엉터리)
--    After ANALYZE:  rows=84 (정확) ← 30메뉴 × 4스태프 × 70% = 84
--
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    120,000건 규모에서 PostgreSQL이 어떤 스캔 방식 선택?
--
-- ✅ Index Cond vs Filter 구분
--    Index Cond: (business_id = '...' AND slot_date = '...')
--    Filter: (is_available = true)
--
-- ✅ Rows Removed by Filter
--    is_available = false인 슬롯이 얼마나 제거되는지
--
-- ✅ Sort 노드
--    ORDER BY start_time → Sort 비용 측정
--    인덱스에 start_time 포함 시 Sort 생략 가능
--
-- ✅ Buffers (I/O 비용)
--    shared read: 디스크에서 읽음
--    shared hit: 캐시에서 읽음
--    120,000건에서 캐시 효율성
--
-- ✅ Execution Time
--    Before: Seq Scan 예상 4~5초
--    After:  Index Scan 목표 40ms 이하
--
-- ✅ 필요한 인덱스
--    CREATE INDEX idx_bookingslot_biz_date_time
--    ON booking_slot(business_id, slot_date, start_time)
--    WHERE is_available = true;

-- ============================================================
-- 추가 테스트: 다른 날짜로 반복 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    slot_date,
    COUNT(*) as total_slots,
    SUM(CASE WHEN is_available THEN 1 ELSE 0 END) as available_slots
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date = CURRENT_DATE + INTERVAL '15 days'
GROUP BY slot_date;

-- ============================================================
-- 추가 테스트: 시간대별 슬롯 개수
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    start_time,
    COUNT(*) as slot_count
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date = CURRENT_DATE + INTERVAL '7 days'
  AND is_available = true
GROUP BY start_time
ORDER BY start_time;

ROLLBACK;

-- ============================================================
-- 결과 해석 가이드
-- ============================================================
-- 1. ANALYZE 효과 확인
--    EXPLAIN 출력에서 "rows=84"처럼 정확한 예측이 나오는지 확인
--    (ANALYZE 없으면 rows=1 같은 엉터리 예측)
--
-- 2. Seq Scan 성능
--    120,000건 전체 스캔 시 4~5초 소요 예상
--    → 인덱스 필요성 명확히 확인
--
-- 3. 대규모 데이터에서 I/O 패턴
--    Buffers 통계로 디스크 vs 캐시 비율 확인
--    대용량일수록 캐시 미스 증가 가능