-- ============================================================
-- 내 예약 목록 조회 (1,000,000건 - 극한 테스트)
-- ============================================================
-- API:        GET /api/reservations
-- 핵심 쿼리:  SELECT * FROM reservation
--            WHERE customer_id = ?
-- 사전조건:   _setup.sql (User, Business)
-- 규모:       1,000,000건 (고객 10,000명)
-- ============================================================
-- 시나리오:
--   - Customer 1:        100건 (테스트 대상) ⭐
--   - Customer 2~10,000: 999,900건 (더미 데이터)
--   - Business 1개:      전체 1,000,000건
--
-- 목적: 100만건 속에서 100건 찾기
-- ============================================================

BEGIN;

-- ============================================================
-- 픽스처: Customer 10,000명 생성
-- ============================================================

INSERT INTO users (
    id, email, password_hash, name, phone_number, role,
    created_at, updated_at
)
SELECT
    ('10000000-0000-0000-0000-' || LPAD(cust_seq::text, 12, '0'))::uuid,
    'customer' || cust_seq || '@test.com',
    '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW',
    'Customer ' || cust_seq,
    '010' || LPAD((cust_seq + 10000000)::text, 8, '0'),
    'USER',
    NOW(),
    NOW()
FROM generate_series(1, 10000) AS cust_seq
ON CONFLICT (id) DO NOTHING;

RAISE NOTICE 'Customer 10,000명 생성 완료';

-- ============================================================
-- 픽스처: Business Category, Menu, BookingSlot
-- ============================================================

-- business_category 1건
INSERT INTO business_category (
    id, business_id, business_type, category_name,
    is_active, created_at, updated_at
)
VALUES (
           '99999999-0000-0000-0000-000000000300'::uuid,
           '99999999-0000-0000-0000-000000000100'::uuid,
           'BD008',
           'Test Category',
           true,
           NOW(),
           NOW()
       ) ON CONFLICT (id) DO NOTHING;

-- menu 1건
INSERT INTO menu (
    id, business_id, business_category_id, service_name,
    description, price, duration_minutes, order_type,
    is_active, created_at, updated_at
)
VALUES (
           '99999999-0000-0000-0000-000000000400'::uuid,
           '99999999-0000-0000-0000-000000000100'::uuid,
           '99999999-0000-0000-0000-000000000300'::uuid,
           'Test Service',
           'Test Description',
           50000,
           60,
           'RESERVATION_BASED',
           true,
           NOW(),
           NOW()
       ) ON CONFLICT (id) DO NOTHING;

-- booking_slot 1건 (참조용)
INSERT INTO booking_slot (
    id, business_id, menu_id, slot_date, start_time, end_time,
    is_available, created_at, updated_at
)
VALUES (
           '99999999-0000-0000-0000-000000000500'::uuid,
           '99999999-0000-0000-0000-000000000100'::uuid,
           '99999999-0000-0000-0000-000000000400'::uuid,
           CURRENT_DATE + INTERVAL '1 day',
           '10:00:00'::time,
           '11:00:00'::time,
           true,
           NOW(),
           NOW()
       ) ON CONFLICT (id) DO NOTHING;

RAISE NOTICE 'Business 픽스처 생성 완료';

-- ============================================================
-- Reservation 1,000,000건 생성
-- ============================================================
-- 구조:
-- - Customer 1:        100건 (테스트 대상)
-- - Customer 2~10,000: 999,900건 (더미)
-- - 고객당 평균:       100건
-- - Business:          1개 고정
-- ============================================================

