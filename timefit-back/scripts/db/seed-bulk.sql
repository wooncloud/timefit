-- ============================================================
-- Timefit 대규모 성능 테스트용 Seed Data (Fixed)
-- ============================================================
-- 목적: 로컬/개발 환경에서 실제 부하를 시뮬레이션하기 위한 대량 데이터 생성
-- 규모:
--   - Users: 220명 (고객 200명 + 사업자 20명)
--   - Businesses: 20개 (다양한 업종)
--   - Menus: 400개 (Business당 20개)
--   - BookingSlots: ~18,000개 (RESERVATION_BASED Menu당 60개)
--   - Reservations: ~20,000건 (Business당 1,000건)
--   - Reviews: ~2,000건 (COMPLETED Reservation의 50%)
--   - Wishlists: ~1,000건 (고객당 5건)
--
-- 실행 순서: FK 제약 조건을 고려한 의존성 순서대로 삽입
-- 멱등성: ON CONFLICT DO NOTHING으로 재실행 시 중복 방지
-- ============================================================

-- ========================================
-- 0. Extensions & Setup
-- ========================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ========================================
-- 1. Users (220명: 고객 200명 + 사업자 20명)
-- ========================================
-- 고객: 1~200번 (USER)
-- 사업자: 201~220번 (BUSINESS)
-- ========================================

INSERT INTO users (id, email, password_hash, name, phone_number, role, created_at, updated_at)
SELECT
    ('10000000-0000-0000-0000-' || LPAD(seq::text, 12, '0'))::uuid,
    CASE
        WHEN seq <= 200 THEN 'customer' || seq || '@timefit.test'
        ELSE 'owner' || (seq - 200) || '@timefit.test'
        END,
    '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW',
    CASE
        WHEN seq <= 200 THEN 'Customer ' || seq
        ELSE 'Owner ' || (seq - 200)
        END,
    '010' || LPAD((1000 + seq)::text, 8, '0'),
    CASE
        WHEN seq <= 200 THEN 'USER'
        ELSE 'BUSINESS'
        END,
    NOW() - (seq || ' days')::INTERVAL,
    NOW() - (seq || ' days')::INTERVAL
FROM generate_series(1, 220) AS seq
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 2. Businesses (20개)
-- ========================================

INSERT INTO business (
    id, business_name, business_number, owner_name,
    address, contact_phone, contact_email, description, logo_url, business_notice,
    is_active, average_rating, review_count, latitude, longitude,
    created_at, updated_at
)
SELECT
    ('30000000-0000-0000-0000-' || LPAD(seq::text, 12, '0'))::uuid,
    CASE
        WHEN seq % 5 = 1 THEN 'Timefit Hair Salon ' || seq
        WHEN seq % 5 = 2 THEN 'Timefit Nail Shop ' || seq
        WHEN seq % 5 = 3 THEN 'Timefit Cafe ' || seq
        WHEN seq % 5 = 4 THEN 'Timefit Restaurant ' || seq
        ELSE 'Timefit Clinic ' || seq
        END,
    -- business_number: XXX-XX-XXXXX 형식
    SUBSTRING(LPAD((100000000 + seq * 11111)::text, 10, '0'), 1, 3) || '-' ||
    SUBSTRING(LPAD((100000000 + seq * 11111)::text, 10, '0'), 4, 2) || '-' ||
    SUBSTRING(LPAD((100000000 + seq * 11111)::text, 10, '0'), 6, 5),
    'Owner ' || seq,
    'Seoul Gangnam ' || (seq * 100) || ' Street',
    -- contact_phone: 02-XXXX-XXXX 형식 (10자리)
    '02-' || SUBSTRING(LPAD((11110000 + seq)::text, 8, '0'), 1, 4) || '-' ||
    SUBSTRING(LPAD((11110000 + seq)::text, 8, '0'), 5, 4),
    'owner' || seq || '@timefit.test',
    'Performance Test Business ' || seq,
    NULL,
    'Please follow facility guidelines.',
    true,
    0.0,
    0,
    37.4979 + (seq * 0.001),
    127.0276 + (seq * 0.001),
    NOW() - (seq || ' days')::INTERVAL,
    NOW() - (seq || ' days')::INTERVAL
