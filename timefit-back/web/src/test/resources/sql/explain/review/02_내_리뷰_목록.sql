-- ============================================================
-- 내 리뷰 목록 조회
-- ============================================================
-- API:        GET /api/customer/review/my
-- 핵심 쿼리:  SELECT * FROM review WHERE user_id = ? AND deleted_at IS NULL
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 픽스처
-- ============================================================

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

-- reservation 1000건 (COMPLETED 상태)
-- 이유: Review는 COMPLETED 예약이 필요
INSERT INTO reservation (
    id, customer_id, business_id, menu_id, booking_slot_id,
    reservation_date, reservation_time,
    reservation_price, reservation_duration,
    customer_name, customer_phone,
    status, created_at, updated_at
)
SELECT
    ('a0000000-0000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    '99999999-0000-0000-0000-000000000001',  -- 같은 고객
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000400',
    '99999999-0000-0000-0000-000000000500',
    CURRENT_DATE - (i || ' days')::interval,
    '10:00:00'::time,
    50000, 60,
    'Test Customer', '010-1234-5678',
    'COMPLETED',
    NOW() - (i || ' days')::interval,
    NOW()
FROM generate_series(1, 1000) AS i;

-- review 1000건 생성
-- 같은 고객(user_id)의 리뷰 1000건
-- 900건은 활성, 100건은 soft delete (deleted_at)
INSERT INTO review (
    id, business_id, user_id, reservation_id,
    menu_name, rating, comment,
    deleted_at, created_at, updated_at
)
SELECT
    ('b0000000-0000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000001',  -- 같은 고객
    ('a0000000-0000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    'Test Service',
    (i % 5) + 1,  -- rating 1-5 순환
    'Test review comment ' || i,
    CASE
        WHEN i <= 100 THEN NOW() - (i || ' days')::interval  -- 처음 100건은 삭제됨
        ELSE NULL  -- 나머지 900건은 활성
        END,
    NOW() - (i || ' days')::interval,
    NOW()
FROM generate_series(1, 1000) AS i;

-- ============================================================
-- EXPLAIN: 내 리뷰 목록 조회
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    id, business_id, user_id, reservation_id,
    menu_name, rating, comment,
    created_at, updated_at
FROM review
WHERE user_id = '99999999-0000-0000-0000-000000000001'
  AND deleted_at IS NULL
ORDER BY created_at DESC;

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    - user_id에 인덱스가 있으면 Index/Bitmap 선택
--    - deleted_at IS NULL 조건 처리 방법
--
-- ✅ Index Cond vs Filter
--    - user_id: Index Cond 목표
--    - deleted_at: Filter 예상 (IS NULL은 인덱스 비효율)
--
-- ✅ Sort 노드 존재 여부
--    - ORDER BY created_at DESC
--    - 인덱스가 정렬 순서와 맞지 않으면 Sort 발생
--
-- ✅ Rows Removed by Filter
--    - deleted_at IS NULL로 100건 제거
--    - 활성 900건 반환
--
-- ✅ actual rows (900건) vs estimated rows
--    - Planner 예측 정확도 확인

ROLLBACK;