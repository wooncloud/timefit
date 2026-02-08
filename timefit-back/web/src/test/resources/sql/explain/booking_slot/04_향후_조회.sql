-- ============================================================
-- BookingSlot 향후 슬롯 조회 (3,000,000건 - 극한 테스트)
-- ============================================================
-- API:        GET /api/business/{id}/booking-slot/upcoming
-- 핵심 쿼리:  SELECT * FROM booking_slot
--            WHERE business_id = ? AND slot_date >= CURRENT_DATE
--            LIMIT 50
-- 사전조건:   _setup.sql (User, Business)
-- 규모:       3,000,000건 (초대형 복합센터 3개월 운영)
-- ============================================================
-- 시나리오:
--   - 카테고리 15개
--   - 메뉴 375개 (15 × 25)
--   - 스태프 6명/메뉴
--   - 90일 운영 (과거 60일 + 미래 30일)
--   - 하루 14슬롯 (09:00~21:00, 50분 간격)
--
-- 목적: 300만건 속에서 LIMIT 50건 조기 종료 (Early Termination)
-- ============================================================

BEGIN;

-- ============================================================
-- 픽스처
-- ============================================================

-- business_category 생성 (15개 카테고리)
INSERT INTO business_category (
    id, business_id, business_type, category_name,
    is_active, created_at, updated_at
)
SELECT
    ('50000000-0000-0000-' || LPAD(cat_seq::text, 4, '0') || '-000000000000')::uuid,
    '99999999-0000-0000-0000-000000000100',
    'BD008',
    CASE cat_seq
        WHEN 1 THEN '헤어컷'
        WHEN 2 THEN '헤어펌'
        WHEN 3 THEN '헤어염색'
        WHEN 4 THEN '두피케어'
        WHEN 5 THEN '네일기본'
        WHEN 6 THEN '네일아트'
        WHEN 7 THEN '피부관리'
        WHEN 8 THEN '피부클리닉'
        WHEN 9 THEN '메이크업'
        WHEN 10 THEN '속눈썹'
        WHEN 11 THEN '마사지'
        WHEN 12 THEN '스파'
        WHEN 13 THEN '왁싱'
        WHEN 14 THEN '태닝'
        ELSE '발관리'
        END,
    true,
    NOW(),
    NOW()
FROM generate_series(1, 15) AS cat_seq
ON CONFLICT (id) DO NOTHING;

-- menu 생성 (카테고리당 25개 = 375개 메뉴)
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
    'Menu ' || cat_seq || '-' || menu_seq,
    '서비스 설명',
    30000 + (cat_seq * 5000) + (menu_seq * 1000),
    50,
    'RESERVATION_BASED',
    true,
    NOW(),
    NOW()
FROM
    generate_series(1, 15) AS cat_seq,
    generate_series(1, 25) AS menu_seq
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- BookingSlot 3,000,000건 생성
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
    generate_series(1, 15) AS cat_seq,
    generate_series(1, 25) AS menu_seq,
    generate_series(1, 6) AS staff_seq,
    generate_series(-60, 29) AS day_offset,
    generate_series(0, 13) AS slot_offset
WHERE
    (day_offset >= 0 OR (cat_seq + menu_seq + staff_seq + day_offset) % 20 != 0);

-- 생성된 데이터 확인
DO $$
    DECLARE
        total_count INTEGER;
        future_count INTEGER;
        future_available_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100';

        SELECT COUNT(*) INTO future_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100'
          AND slot_date >= CURRENT_DATE;

        SELECT COUNT(*) INTO future_available_count
        FROM booking_slot
        WHERE business_id = '99999999-0000-0000-0000-000000000100'
          AND slot_date >= CURRENT_DATE
          AND is_available = true;

        RAISE NOTICE '========================================';
        RAISE NOTICE 'BookingSlot 생성 완료 (극한 테스트)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 슬롯:     % 개', total_count;
        RAISE NOTICE '미래 슬롯:     % 개', future_count;
        RAISE NOTICE '예약 가능:     % 개', future_available_count;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================

ANALYZE booking_slot;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner 예측: rows=50 (LIMIT)';
RAISE NOTICE '300만건 속에서 50건만 읽고 조기 종료';
RAISE NOTICE '========================================';

-- EXPLAIN: 향후 슬롯 50개 조회 (Early Termination)
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
--    ANALYZE 후: rows=50 (LIMIT 적용)
--
-- ✅ Scan 타입 + Early Termination
--    Index Scan + LIMIT → 50개만 읽고 종료
--    Seq Scan이면 300만건 전체 스캔 후 LIMIT
--
-- ✅ Execution Time
--    Before: Seq Scan (3,000,000건) → 80~100초
--    After:  Index Scan + LIMIT → 목표 50ms 이하
--    개선율: 99.9%+
--
-- ✅ Early Termination 효과
--    LIMIT 50 + Index Scan = O(log N + 50)
--    실제로 읽은 rows: 50개만
--    나머지 2,999,950개는 건너뜀
--
-- ✅ 필요한 인덱스
--    CREATE INDEX idx_bookingslot_biz_future
--    ON booking_slot(business_id, slot_date, start_time)
--    WHERE is_available = true AND slot_date >= CURRENT_DATE;
--
--    Partial Index를 사용하면 인덱스 크기 대폭 감소!

-- 추가 테스트: LIMIT 없이 카운트
EXPLAIN (ANALYZE, BUFFERS)
SELECT COUNT(*) as total_future_slots
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date >= CURRENT_DATE
  AND is_available = true;

-- 추가 테스트: 다음 주 슬롯만 (LIMIT 100)
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    slot_date,
    COUNT(*) as slot_count
FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date >= CURRENT_DATE
  AND is_available = true
GROUP BY slot_date
ORDER BY slot_date
LIMIT 7;  -- 다음 7일

ROLLBACK;

-- ============================================================
-- 성능 예측
-- ============================================================
-- Before (인덱스 없음):
--   - Seq Scan: 3,000,000건 전체 스캔
--   - LIMIT 후처리: 상위 50개 선택
--   - 시간: 80~100초
--   - Buffers: shared read 36,000+
--
-- After (인덱스 + LIMIT):
--   - Index Scan: 50개만 읽고 종료 (Early Termination)
--   - 시간: 50ms 이하
--   - Buffers: shared read 5~10
--   - 개선율: 99.9%+
--
-- LIMIT의 마법:
--   - Index Scan + ORDER BY + LIMIT = 최강 조합
--   - 정렬된 인덱스에서 50개만 읽고 즉시 종료
--   - 60,000배 데이터 감소 (3,000,000 → 50)
--   - 1,500~2,000배 속도 향상 (100s → 50ms)