FROM generate_series(1, 20) AS seq
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 3. BusinessType (업종 매핑)
-- ========================================

INSERT INTO business_type (business_id, type_code)
SELECT
    ('30000000-0000-0000-0000-' || LPAD(seq::text, 12, '0'))::uuid,
    CASE
        WHEN seq % 5 = 1 THEN 'BD008'
        WHEN seq % 5 = 2 THEN 'BD008'
        WHEN seq % 5 = 3 THEN 'BD001'
        WHEN seq % 5 = 4 THEN 'BD000'
        ELSE 'BD007'
        END
FROM generate_series(1, 20) AS seq
ON CONFLICT (business_id, type_code) DO NOTHING;

-- ========================================
-- 4. UserBusinessRole (소유자-업체 매핑)
-- ========================================

INSERT INTO user_business_role (id, user_id, business_id, role, is_active, joined_at, created_at, updated_at)
SELECT
    ('40000000-0000-0000-0000-' || LPAD(seq::text, 12, '0'))::uuid,
    ('10000000-0000-0000-0000-' || LPAD((200 + seq)::text, 12, '0'))::uuid,
    ('30000000-0000-0000-0000-' || LPAD(seq::text, 12, '0'))::uuid,
    'OWNER',
    true,
    NOW() - (seq || ' days')::INTERVAL,
    NOW() - (seq || ' days')::INTERVAL,
    NOW() - (seq || ' days')::INTERVAL
FROM generate_series(1, 20) AS seq
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 5. BusinessCategory (카테고리: 100개)
-- ========================================

