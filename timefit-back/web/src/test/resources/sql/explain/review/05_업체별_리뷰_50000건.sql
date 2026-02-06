-- ============================================================
-- 업체별 리뷰 목록 조회 (대용량 스트레스 테스트)
-- ============================================================
-- API:        GET /api/public/business/{id}/reviews
-- 핵심 쿼리:  SELECT * FROM review WHERE business_id = ? AND deleted_at IS NULL
-- 사전조건:   _setup.sql (User, Business)
-- 데이터:     50,000건 (45,000 활성 + 5,000 삭제)
-- ============================================================

BEGIN;

-- ============================================================
-- 픽스처
-- ============================================================

-- 추가 고객 500명 생성
INSERT INTO users (
    id, email, name, phone_number, role,
    created_at, updated_at
)
SELECT
    ('e0000000-0000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    'customer' || i || '@test.com',
    'Customer ' || i,
    '010-' || LPAD((2000 + i)::text, 4, '0') || '-' || LPAD((i * 10)::text, 4, '0'),
    'USER',
    NOW(),
    NOW()
FROM generate_series(1, 500) AS i;

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

-- reservation 50,000건 (COMPLETED 상태)
-- 500명 고객 × 100건씩 = 50,000건
INSERT INTO reservation (
    id, customer_id, business_id, menu_id, booking_slot_id,
    reservation_date, reservation_time,
    reservation_price, reservation_duration,
    customer_name, customer_phone,
    status, created_at, updated_at
)
SELECT
    ('f0000000-0000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    ('e0000000-0000-0000-0000-' || LPAD(((i - 1) % 500 + 1)::text, 12, '0'))::uuid,
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000400',
    '99999999-0000-0000-0000-000000000500',
    CURRENT_DATE - ((i / 10) || ' days')::interval,  -- 10건씩 같은 날짜
    '10:00:00'::time,
    50000, 60,
    'Customer ' || ((i - 1) % 500 + 1),
    '010-1234-5678',
    'COMPLETED',
    NOW() - ((i / 10) || ' days')::interval,
    NOW()
FROM generate_series(1, 50000) AS i;

-- review 50,000건 생성
-- 45,000건 활성 + 5,000건 삭제
INSERT INTO review (
    id, business_id, user_id, reservation_id,
    menu_name, rating, comment,
    deleted_at, created_at, updated_at
)
SELECT
    ('f1000000-0000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    '99999999-0000-0000-0000-000000000100',
    ('e0000000-0000-0000-0000-' || LPAD(((i - 1) % 500 + 1)::text, 12, '0'))::uuid,
    ('f0000000-0000-0000-0000-' || LPAD(i::text, 12, '0'))::uuid,
    'Test Service',
    (i % 5) + 1,  -- rating 1-5
    'Review ' || i || ': ' ||
    CASE (i % 10)
        WHEN 0 THEN 'Excellent service! Highly recommend.'
        WHEN 1 THEN 'Good experience overall.'
        WHEN 2 THEN 'Satisfactory, but could be better.'
        WHEN 3 THEN 'Average service.'
        WHEN 4 THEN 'Not bad, would visit again.'
        WHEN 5 THEN 'Great atmosphere and staff.'
        WHEN 6 THEN 'Professional and efficient.'
        WHEN 7 THEN 'Very clean and comfortable.'
        WHEN 8 THEN 'Worth the price.'
        ELSE 'Friendly staff, good value.'
        END,
    CASE
        WHEN i <= 5000 THEN NOW() - ((i / 10) || ' days')::interval
        ELSE NULL
        END,
    NOW() - ((i / 10) || ' days')::interval,
    NOW()
FROM generate_series(1, 50000) AS i;

-- ============================================================
-- EXPLAIN: 업체별 리뷰 목록 조회 (50,000건)
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    id, business_id, user_id, reservation_id,
    menu_name, rating, comment,
    created_at, updated_at
FROM review
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND deleted_at IS NULL
ORDER BY created_at DESC
LIMIT 100;  -- 페이징 적용

-- ============================================================
-- 확인 포인트
-- ============================================================
-- ✅ 대용량 데이터에서 스캔 타입
--    - 50,000건 → 45,000건 활성
--    - Seq Scan vs Bitmap Scan vs Index Scan
--
-- ✅ Sort 노드 비용
--    - 45,000건 정렬 시간
--    - work_mem 초과 시 디스크 사용 가능
--
-- ✅ LIMIT 효과
--    - LIMIT 100으로 early termination
--    - Index Scan이면 ~120건만 읽고 멈춤
--    - Seq/Bitmap이면 전체 읽고 정렬 후 100건 반환
--
-- ✅ Buffers
--    - 대용량 I/O 비용
--    - shared read (디스크) vs hit (캐시)
--
-- ✅ Planning vs Execution Time
--    - 대용량에서 플래닝 비용
--
-- ✅ 인덱스 필요성 명확히 확인
--    - Before: Seq Scan + Sort (느림)
--    - After: Index Scan + LIMIT (빠름)

-- ============================================================
-- 추가 테스트: LIMIT 없이 전체 조회
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

-- 전체 45,000건 조회 + 정렬 시간 측정

ROLLBACK;