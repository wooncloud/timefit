-- ============================================================
-- 업체별 리뷰 목록 조회
-- ============================================================
-- API:        GET /api/public/business/{id}/reviews
-- 핵심 쿼리:  SELECT * FROM review WHERE business_id = ? AND deleted_at IS NULL
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 픽스처
-- ============================================================

-- 추가 고객 100명 생성 (리뷰 작성자)
INSERT INTO "user" (
    id, email, name, phone, role,
    email_verified, created_at, updated_at
)
SELECT
    ('c0000000-0000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    'customer' || i || '@test.com',
    'Customer ' || i,
    '010-' || LPAD((1000 + i)::text, 4, '0') || '-' || LPAD((i * 10)::text, 4, '0'),
    'ROLE_CUSTOMER',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 100) AS i;

-- business_category
INSERT INTO business_category (
    id, business_id, business_type, category_name,
    is_active, created_at, updated_at
)
VALUES (
           '99999999-0000-0000-0000-000000000300',
           '99999999-0000-0000-0000-000000000100',
           'BD008', 'Test Category', true, NOW(), NOW()
       );

-- menu
INSERT INTO menu (
    id, business_id, business_category_id, service_name,
    description, price, duration_minutes, order_type,
    is_active, created_at, updated_at
)
VALUES (
           '99999999-0000-0000-0000-000000000400',
           '99999999-0000-0000-0000-000000000100',
           '99999999-0000-0000-0000-000000000300',
           'Test Service', 'Test Description', 50000, 60,
           'RESERVATION_BASED', true, NOW(), NOW()
       );

-- booking_slot 1건
INSERT INTO booking_slot (
    id, business_id, menu_id, slot_date, start_time, end_time,
    is_available, created_at, updated_at
)
VALUES (
           '99999999-0000-0000-0000-000000000500',
           '99999999-0000-0000-0000-000000000100',
           '99999999-0000-0000-0000-000000000400',
           CURRENT_DATE + 1, '10:00:00'::time, '11:00:00'::time,
           true, NOW(), NOW()
       );

-- reservation 10,000건 (COMPLETED 상태)
-- 100명 고객 × 100건씩 = 10,000건
INSERT INTO reservation (
    id, customer_id, business_id, menu_id, booking_slot_id,
    reservation_date, reservation_time,
    reservation_price, reservation_duration,
    customer_name, customer_phone,
    status, created_at, updated_at
)
SELECT
    ('d0000000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    ('c0000000-0000-0000-0000-' || LPAD(((i - 1) % 100 + 1)::text, 12, '0'))::uuid,  -- 100명 순환
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000400',
    '99999999-0000-0000-0000-000000000500',
    CURRENT_DATE - (i || ' days')::interval,
    '10:00:00'::time,
    50000, 60,
    'Customer ' || ((i - 1) % 100 + 1),
    '010-1234-5678',
    'COMPLETED',
    NOW() - (i || ' days')::interval,
    NOW()
FROM generate_series(1, 10000) AS i;

-- review 10,000건 생성
-- 같은 업체(business_id)의 리뷰 10,000건
-- 9,000건은 활성, 1,000건은 soft delete (deleted_at)
INSERT INTO review (
    id, business_id, user_id, reservation_id,
    menu_name, rating, comment,
    deleted_at, created_at, updated_at
)
SELECT
    ('e0000000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    '99999999-0000-0000-0000-000000000100',  -- 같은 업체
    ('c0000000-0000-0000-0000-' || LPAD(((i - 1) % 100 + 1)::text, 12, '0'))::uuid,  -- 100명 순환
    ('d0000000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    'Test Service',
    (i % 5) + 1,  -- rating 1-5 순환
    'Review comment ' || i || ' - Great service!',
    CASE
        WHEN i <= 1000 THEN NOW() - (i || ' days')::interval  -- 처음 1000건은 삭제됨
        ELSE NULL  -- 나머지 9000건은 활성
        END,
    NOW() - (i || ' days')::interval,
    NOW()
FROM generate_series(1, 10000) AS i;

-- ============================================================
-- EXPLAIN: 업체별 리뷰 목록 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    id, business_id, user_id, reservation_id,
    menu_name, rating, comment,
    created_at, updated_at
FROM review
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND deleted_at IS NULL
ORDER BY created_at DESC;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    - 10,000건 규모에서 어떤 스캔 선택?
--    - business_id 인덱스 필요성 확인
--
-- ✅ Index Cond vs Filter
--    - business_id: Index Cond 목표
--    - deleted_at IS NULL: Filter 예상
--
-- ✅ Sort 노드
--    - ORDER BY created_at DESC
--    - 9,000건 정렬 비용 측정
--
-- ✅ Rows Removed by Filter
--    - deleted_at IS NULL로 1,000건 제거
--    - 활성 9,000건 반환
--
-- ✅ Buffers
--    - 10,000건 규모의 I/O 비용
--    - 캐시 히트율 확인
--
-- ✅ Execution Time
--    - 9,000건 조회 + 정렬 시간
--    - 인덱스 추가 전 baseline

ROLLBACK;