INSERT INTO business_category (
    id, business_id, business_type, category_name, category_notice, is_active, created_at, updated_at
)
SELECT
    ('50000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD(cat_seq::text, 4, '0') || '-0000-000000000000')::uuid,
    ('30000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    CASE
        WHEN biz_seq % 5 = 1 THEN 'BD008'
        WHEN biz_seq % 5 = 2 THEN 'BD008'
        WHEN biz_seq % 5 = 3 THEN 'BD001'
        WHEN biz_seq % 5 = 4 THEN 'BD000'
        ELSE 'BD007'
        END,
    CASE
        WHEN biz_seq % 5 = 1 THEN
            CASE cat_seq
                WHEN 1 THEN 'Cut' WHEN 2 THEN 'Perm' WHEN 3 THEN 'Coloring'
                WHEN 4 THEN 'Clinic' ELSE 'Styling'
                END
        WHEN biz_seq % 5 = 2 THEN
            CASE cat_seq
                WHEN 1 THEN 'Gel Nail' WHEN 2 THEN 'Nail Art' WHEN 3 THEN 'Care'
                WHEN 4 THEN 'Pedicure' ELSE 'Nail Tip'
                END
        WHEN biz_seq % 5 = 3 THEN
            CASE cat_seq
                WHEN 1 THEN 'Coffee' WHEN 2 THEN 'Dessert' WHEN 3 THEN 'Brunch'
                WHEN 4 THEN 'Tea' ELSE 'Smoothie'
                END
        WHEN biz_seq % 5 = 4 THEN
            CASE cat_seq
                WHEN 1 THEN 'Pasta' WHEN 2 THEN 'Pizza' WHEN 3 THEN 'Steak'
                WHEN 4 THEN 'Salad' ELSE 'Dessert'
                END
        ELSE
            CASE cat_seq
                WHEN 1 THEN 'Laser' WHEN 2 THEN 'Filler' WHEN 3 THEN 'Botox'
                WHEN 4 THEN 'Lifting' ELSE 'Skincare'
                END
        END,
    'Please check category details.',
    true,
    NOW() - (biz_seq || ' days')::INTERVAL,
    NOW() - (biz_seq || ' days')::INTERVAL
FROM
    generate_series(1, 20) AS biz_seq,
    generate_series(1, 5) AS cat_seq
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 6. Menu (메뉴: 400개)
-- ========================================
-- 중요: ONDEMAND_BASED도 duration_minutes 설정 필요
--   - RESERVATION_BASED (1~3번): 60분
--   - ONDEMAND_BASED (4번): 30분
-- ========================================

INSERT INTO menu (
    id, business_id, business_category_id, service_name, price, description,
    order_type, duration_minutes, image_url, is_active, created_at, updated_at
)
SELECT
    ('60000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD(cat_seq::text, 4, '0') || '-' || LPAD(menu_seq::text, 4, '0') || '-000000000000')::uuid,
    ('30000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    ('50000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD(cat_seq::text, 4, '0') || '-0000-000000000000')::uuid,
    'Service ' || menu_seq,
    CASE
        WHEN menu_seq = 1 THEN 20000
        WHEN menu_seq = 2 THEN 35000
        WHEN menu_seq = 3 THEN 50000
        ELSE 80000
        END,
    'Menu description ' || menu_seq,
    CASE
        WHEN menu_seq <= 3 THEN 'RESERVATION_BASED'
        ELSE 'ONDEMAND_BASED'
        END,
    CASE
        WHEN menu_seq <= 3 THEN 60
        ELSE 30  -- ONDEMAND_BASED도 duration 필요
        END,
    NULL,
    true,
    NOW() - (biz_seq || ' days')::INTERVAL,
    NOW() - (biz_seq || ' days')::INTERVAL
FROM
    generate_series(1, 20) AS biz_seq,
    generate_series(1, 5) AS cat_seq,
    generate_series(1, 4) AS menu_seq
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 7. OperatingHours (영업시간: 280개)
-- ========================================

INSERT INTO operating_hours (
    id, business_id, day_of_week, open_time, close_time, is_closed, sequence, created_at, updated_at
)
SELECT
    ('70000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD(day::text, 2, '0') || LPAD(time_seq::text, 2, '0') || '-0000-000000000000')::uuid,
    ('30000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    day,
    CASE WHEN time_seq = 0 THEN '09:00:00'::time ELSE '14:00:00'::time END,
    CASE WHEN time_seq = 0 THEN '12:00:00'::time ELSE '18:00:00'::time END,
    false,
    time_seq,
    NOW() - (biz_seq || ' days')::INTERVAL,
    NOW() - (biz_seq || ' days')::INTERVAL
FROM
    generate_series(1, 20) AS biz_seq,
    generate_series(0, 6) AS day,
    generate_series(0, 1) AS time_seq
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 8. BookingSlot (예약 슬롯: 18,000개)
-- ========================================

INSERT INTO booking_slot (
    id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at
)
SELECT
    ('80000000-' ||
     LPAD(biz_seq::text, 4, '0') || '-' ||
     LPAD(cat_seq::text, 2, '0') || LPAD(menu_seq::text, 2, '0') || '-' ||
     LPAD(day_offset::text, 2, '0') || LPAD(slot_seq::text, 2, '0') || '-' ||
     '000000000000')::uuid,
    ('30000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    ('60000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD(cat_seq::text, 4, '0') || '-' || LPAD(menu_seq::text, 4, '0') || '-000000000000')::uuid,
    CURRENT_DATE + (day_offset || ' days')::INTERVAL,
    CASE WHEN slot_seq = 0 THEN '09:00:00'::time ELSE '14:00:00'::time END,
    CASE WHEN slot_seq = 0 THEN '10:00:00'::time ELSE '15:00:00'::time END,
    true,
    NOW(),
    NOW()
FROM
    generate_series(1, 20) AS biz_seq,
    generate_series(1, 5) AS cat_seq,
    generate_series(1, 3) AS menu_seq,
    generate_series(1, 30) AS day_offset,
    generate_series(0, 1) AS slot_seq
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 9. Reservation (예약: 20,000건)
-- ========================================
-- 중요: reservation_duration은 NOT NULL 제약
--   - RESERVATION_BASED: 60분
--   - ONDEMAND_BASED: 30분
-- ========================================

INSERT INTO reservation (
    id, customer_id, business_id, menu_id, booking_slot_id,
    reservation_date, reservation_time, reservation_number,
    reservation_price, reservation_duration,
    customer_name, customer_phone, status, notes, cancelled_at,
    snapshot_business_name, snapshot_menu_name,
    created_at, updated_at
)
SELECT
    ('90000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD(res_seq::text, 4, '0') || '-0000-000000000000')::uuid,
    ('10000000-0000-0000-0000-' || LPAD((1 + (res_seq % 200))::text, 12, '0'))::uuid,
    ('30000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    ('60000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD((1 + (res_seq % 5))::text, 4, '0') || '-' || LPAD((1 + (res_seq % 4))::text, 4, '0') || '-000000000000')::uuid,
    CASE
        WHEN (1 + (res_seq % 4)) <= 3 THEN
            ('80000000-' ||
             LPAD(biz_seq::text, 4, '0') || '-' ||
             LPAD((1 + (res_seq % 5))::text, 2, '0') || LPAD((1 + (res_seq % 3))::text, 2, '0') || '-' ||
             LPAD((1 + (res_seq % 30))::text, 2, '0') || LPAD((res_seq % 2)::text, 2, '0') || '-' ||
             '000000000000')::uuid
        ELSE NULL
        END,
    CASE
        WHEN res_seq % 20 < 14 THEN  -- PENDING, CONFIRMED → 미래 날짜 (booking_slot 날짜와 일치)
                    CURRENT_DATE + ((res_seq % 14) || ' days')::INTERVAL
        ELSE                          -- COMPLETED, CANCELLED, NO_SHOW → 과거 날짜
                    CURRENT_DATE - ((1 + res_seq % 30) || ' days')::INTERVAL
        END,
    '09:00:00'::time + ((res_seq % 10) || ' hours')::INTERVAL,
    'RES-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD((biz_seq * 1000 + res_seq)::text, 6, '0'),
    CASE
        WHEN (1 + (res_seq % 4)) = 1 THEN 20000
        WHEN (1 + (res_seq % 4)) = 2 THEN 35000
        WHEN (1 + (res_seq % 4)) = 3 THEN 50000
        ELSE 80000
        END,
    CASE
        WHEN (1 + (res_seq % 4)) <= 3 THEN 60  -- RESERVATION_BASED
        ELSE 30  -- ONDEMAND_BASED
        END,
    'Customer ' || (1 + (res_seq % 200)),
    '010' || LPAD((1001 + (res_seq % 200))::text, 8, '0'),
    CASE
        WHEN res_seq % 20 < 6 THEN 'PENDING'
        WHEN res_seq % 20 < 14 THEN 'CONFIRMED'
        WHEN res_seq % 20 < 18 THEN 'COMPLETED'
        WHEN res_seq % 20 < 19 THEN 'CANCELLED'
        ELSE 'NO_SHOW'
        END,
    'Customer request notes',
    CASE
        WHEN res_seq % 20 >= 18 THEN NOW() - ((res_seq % 10) || ' days')::INTERVAL
        ELSE NULL
        END,
    CASE
        WHEN biz_seq % 5 = 1 THEN 'Timefit Hair Salon ' || biz_seq
        WHEN biz_seq % 5 = 2 THEN 'Timefit Nail Shop ' || biz_seq
        WHEN biz_seq % 5 = 3 THEN 'Timefit Cafe ' || biz_seq
        WHEN biz_seq % 5 = 4 THEN 'Timefit Restaurant ' || biz_seq
        ELSE 'Timefit Clinic ' || biz_seq
        END,
    'Service ' || (1 + (res_seq % 4)),
    NOW() - ((res_seq % 60) || ' days')::INTERVAL,
    NOW() - ((res_seq % 60) || ' days')::INTERVAL
FROM
    generate_series(1, 20) AS biz_seq,
    generate_series(1, 1000) AS res_seq
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 10. Review (리뷰: 2,000건)
-- ========================================

INSERT INTO review (
    id, business_id, user_id, reservation_id, menu_name, rating, comment, deleted_at, created_at, updated_at
)
SELECT
    ('A0000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD(res_seq::text, 4, '0') || '-0000-000000000000')::uuid,
    ('30000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    ('10000000-0000-0000-0000-' || LPAD((1 + (res_seq % 200))::text, 12, '0'))::uuid,
    ('90000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD(res_seq::text, 4, '0') || '-0000-000000000000')::uuid,
    'Service ' || (1 + (res_seq % 4)),
    CASE
        WHEN res_seq % 10 < 2 THEN 3
        WHEN res_seq % 10 < 5 THEN 4
        ELSE 5
        END,
    'Great service, very satisfied.',
    NULL,
    NOW() - ((res_seq % 60) || ' days')::INTERVAL,
    NOW() - ((res_seq % 60) || ' days')::INTERVAL
FROM
    generate_series(1, 20) AS biz_seq,
    generate_series(1, 1000) AS res_seq
WHERE
    (res_seq % 20 >= 14 AND res_seq % 20 < 18)
  AND res_seq % 2 = 0
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 11. Wishlist (찜: 1,000건)
-- ========================================
-- 중요: user_id는 1~200번 고객만 (USER 역할)
-- ========================================

INSERT INTO wishlist (
    id, user_id, business_id, created_at, updated_at
)
SELECT
    ('B0000000-' || LPAD(user_seq::text, 4, '0') || '-' || LPAD(wish_seq::text, 4, '0') || '-0000-000000000000')::uuid,
    ('10000000-0000-0000-0000-' || LPAD(user_seq::text, 12, '0'))::uuid,
    ('30000000-0000-0000-0000-' || LPAD((1 + ((user_seq * wish_seq) % 20))::text, 12, '0'))::uuid,
    NOW() - ((wish_seq || ' days')::INTERVAL),
    NOW() - ((wish_seq || ' days')::INTERVAL)
FROM
    generate_series(1, 200) AS user_seq,
    generate_series(1, 5) AS wish_seq
ON CONFLICT (user_id, business_id) DO NOTHING;

-- ========================================
-- 완료 메시지
-- ========================================

DO $$
    DECLARE
        user_count INTEGER;
        business_count INTEGER;
        menu_count INTEGER;
        slot_count INTEGER;
        reservation_count INTEGER;
        review_count INTEGER;
        wishlist_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO user_count FROM users;
        SELECT COUNT(*) INTO business_count FROM business;
        SELECT COUNT(*) INTO menu_count FROM menu;
        SELECT COUNT(*) INTO slot_count FROM booking_slot;
        SELECT COUNT(*) INTO reservation_count FROM reservation;
        SELECT COUNT(*) INTO review_count FROM review WHERE deleted_at IS NULL;
        SELECT COUNT(*) INTO wishlist_count FROM wishlist;

        RAISE NOTICE '===========================================';
        RAISE NOTICE 'Seed Data generation complete!';
        RAISE NOTICE '===========================================';
        RAISE NOTICE 'Users:        %', user_count;
        RAISE NOTICE 'Businesses:   %', business_count;
        RAISE NOTICE 'Menus:        %', menu_count;
        RAISE NOTICE 'BookingSlots: %', slot_count;
        RAISE NOTICE 'Reservations: %', reservation_count;
        RAISE NOTICE 'Reviews:      %', review_count;
        RAISE NOTICE 'Wishlists:    %', wishlist_count;
        RAISE NOTICE '===========================================';
        RAISE NOTICE 'Performance test data ready!';
        RAISE NOTICE '===========================================';
    END $$;

-- ========================================
-- 12. Test Reservations for Business 1
--     상태별 2건씩 (UI 연동 검증용)
--     business_id: 30000000-0000-0000-0000-000000000001
-- ========================================
-- 설계 원칙:
--   - PENDING / CONFIRMED  → 미래 슬롯 (기존 bulk 슬롯 재사용)
--   - COMPLETED / CANCELLED / NO_SHOW → 과거 슬롯 (별도 생성)
--   - 예약이 붙은 슬롯은 is_available = false 로 UPDATE
--   - day_offset 91~96: 기존 범위(1~30) 외 → ID 충돌 없음
-- ========================================

-- ----------------------------------------
-- Step 1. 과거 BookingSlot 추가 (COMPLETED / CANCELLED / NO_SHOW용)
-- ----------------------------------------
INSERT INTO booking_slot (
    id, business_id, menu_id,
    slot_date, start_time, end_time,
    is_available, created_at, updated_at
)
VALUES
    -- COMPLETED용 (7일 전)
    ('80000000-0001-0101-9100-000000000000',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     CURRENT_DATE - INTERVAL '7 days', '09:00', '10:00', false, NOW(), NOW()),

    ('80000000-0001-0101-9200-000000000000',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     CURRENT_DATE - INTERVAL '7 days', '14:00', '15:00', false, NOW(), NOW()),

    -- CANCELLED용 (5일 전)
    ('80000000-0001-0101-9300-000000000000',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     CURRENT_DATE - INTERVAL '5 days', '09:00', '10:00', false, NOW(), NOW()),

    ('80000000-0001-0101-9400-000000000000',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     CURRENT_DATE - INTERVAL '5 days', '14:00', '15:00', false, NOW(), NOW()),

    -- NO_SHOW용 (3일 전)
    ('80000000-0001-0101-9500-000000000000',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     CURRENT_DATE - INTERVAL '3 days', '09:00', '10:00', false, NOW(), NOW()),

    ('80000000-0001-0101-9600-000000000000',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     CURRENT_DATE - INTERVAL '3 days', '14:00', '15:00', false, NOW(), NOW())

ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------
-- Step 2. 테스트 예약 10건 삽입 (상태별 2건)
--
-- 미래 슬롯 참조 (기존 bulk 생성 슬롯):
--   PENDING  → day=5 : 80000000-0001-0101-0500/0501-000000000000
--   CONFIRMED→ day=10: 80000000-0001-0101-1000/1001-000000000000
-- ----------------------------------------
INSERT INTO reservation (
    id, customer_id, business_id, menu_id, booking_slot_id,
    reservation_date, reservation_time, reservation_number,
    reservation_price, reservation_duration,
    customer_name, customer_phone, status, notes, cancelled_at,
    snapshot_business_name, snapshot_menu_name,
    created_at, updated_at
)
VALUES

    -- ======== PENDING (확정 대기) ========
    ('99000000-0001-0001-0000-000000000000',
     '10000000-0000-0000-0000-000000000001',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-0500-000000000000',
     CURRENT_DATE + INTERVAL '5 days', '09:00',
     'TEST-PENDING-001', 20000, 60,
     'Customer 1', '01010010001', 'PENDING', 'Test note', NULL,
     'Timefit Hair Salon 1', 'Service 1',
     NOW(), NOW()),

    ('99000000-0001-0002-0000-000000000000',
     '10000000-0000-0000-0000-000000000002',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-0501-000000000000',
     CURRENT_DATE + INTERVAL '5 days', '14:00',
     'TEST-PENDING-002', 20000, 60,
     'Customer 2', '01010010002', 'PENDING', NULL, NULL,
     'Timefit Hair Salon 1', 'Service 1',
     NOW(), NOW()),

    -- ======== CONFIRMED (예약 확정) ========
    ('99000000-0002-0001-0000-000000000000',
     '10000000-0000-0000-0000-000000000003',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-1000-000000000000',
     CURRENT_DATE + INTERVAL '10 days', '09:00',
     'TEST-CONFIRMED-001', 20000, 60,
     'Customer 3', '01010010003', 'CONFIRMED', NULL, NULL,
     'Timefit Hair Salon 1', 'Service 1',
     NOW() - INTERVAL '1 day', NOW()),

    ('99000000-0002-0002-0000-000000000000',
     '10000000-0000-0000-0000-000000000004',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-1001-000000000000',
     CURRENT_DATE + INTERVAL '10 days', '14:00',
     'TEST-CONFIRMED-002', 20000, 60,
     'Customer 4', '01010010004', 'CONFIRMED', NULL, NULL,
     'Timefit Hair Salon 1', 'Service 1',
     NOW() - INTERVAL '1 day', NOW()),

    -- ======== COMPLETED (방문 완료) ========
    ('99000000-0003-0001-0000-000000000000',
     '10000000-0000-0000-0000-000000000005',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-9100-000000000000',
     CURRENT_DATE, '09:00',
     'TEST-COMPLETED-001', 20000, 60,
     'Customer 5', '01010010005', 'COMPLETED', NULL, NULL,
     'Timefit Hair Salon 1', 'Service 1',
     NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),

    ('99000000-0003-0002-0000-000000000000',
     '10000000-0000-0000-0000-000000000006',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-9200-000000000000',
     CURRENT_DATE, '14:00',
     'TEST-COMPLETED-002', 20000, 60,
     'Customer 6', '01010010006', 'COMPLETED', NULL, NULL,
     'Timefit Hair Salon 1', 'Service 1',
     NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),

    -- ======== CANCELLED (예약 취소) ========
    ('99000000-0004-0001-0000-000000000000',
     '10000000-0000-0000-0000-000000000007',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-9300-000000000000',
     CURRENT_DATE, '09:00',
     'TEST-CANCELLED-001', 20000, 60,
     'Customer 7', '01010010007', 'CANCELLED', 'Personal reason',
     NOW() - INTERVAL '5 days' - INTERVAL '6 hours',
     'Timefit Hair Salon 1', 'Service 1',
     NOW() - INTERVAL '6 days', NOW() - INTERVAL '5 days'),

    ('99000000-0004-0002-0000-000000000000',
     '10000000-0000-0000-0000-000000000008',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-9400-000000000000',
     CURRENT_DATE, '14:00',
     'TEST-CANCELLED-002', 20000, 60,
     'Customer 8', '01010010008', 'CANCELLED', NULL,
     NOW() - INTERVAL '5 days' - INTERVAL '3 hours',
     'Timefit Hair Salon 1', 'Service 1',
     NOW() - INTERVAL '6 days', NOW() - INTERVAL '5 days'),

    -- ======== NO_SHOW (노쇼) ========
    ('99000000-0005-0001-0000-000000000000',
     '10000000-0000-0000-0000-000000000009',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-9500-000000000000',
     CURRENT_DATE, '09:00',
     'TEST-NOSHOW-001', 20000, 60,
     'Customer 9', '01010010009', 'NO_SHOW', NULL, NULL,
     'Timefit Hair Salon 1', 'Service 1',
     NOW() - INTERVAL '4 days', NOW() - INTERVAL '3 days'),

    ('99000000-0005-0002-0000-000000000000',
     '10000000-0000-0000-0000-000000000010',
     '30000000-0000-0000-0000-000000000001',
     '60000000-0001-0001-0001-000000000000',
     '80000000-0001-0101-9600-000000000000',
     CURRENT_DATE, '14:00',
     'TEST-NOSHOW-002', 20000, 60,
     'Customer 10', '01010010010', 'NO_SHOW', NULL, NULL,
     'Timefit Hair Salon 1', 'Service 1',
     NOW() - INTERVAL '4 days', NOW() - INTERVAL '3 days')

ON CONFLICT (id) DO NOTHING;

-- ----------------------------------------
-- Step 3. 예약이 붙은 미래 슬롯 → is_available = false
--   (PENDING / CONFIRMED 슬롯만 해당;
--    과거 슬롯은 Step 1에서 이미 false로 삽입)
-- ----------------------------------------
UPDATE booking_slot
SET is_available = false, updated_at = NOW()
WHERE id IN (
             '80000000-0001-0101-0500-000000000000',  -- PENDING-001
             '80000000-0001-0101-0501-000000000000',  -- PENDING-002
             '80000000-0001-0101-1000-000000000000',  -- CONFIRMED-001
             '80000000-0001-0101-1001-000000000000'   -- CONFIRMED-002
    );

-- ========================================
-- 13. BusinessCustomerMemo (업체-고객 메모)
-- ========================================
-- 대상: Business 1~5번
-- 고객: 1~100번 중 홀수 (50건/업체, 전체 250건)
-- 비즈니스 규칙:
--   - (business_id, customer_id) UNIQUE → 멱등성 ON CONFLICT (business_id, customer_id) DO NOTHING
--   - 고객 목록은 reservation 기반이므로 메모 없는 고객도 목록에 노출됨
--   - 일부 고객(짝수번)은 의도적으로 메모 미보유
-- ID: a0000000-{biz:4}-{cust:4}-0000-000000000000
-- ========================================

INSERT INTO business_customer_memo (
    id, business_id, customer_id, memo, created_at, updated_at
)
SELECT
    ('a0000000-' || LPAD(biz_seq::text, 4, '0') || '-' || LPAD(cust_seq::text, 4, '0') || '-0000-000000000000')::uuid,
    ('30000000-0000-0000-0000-' || LPAD(biz_seq::text, 12, '0'))::uuid,
    ('10000000-0000-0000-0000-' || LPAD(cust_seq::text, 12, '0'))::uuid,
    CASE biz_seq
        WHEN 1 THEN  -- Hair Salon
            CASE (cust_seq % 5)
                WHEN 1 THEN 'Regular customer. Prefers feather-light trim on bangs.'
                WHEN 3 THEN 'Sensitive scalp — avoid strong chemical treatments.'
                ELSE        'VIP. Visits every 4 weeks, always books morning slot.'
                END
        WHEN 2 THEN  -- Nail Shop
            CASE (cust_seq % 5)
                WHEN 1 THEN 'Prefers short natural nails, no extensions.'
                WHEN 3 THEN 'Allergic to acrylic powder — gel only.'
                ELSE        'Loyal customer. Often requests seasonal nail art.'
                END
        WHEN 3 THEN  -- Cafe
            CASE (cust_seq % 5)
                WHEN 1 THEN 'Nut allergy. Always double-check menu ingredients.'
                WHEN 3 THEN 'Oat milk substitute, no syrup.'
                ELSE        'Frequent visitor, prefers window seat.'
                END
        WHEN 4 THEN  -- Restaurant
            CASE (cust_seq % 5)
                WHEN 1 THEN 'Vegan. No meat or dairy in any dish.'
                WHEN 3 THEN 'Gluten intolerance — confirm with kitchen.'
                ELSE        'Corporate client, usually group reservation of 4+.'
                END
        ELSE          -- Clinic (seq=5, biz%5=0)
            CASE (cust_seq % 5)
                WHEN 1 THEN 'Keloid history — avoid aggressive laser settings.'
                WHEN 3 THEN 'First-time botox. Needs consultation before treatment.'
                ELSE        'Long-term patient. Quarterly skin care program.'
                END
        END,
    NOW() - ((biz_seq + cust_seq) % 30 || ' days')::INTERVAL,
    NOW() - ((biz_seq + cust_seq) % 30 || ' days')::INTERVAL
FROM
    generate_series(1, 5)   AS biz_seq,
    generate_series(1, 100) AS cust_seq
WHERE cust_seq % 2 = 1   -- 홀수 고객만 (50%) → 메모 없는 고객도 목록에 나오도록
ON CONFLICT (business_id, customer_id) DO NOTHING;