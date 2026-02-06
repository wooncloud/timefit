-- ============================================================
-- BookingSlot 메뉴별 조회 (120,000건 - 대규모)
-- ============================================================
-- API:        GET /api/business/{id}/booking-slot/menu/{mid}
-- 핵심 쿼리:  SELECT * FROM booking_slot
--            WHERE menu_id = ? AND slot_date >= ?
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
        menu_count INTEGER;
        future_menu_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100';

        -- 특정 메뉴 (헤어 1번)
        SELECT COUNT(*) INTO menu_count
        FROM booking_slot
        WHERE menu_id = '60000000-0000-0001-0001-000000000000';

        -- 특정 메뉴의 미래 슬롯
        SELECT COUNT(*) INTO future_menu_count
        FROM booking_slot
        WHERE menu_id = '60000000-0000-0001-0001-000000000000'
          AND slot_date >= CURRENT_DATE;

        RAISE NOTICE '========================================';
        RAISE NOTICE 'BookingSlot 생성 완료 (대규모)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 슬롯:     % 개', total_count;
        RAISE NOTICE '헤어1 슬롯:    % 개', menu_count;
        RAISE NOTICE '헤어1 미래:    % 개', future_menu_count;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================

ANALYZE booking_slot;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner 예측: rows=1680 (4스태프 × 30일 × 14슬롯)';
RAISE NOTICE '========================================';

-- ============================================================
-- EXPLAIN: 특정 메뉴의 향후 슬롯 조회
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
WHERE menu_id = '60000000-0000-0001-0001-000000000000'  -- 헤어 1번
  AND slot_date >= CURRENT_DATE
  AND is_available = true
ORDER BY slot_date ASC, start_time ASC;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Planner 예측 정확도 ⭐
--    ANALYZE 후: rows=1680 vs actual rows=1680 (정확)
--    4스태프 × 30일 × 14슬롯 = 1,680개
--    (ANALYZE 없으면 rows=1 같은 엉터리)
--
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    menu_id에 인덱스 필요
--    FK 인덱스 자동 생성 여부 확인
--
-- ✅ Index Cond vs Filter
--    Index Cond: (menu_id = '...')
--    Filter: (slot_date >= ... AND is_available = true)
--    slot_date가 인덱스에 없으면 Filter 처리
--
-- ✅ Sort 노드
--    ORDER BY slot_date, start_time
--    인덱스에 포함되지 않으면 Sort 필요
--
-- ✅ FK 인덱스의 효과
--    menu_id는 FK이므로 기본 인덱스 존재 가능
--    하지만 정렬 순서는 (menu_id)만 → Sort 필요
--
-- ✅ Execution Time
--    Before: Seq Scan (120,000건) → 예상 4~5초
--    After:  Index Scan → 목표 50ms 이하
--
-- ✅ 필요한 인덱스
--    CREATE INDEX idx_bookingslot_menu_date_time
--    ON booking_slot(menu_id, slot_date, start_time)
--    WHERE is_available = true;

-- ============================================================
-- 추가 테스트: 날짜별 슬롯 개수
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    slot_date,
    COUNT(*) as slot_count,
    SUM(CASE WHEN is_available THEN 1 ELSE 0 END) as available_count
FROM booking_slot
WHERE menu_id = '60000000-0000-0001-0001-000000000000'
  AND slot_date >= CURRENT_DATE
GROUP BY slot_date
ORDER BY slot_date
LIMIT 14;  -- 2주치만

-- ============================================================
-- 추가 테스트: 시간대별 분포
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    start_time,
    COUNT(*) as total_slots,
    COUNT(CASE WHEN slot_date >= CURRENT_DATE THEN 1 END) as future_slots
FROM booking_slot
WHERE menu_id = '60000000-0000-0001-0001-000000000000'
GROUP BY start_time
ORDER BY start_time;

ROLLBACK;