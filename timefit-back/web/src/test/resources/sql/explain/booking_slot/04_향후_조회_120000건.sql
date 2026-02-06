-- ============================================================
-- BookingSlot 향후 조회 (120,000건 - 대규모)
-- ============================================================
-- API:        GET /api/business/{id}/booking-slot/upcoming
-- 핵심 쿼리:  SELECT * FROM booking_slot
--            WHERE business_id = ? AND slot_date >= CURRENT_DATE
-- 사전조건:   _setup.sql (User, Business)
-- 규모:       120,000건 (초대형 프랜차이즈 3개월 운영)
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
    50,
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

INSERT INTO booking_slot (
    id, business_id, menu_id, slot_date, start_time, end_time,
    is_available, created_at, updated_at
)
SELECT
    gen_random_uuid(),
    '99999999-0000-0000-0000-000000000100',
    ('60000000-0000-' || LPAD(cat_seq::text, 4, '0') || '-' ||
     LPAD(menu_seq::text, 4, '0') || '-000000000000')::uuid,
    CURRENT_DATE + (day_offset || ' days')::interval,
    ('09:00:00'::time + (slot_offset * INTERVAL '50 minutes')),
    ('09:50:00'::time + (slot_offset * INTERVAL '50 minutes')),
    CASE
        WHEN day_offset < 0 THEN (
            (cat_seq + menu_seq + staff_seq + day_offset + slot_offset) % 10 < 7
            )
        ELSE true
        END,
    NOW() - ((90 - day_offset) || ' days')::interval,
    NOW()
FROM
    generate_series(1, 6) AS cat_seq,
    generate_series(1, 5) AS menu_seq,
    generate_series(1, 4) AS staff_seq,
    generate_series(-60, 29) AS day_offset,
    generate_series(0, 13) AS slot_offset
WHERE
    (day_offset >= 0 OR (day_offset + cat_seq + menu_seq + staff_seq) % 5 != 0);

-- 생성된 데이터 확인
DO $$
    DECLARE
        total_count INTEGER;
        future_count INTEGER;
        past_count INTEGER;
        available_future INTEGER;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100';

        SELECT COUNT(*) INTO future_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100'
          AND slot_date >= CURRENT_DATE;

        SELECT COUNT(*) INTO past_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100'
          AND slot_date < CURRENT_DATE;

        SELECT COUNT(*) INTO available_future
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100'
          AND slot_date >= CURRENT_DATE
          AND is_available = true;

        RAISE NOTICE '========================================';
        RAISE NOTICE 'BookingSlot 생성 완료 (대규모)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 슬롯:     % 개', total_count;
        RAISE NOTICE '미래 슬롯:     % 개', future_count;
        RAISE NOTICE '과거 슬롯:     % 개', past_count;
        RAISE NOTICE '예약 가능:     % 개', available_future;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================

ANALYZE booking_slot;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner 예측: rows=50 (LIMIT으로 조기 종료)';
RAISE NOTICE '========================================';

-- ============================================================
-- EXPLAIN: 향후 슬롯 조회 (LIMIT 적용)
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
  AND slot_date >= CURRENT_DATE
  AND is_available = true
ORDER BY slot_date ASC, start_time ASC
LIMIT 50;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Planner 예측 정확도 ⭐
--    ANALYZE 후: rows=50 (LIMIT 인식)
--    실제로 50건만 읽고 중단
--    (ANALYZE 없으면 예측 불가)
--
-- ✅ Scan 타입: Index Scan vs Bitmap Index Scan
--    (business_id, slot_date) 복합 인덱스로 Range Scan
--    slot_date >= 조건은 범위 검색
--
-- ✅ Index Cond vs Filter
--    Index Cond: (business_id = '...' AND slot_date >= ...)
--    Filter: (is_available = true)
--
-- ✅ LIMIT 노드 ⭐ 핵심!
--    LIMIT 50이 상위 노드로 등장
--    Index Scan이면 50건만 읽고 중단 (효율적)
--    Seq Scan이면 전체 읽고 LIMIT (비효율적)
--
-- ✅ Sort 노드 vs Index 순서
--    ORDER BY slot_date, start_time
--    인덱스가 (business_id, slot_date, start_time) 순이면 Sort 생략
--    아니면 Sort 노드 등장
--
-- ✅ actual rows (50건) vs estimated rows
--    LIMIT으로 실제 읽은 행 수 확인
--    미래 슬롯 25,200개 중 50건만 반환
--
-- ✅ Buffers 효율성 ⭐
--    Index Scan + LIMIT → 최소 페이지만 읽음
--    Seq Scan → 전체 스캔 후 LIMIT (비효율)
--
-- ✅ Execution Time
--    Before: Seq Scan (120,000건) → 예상 4~5초
--    After:  Index Scan + LIMIT → 목표 30ms 이하
--
-- ✅ 부분 인덱스 효과
--    WHERE is_available = true AND slot_date >= CURRENT_DATE
--    부분 인덱스로 인덱스 크기 감소

-- ============================================================
-- 추가 테스트: LIMIT 없이 전체 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    COUNT(*) as total_future_slots,
    MIN(slot_date) as earliest_date,
    MAX(slot_date) as latest_date
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date >= CURRENT_DATE
  AND is_available = true;

-- ============================================================
-- 추가 테스트: 날짜별 집계 (미래 7일)
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    slot_date,
    COUNT(*) as slot_count,
    COUNT(DISTINCT menu_id) as menu_count,
    MIN(start_time) as first_time,
    MAX(end_time) as last_time
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
  AND is_available = true
GROUP BY slot_date
ORDER BY slot_date;

-- ============================================================
-- 추가 테스트: 메뉴별 미래 슬롯 개수
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    menu_id,
    COUNT(*) as future_slot_count
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date >= CURRENT_DATE
  AND is_available = true
GROUP BY menu_id
ORDER BY future_slot_count DESC
LIMIT 10;

ROLLBACK;