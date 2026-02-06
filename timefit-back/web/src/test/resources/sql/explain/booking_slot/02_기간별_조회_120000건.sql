-- ============================================================
-- API:        GET /api/business/{id}/booking-slot/range
-- 핵심 쿼리:  SELECT * FROM booking_slot
--            WHERE business_id = ? AND slot_date BETWEEN ? AND ?
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
-- 픽스처 (날짜별 조회와 동일)
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
        week_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100';

        SELECT COUNT(*) INTO week_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100'
          AND slot_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days';

        RAISE NOTICE '========================================';
        RAISE NOTICE 'BookingSlot 생성 완료 (대규모)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 슬롯:     % 개', total_count;
        RAISE NOTICE '7일 슬롯:      % 개', week_count;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================

ANALYZE booking_slot;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner 예측: rows=588 (7일 × 84개/일)';
RAISE NOTICE '========================================';

-- ============================================================
-- EXPLAIN: 7일간의 슬롯 조회
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
  AND slot_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
  AND is_available = true
ORDER BY slot_date ASC, start_time ASC;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Planner 예측 정확도 ⭐
--    ANALYZE 후: rows=588 vs actual rows=588 (정확)
--    7일 × 84개/일 = 588개 예측
--    (ANALYZE 없으면 rows=1 같은 엉터리)
--
-- ✅ Scan 타입: Index Range Scan vs Bitmap Index Scan
--    BETWEEN 조건은 범위 검색
--    (business_id, slot_date) 복합 인덱스 필요
--
-- ✅ Index Cond vs Filter
--    Index Cond: (business_id = '...' AND slot_date >= ... AND slot_date <= ...)
--    Filter: (is_available = true)
--
-- ✅ Sort 노드 존재 여부
--    ORDER BY slot_date, start_time
--    인덱스 순서와 일치하면 Sort 생략
--
-- ✅ Buffers: 범위 검색의 I/O 패턴
--    여러 페이지 연속 접근
--    캐시 효율성 (sequential vs random read)
--
-- ✅ Execution Time
--    Before: Seq Scan (120,000건) → 예상 4~5초
--    After:  Index Range Scan → 목표 55ms 이하

-- ============================================================
-- 추가 테스트: 날짜별 집계
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    slot_date,
    COUNT(*) as total_slots,
    SUM(CASE WHEN is_available THEN 1 ELSE 0 END) as available_slots,
    MIN(start_time) as first_slot,
    MAX(end_time) as last_slot
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
GROUP BY slot_date
ORDER BY slot_date;

-- ============================================================
-- 추가 테스트: 14일 기간 조회 (더 긴 기간)
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    COUNT(*) as total_slots,
    COUNT(DISTINCT slot_date) as date_count,
    COUNT(DISTINCT menu_id) as menu_count
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '14 days'
  AND is_available = true;

ROLLBACK;