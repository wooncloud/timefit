-- ============================================================
-- 업체 예약 목록 조회 (1,000,000건 - 극한 테스트)
-- ============================================================
-- API:        GET /api/business/{id}/reservations
-- 핵심 쿼리:  SELECT * FROM reservation
--            WHERE business_id = ? AND status = ?
-- 사전조건:   _setup.sql (User, Business)
-- 규모:       1,000,000건 (업체 400개)
-- ============================================================
-- 시나리오:
--   - Business 1:     2,500건 (테스트 대상) ⭐
--   - Business 2~400: 997,500건 (더미 데이터)
--   - Customer:       10,000명 순환
--
-- 목적: 100만건 속에서 2,500건 찾기 (PENDING 750건)
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
-- 픽스처: Business 400개 생성
-- ============================================================

-- business_type 데이터 (참조용)
INSERT INTO business_type (business_id, type_code)
SELECT
    ('20000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    'BD008'
FROM generate_series(1, 400) AS biz_seq
ON CONFLICT (business_id, type_code) DO NOTHING;

-- business 400개 생성
INSERT INTO business (
    id, business_name, business_number, owner_name,
    address, contact_phone, description,
    is_active, average_rating, review_count,
    created_at, updated_at
)
SELECT
    ('20000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    'Business ' || biz_seq,
    LPAD((1000000000 + biz_seq)::text, 10, '0'),
    'Owner ' || biz_seq,
    'Address ' || biz_seq,
    '02' || LPAD((20000000 + biz_seq)::text, 8, '0'),
    'Test Business ' || biz_seq,
    true,
    0.0,
    0,
    NOW(),
    NOW()
FROM generate_series(1, 400) AS biz_seq
ON CONFLICT (id) DO NOTHING;

RAISE NOTICE 'Business 400개 생성 완료';

-- ============================================================
-- 픽스처: Business Category, Menu, BookingSlot
-- ============================================================

-- business_category 1건 (Business 1용)
INSERT INTO business_category (
    id, business_id, business_type, category_name,
    is_active, created_at, updated_at
)
VALUES (
           '99999999-0000-0000-0000-000000000300'::uuid,
           '20000000-0000-0000-0000-000000000001'::uuid,
           'BD008',
           'Test Category',
           true,
           NOW(),
           NOW()
       ) ON CONFLICT (id) DO NOTHING;

-- menu 1건 (Business 1용)
INSERT INTO menu (
    id, business_id, business_category_id, service_name,
    description, price, duration_minutes, order_type,
    is_active, created_at, updated_at
)
VALUES (
           '99999999-0000-0000-0000-000000000400'::uuid,
           '20000000-0000-0000-0000-000000000001'::uuid,
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
           '20000000-0000-0000-0000-000000000001'::uuid,
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
-- - Business 1:     2,500건 (테스트 대상)
-- - Business 2~400: 997,500건 (더미)
-- - 업체당 평균:    2,500건
-- - Customer:       10,000명 순환
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
    -- business: 400개 순환 (업체당 2,500건)
    ('20000000-0000-0000-0000-' || LPAD(((res_seq % 400) + 1)::text, 12, '0'))::uuid,
    -- customer: 10,000명 순환
    ('10000000-0000-0000-0000-' || LPAD(((res_seq % 10000) + 1)::text, 12, '0'))::uuid,
    -- menu: Business 1만 실제 menu, 나머지는 NULL
    CASE
        WHEN (res_seq % 400) + 1 = 1 THEN '99999999-0000-0000-0000-000000000400'::uuid
        ELSE NULL::uuid
        END,
    -- booking_slot: Business 1만 실제 slot, 나머지는 NULL
    CASE
        WHEN (res_seq % 400) + 1 = 1 AND res_seq % 3 = 0
            THEN '99999999-0000-0000-0000-000000000500'::uuid
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
        business_count INTEGER;
        test_business_count INTEGER;
        pending_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO total_count
        FROM reservation;

        SELECT COUNT(DISTINCT business_id) INTO business_count
        FROM reservation;

        -- 테스트용 업체 (Business 1) - 2,500건 예상
        SELECT COUNT(*) INTO test_business_count
        FROM reservation
        WHERE business_id = '20000000-0000-0000-0000-000000000001';

        -- PENDING 상태 개수 (30%)
        SELECT COUNT(*) INTO pending_count
        FROM reservation
        WHERE business_id = '20000000-0000-0000-0000-000000000001'
          AND status = 'PENDING';

        RAISE NOTICE '========================================';
        RAISE NOTICE 'Reservation 생성 완료 (극한 테스트)';
        RAISE NOTICE '========================================';
        RAISE NOTICE '전체 예약:     % 건', total_count;
        RAISE NOTICE '업체 수:       % 개', business_count;
        RAISE NOTICE 'Business1:     % 건 ⭐', test_business_count;
        RAISE NOTICE 'PENDING:       % 건 (30%%)', pending_count;
        RAISE NOTICE '더미 데이터:   % 건', total_count - test_business_count;
        RAISE NOTICE '========================================';
    END $$;

-- ============================================================
-- ★ 핵심: 트랜잭션 내 통계 정보 갱신
-- ============================================================

ANALYZE reservation;

RAISE NOTICE '========================================';
RAISE NOTICE '통계 정보 갱신 완료 (ANALYZE)';
RAISE NOTICE 'Planner 예측: rows=750 (2500 × 30%%)';
RAISE NOTICE '100만건 속에서 750건 찾기 준비 완료';
RAISE NOTICE '========================================';

-- ============================================================
-- EXPLAIN: 업체 예약 목록 조회 (100만건 중 750건)
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
    created_at,
    updated_at
FROM reservation
WHERE business_id = '20000000-0000-0000-0000-000000000001'
  AND status = 'PENDING'
ORDER BY reservation_date ASC, reservation_time ASC;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Planner 예측 정확도 ⭐
--    ANALYZE 후: rows=750 (2,500 × 30%)
--
-- ✅ Scan 타입
--    Before: Seq Scan (1,000,000건 전체)
--    After:  Index Scan (2,500건 → Filter → 750건)
--
-- ✅ Execution Time
--    Before: Seq Scan → 예상 35~40초
--    After:  Index Scan → 목표 150ms 이하
--    개선율: 99.6%+
--
-- ✅ Selectivity (선택도)
--    2,500 / 1,000,000 = 0.25%
--    PENDING 750 / 1,000,000 = 0.075%
--    → 복합 인덱스 필수!
--
-- ✅ 필요한 인덱스
--    CREATE INDEX idx_reservation_biz_status_date
--    ON reservation(business_id, status, reservation_date ASC);

-- ============================================================
-- 추가 테스트: 전체 상태 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    id,
    customer_id,
    reservation_date,
    reservation_time,
    status
FROM reservation
WHERE business_id = '20000000-0000-0000-0000-000000000001'
ORDER BY reservation_date DESC
LIMIT 100;

-- ============================================================
-- 추가 테스트: 상태별 집계
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    status,
    COUNT(*) as count,
    SUM(reservation_price) as total_price,
    AVG(reservation_price) as avg_price
FROM reservation
WHERE business_id = '20000000-0000-0000-0000-000000000001'
GROUP BY status;

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
--   - Index Scan: 2,500건 조회 → Filter → 750건
--   - 시간: 150ms 이하
--   - Buffers: shared read 30~50
--   - 개선율: 99.6%+
--
-- 인덱스 효과:
--   - 400배 데이터 감소 (1,000,000 → 2,500)
--   - status Filter로 3.3배 추가 감소 (2,500 → 750)
--   - 250~300배 속도 향상 (40s → 150ms)