INSERT INTO reservation (
    id,
    business_id,
    customer_id,
    menu_id,
    booking_slot_id,
    reservation_date,
    reservation_time,
    reservation_price,
    reservation_duration,
    customer_name,
    customer_phone,
    status,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    -- business: 1개 고정
    '99999999-0000-0000-0000-000000000100'::uuid,
    -- customer: 10,000명 순환 (고객당 100건)
    ('10000000-0000-0000-0000-' || LPAD(((res_seq % 10000) + 1)::text, 12, '0'))::uuid,
    -- menu
    '99999999-0000-0000-0000-000000000400'::uuid,
    -- booking_slot
    CASE
        WHEN res_seq % 3 = 0 THEN '99999999-0000-0000-0000-000000000500'::uuid
        ELSE NULL::uuid
        END,
    -- reservation_date: 과거 1000일 분산
    CURRENT_DATE - ((res_seq % 1000) || ' days')::interval,
    -- reservation_time: 10:00~18:00
    ('10:00:00'::time + ((res_seq % 9) || ' hours')::interval),
    -- price: 20K~80K
    CASE (res_seq % 4)
        WHEN 0 THEN 20000
        WHEN 1 THEN 35000
        WHEN 2 THEN 50000
        ELSE 80000
        END,
    60,  -- duration
    'Customer ' || ((res_seq % 10000) + 1),
    '010' || LPAD((((res_seq % 10000) + 10000000))::text, 8, '0'),
    -- status: 현실적 비율
    CASE
        WHEN res_seq % 20 < 6 THEN 'PENDING'       -- 30%
        WHEN res_seq % 20 < 14 THEN 'CONFIRMED'    -- 40%
        WHEN res_seq % 20 < 18 THEN 'COMPLETED'    -- 20%
        WHEN res_seq % 20 < 19 THEN 'CANCELLED'    -- 5%
        ELSE 'NO_SHOW'                              -- 5%
        END,
    NOW() - ((res_seq % 1000) || ' days')::interval,
    NOW()
FROM generate_series(1, 1000000) AS res_seq;

-- 생성된 데이터 확인
DO $$
    DECLARE
        total_count INTEGER;
        customer_count INTEGER;
        test_customer_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM reservation;

        SELECT COUNT(DISTINCT customer_id) INTO customer_count
        FROM reservation;

        -- 테스트용 고객 (Customer 1) - 100건 예상
        SELECT COUNT(*) INTO test_customer_count
        FROM reservation
        WHERE customer_id = '10000000-0000-0000-0000-000000000001';

        RAISE NOTICE '========================================';
        RAISE NOTICE 'Reservation 생성 완료 (극한 테스트)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 예약:     % 건', total_count;
        RAISE NOTICE '고객 수:       % 명', customer_count;
        RAISE NOTICE 'Customer1:     % 건 ⭐', test_customer_count;
        RAISE NOTICE '더미 데이터:   % 건', total_count - test_customer_count;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================

ANALYZE reservation;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner 예측: rows=100';
RAISE NOTICE '100만건 속에서 100건 찾기 준비 완료';
RAISE NOTICE '========================================';

-- ============================================================
-- EXPLAIN: 내 예약 목록 조회 (100만건 중 100건)
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS, VERBOSE)
SELECT
    id,
    business_id,
    customer_id,
    menu_id,
    booking_slot_id,
    reservation_date,
    reservation_time,
    reservation_price,
    reservation_duration,
    customer_name,
    customer_phone,
    status,
    notes,
    created_at,
    updated_at
FROM reservation
WHERE customer_id = '10000000-0000-0000-0000-000000000001'
ORDER BY reservation_date DESC, reservation_time DESC;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Planner 예측 정확도 ⭐
--    ANALYZE 후: rows=100 (정확)
--
-- ✅ Scan 타입
--    Before: Seq Scan (1,000,000건 전체)
--    After:  Index Scan (100건만)
--
-- ✅ Execution Time
--    Before: Seq Scan → 예상 35~40초
--    After:  Index Scan → 목표 100ms 이하
--    개선율: 99.7%+
--
-- ✅ Selectivity (선택도)
--    100 / 1,000,000 = 0.01% (극도로 낮음)
--    → 인덱스 필수!
--
-- ✅ 필요한 인덱스
--    CREATE INDEX idx_reservation_customer_date
--    ON reservation(customer_id, reservation_date DESC);

-- ============================================================
-- 추가 테스트: 상태별 집계
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    status,
    COUNT(*) as count
FROM reservation
WHERE customer_id = '10000000-0000-0000-0000-000000000001'
GROUP BY status;

-- ============================================================
-- 추가 테스트: 최근 예약만 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    reservation_date,
    reservation_time,
    status
FROM reservation
WHERE customer_id = '10000000-0000-0000-0000-000000000001'
  AND reservation_date >= CURRENT_DATE - INTERVAL '30 days'
ORDER BY reservation_date DESC
LIMIT 10;

ROLLBACK;

-- ============================================================
-- 성능 예측
-- ============================================================
-- Before (인덱스 없음):
--   - Seq Scan: 1,000,000건 전체 스캔
--   - 시간: 35~40초
--   - Buffers: shared read 12,000+
--
-- After (인덱스 적용):
--   - Index Scan: 100건만 조회
--   - 시간: 100ms 이하
--   - Buffers: shared read 10~20
--   - 개선율: 99.7%+
--
-- 인덱스 효과:
--   - 10,000배 데이터 감소 (1,000,000 → 100)
--   - 350~400배 속도 향상 (40s → 100ms)