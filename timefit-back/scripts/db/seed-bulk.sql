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
    address, contact_phone, description, logo_url, business_notice,
    is_active, average_rating, review_count, latitude, longitude,
    created_at, updated_at
)
SELECT
    ('30000000-0000-0000-0000-' || LPAD(seq::text, 12, '0'))::uuid,
    CASE
        WHEN seq % 5 = 1 THEN '타임핏 헤어샵 ' || seq
        WHEN seq % 5 = 2 THEN '타임핏 네일샵 ' || seq
        WHEN seq % 5 = 3 THEN '타임핏 카페 ' || seq
        WHEN seq % 5 = 4 THEN '타임핏 레스토랑 ' || seq
        ELSE '타임핏 피부과 ' || seq
        END,
    LPAD((100000000 + seq * 11111)::text, 10, '0'),
    'Owner ' || seq,
    'Seoul Gangnam ' || (seq * 100) || ' Street',
    '02' || LPAD((11110000 + seq)::text, 8, '0'),
    'Performance Test Business ' || seq,
    NULL,
    '시설 이용 안내사항',
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
                WHEN 1 THEN '컷' WHEN 2 THEN '펌' WHEN 3 THEN '염색'
                WHEN 4 THEN '클리닉' ELSE '스타일링'
                END
        WHEN biz_seq % 5 = 2 THEN
            CASE cat_seq
                WHEN 1 THEN '젤네일' WHEN 2 THEN '아트' WHEN 3 THEN '케어'
                WHEN 4 THEN '페디큐어' ELSE '네일팁'
                END
        WHEN biz_seq % 5 = 3 THEN
            CASE cat_seq
                WHEN 1 THEN '커피' WHEN 2 THEN '디저트' WHEN 3 THEN '브런치'
                WHEN 4 THEN '티' ELSE '스무디'
                END
        WHEN biz_seq % 5 = 4 THEN
            CASE cat_seq
                WHEN 1 THEN '파스타' WHEN 2 THEN '피자' WHEN 3 THEN '스테이크'
                WHEN 4 THEN '샐러드' ELSE '디저트'
                END
        ELSE
            CASE cat_seq
                WHEN 1 THEN '레이저' WHEN 2 THEN '필러' WHEN 3 THEN '보톡스'
                WHEN 4 THEN '리프팅' ELSE '스킨케어'
                END
        END,
    '카테고리별 상세 안내사항',
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
    '서비스 ' || menu_seq || '번',
    CASE
        WHEN menu_seq = 1 THEN 20000
        WHEN menu_seq = 2 THEN 35000
        WHEN menu_seq = 3 THEN 50000
        ELSE 80000
        END,
    '메뉴 상세 설명 ' || menu_seq || '번',
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
    CURRENT_DATE - ((res_seq % 60) || ' days')::INTERVAL,
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
    '고객 요청사항',
    CASE
        WHEN res_seq % 20 >= 18 THEN NOW() - ((res_seq % 10) || ' days')::INTERVAL
        ELSE NULL
        END,
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
    '서비스 ' || (1 + (res_seq % 4)) || '번',
    CASE
        WHEN res_seq % 10 < 2 THEN 3
        WHEN res_seq % 10 < 5 THEN 4
        ELSE 5
        END,
    '만족스러운 서비스였습니다.',
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
        RAISE NOTICE 'Seed Data 생성 완료!';
        RAISE NOTICE '===========================================';
        RAISE NOTICE 'Users:        % 명', user_count;
        RAISE NOTICE 'Businesses:   % 개', business_count;
        RAISE NOTICE 'Menus:        % 개', menu_count;
        RAISE NOTICE 'BookingSlots: % 개', slot_count;
        RAISE NOTICE 'Reservations: % 건', reservation_count;
        RAISE NOTICE 'Reviews:      % 건', review_count;
        RAISE NOTICE 'Wishlists:    % 건', wishlist_count;
        RAISE NOTICE '===========================================';
        RAISE NOTICE '성능 테스트 준비 완료!';
        RAISE NOTICE '===========================================';
    END $$;