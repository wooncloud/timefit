-- ========================================
-- Timefit Bulk Test Data v2
-- For Level 3 Performance Testing
-- ========================================
-- Data Scale:
--   Users: 100 (3 business owners + 97 customers)
--   Business: 3
--   Menu: 30
--   BookingSlot: ~3,000 (CURRENT_DATE ~ +60 days)
--   Reservation: 10,000
--   Date Range: TODAY ~ +60 DAYS (No past dates!)
-- ========================================

-- Enable pgcrypto extension (for UUID generation)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ========================================
-- 1. Users (100 people)
-- ========================================

-- Business Owners (3) - Same as seed-minimal.sql
INSERT INTO users (id, email, password_hash, name, phone_number, role, created_at, updated_at) VALUES
                                                                                                   ('10000000-0000-0000-0000-000000000001', 'owner1@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Owner Kim', '01011111111', 'BUSINESS', NOW(), NOW()),
                                                                                                   ('10000000-0000-0000-0000-000000000002', 'owner2@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Owner Lee', '01011112222', 'BUSINESS', NOW(), NOW()),
                                                                                                   ('10000000-0000-0000-0000-000000000003', 'owner3@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Owner Park', '01011113333', 'BUSINESS', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET password_hash = EXCLUDED.password_hash;

-- Customers (97) - Generated with series
INSERT INTO users (id, email, password_hash, name, phone_number, role, created_at, updated_at)
SELECT
    ('20000000-0000-0000-0000-0000000000' || LPAD(num::text, 2, '0'))::uuid,
    'customer' || num || '@timefit.com',
    '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW',
    'Customer ' || num,
    '0102222' || LPAD(num::text, 4, '0'),
    'USER',
    NOW(),
    NOW()
FROM generate_series(1, 97) AS num
ON CONFLICT (id) DO UPDATE SET password_hash = EXCLUDED.password_hash;

-- ========================================
-- 2. Business (3 businesses) - Same as seed-minimal.sql
-- ========================================
INSERT INTO business (id, business_name, business_number, owner_name, address, contact_phone, description, is_active, created_at, updated_at) VALUES
                                                                                                                                                  ('30000000-0000-0000-0000-000000000001', 'Timefit Hair Salon', '1234567890', 'Owner Kim', 'Seoul Gangnam 123', '0211111111', 'DB Test Hair Salon', true, NOW(), NOW()),
                                                                                                                                                  ('30000000-0000-0000-0000-000000000002', 'Timefit Nail Shop', '2345678901', 'Owner Lee', 'Seoul Gangnam 456', '0222222222', 'DB Test Nail Shop', true, NOW(), NOW()),
                                                                                                                                                  ('30000000-0000-0000-0000-000000000003', 'Timefit Cafe', '3456789012', 'Owner Park', 'Seoul Seocho 789', '0233333333', 'DB Test Cafe', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 3. UserBusinessRole (Owner-Business mapping)
-- ========================================
INSERT INTO user_business_role (id, user_id, business_id, role, is_active, joined_at, created_at, updated_at) VALUES
                                                                                                                  ('40000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'OWNER', true, NOW(), NOW(), NOW()),
                                                                                                                  ('40000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', 'OWNER', true, NOW(), NOW(), NOW()),
                                                                                                                  ('40000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000003', 'OWNER', true, NOW(), NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 4. BusinessType (Business type mapping)
-- ========================================
INSERT INTO business_type (business_id, type_code) VALUES
                                                       ('30000000-0000-0000-0000-000000000001', 'BD008'),
                                                       ('30000000-0000-0000-0000-000000000002', 'BD009'),
                                                       ('30000000-0000-0000-0000-000000000003', 'BD010')
ON CONFLICT (business_id, type_code) DO NOTHING;

-- ========================================
-- 5. BusinessHours (Operating hours)
-- ========================================
-- day_of_week: 0=MONDAY, 1=TUESDAY, ..., 6=SUNDAY (ORDINAL)

-- Business 1 (Hair Salon): Mon-Fri 09:00-18:00 (weekday full hours)
INSERT INTO business_hours (id, business_id, day_of_week, open_time, close_time, is_closed, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    day_num,
    '09:00:00'::time,
    '18:00:00'::time,
    false,
    NOW(),
    NOW()
FROM generate_series(0, 4) AS day_num;  -- 0=MON, 4=FRI

-- Business 1 (Hair Salon): Sat-Sun 09:00-14:00 (weekend shortened hours)
INSERT INTO business_hours (id, business_id, day_of_week, open_time, close_time, is_closed, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    day_num,
    '09:00:00'::time,
    '14:00:00'::time,
    false,
    NOW(),
    NOW()
FROM generate_series(5, 6) AS day_num;  -- 5=SAT, 6=SUN

-- Business 2 (Nail Shop): Mon-Fri 10:00-20:00
INSERT INTO business_hours (id, business_id, day_of_week, open_time, close_time, is_closed, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    day_num,
    '10:00:00'::time,
    '20:00:00'::time,
    false,
    NOW(),
    NOW()
FROM generate_series(0, 4) AS day_num;  -- 0=MON, 4=FRI

-- Business 2 (Nail Shop): Sat-Sun 11:00-18:00
INSERT INTO business_hours (id, business_id, day_of_week, open_time, close_time, is_closed, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    day_num,
    '11:00:00'::time,
    '18:00:00'::time,
    false,
    NOW(),
    NOW()
FROM generate_series(5, 6) AS day_num;  -- 5=SAT, 6=SUN

-- Business 3 (Cafe): Mon-Sun 07:00-22:00
INSERT INTO business_hours (id, business_id, day_of_week, open_time, close_time, is_closed, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000003',
    day_num,
    '07:00:00'::time,
    '22:00:00'::time,
    false,
    NOW(),
    NOW()
FROM generate_series(0, 6) AS day_num;  -- 0=MON, 6=SUN

-- ========================================
-- 6. BusinessCategory (6 categories) - Same as seed-minimal.sql
-- ========================================
INSERT INTO business_category (id, business_id, business_type, category_name, category_notice, is_active, created_at, updated_at) VALUES
                                                                                                                                      ('50000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'BD008', 'Hair', 'Hair Services', true, NOW(), NOW()),
                                                                                                                                      ('50000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', 'BD008', 'Perm Dye', 'Perm and Dye Services', true, NOW(), NOW()),
                                                                                                                                      ('50000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000002', 'BD009', 'Basic Care', 'Basic Nail Care', true, NOW(), NOW()),
                                                                                                                                      ('50000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000002', 'BD009', 'Art', 'Nail Art', true, NOW(), NOW()),
                                                                                                                                      ('50000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000003', 'BD010', 'Coffee', 'Coffee Drinks', true, NOW(), NOW()),
                                                                                                                                      ('50000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000003', 'BD010', 'Dessert', 'Desserts and Baked Goods', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 6.5. OperatingHours (Booking time ranges with break times)
-- Total: 17 rows (Business 1: 12, Business 2: 12, Business 3: 7)
-- ========================================

-- ========== Business 1 (Hair Salon) ==========
-- Mon-Fri Morning session: 09:00-12:00 (sequence=0)
INSERT INTO operating_hours (id, business_id, day_of_week, open_time, close_time, is_closed, sequence, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    day_num,
    '09:00:00'::time,
    '12:00:00'::time,
    false,
    0,  -- Morning session
    NOW(),
    NOW()
FROM generate_series(0, 4) AS day_num;  -- MON-FRI (5 rows)

-- Mon-Fri Afternoon session: 13:00-18:00 (sequence=1)
-- Lunch break 12:00-13:00 excluded!
INSERT INTO operating_hours (id, business_id, day_of_week, open_time, close_time, is_closed, sequence, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    day_num,
    '13:00:00'::time,
    '18:00:00'::time,
    false,
    1,  -- Afternoon session
    NOW(),
    NOW()
FROM generate_series(0, 4) AS day_num;  -- MON-FRI (5 rows)

-- Sat-Sun: 09:00-14:00 (sequence=0, single session, shortened hours)
INSERT INTO operating_hours (id, business_id, day_of_week, open_time, close_time, is_closed, sequence, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    day_num,
    '09:00:00'::time,
    '14:00:00'::time,
    false,
    0,  -- Weekend single session
    NOW(),
    NOW()
FROM generate_series(5, 6) AS day_num;  -- SAT-SUN (2 rows)

-- ========== Business 2 (Nail Shop) ==========
-- Mon-Fri Morning: 10:00-14:00 (sequence=0)
INSERT INTO operating_hours (id, business_id, day_of_week, open_time, close_time, is_closed, sequence, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    day_num,
    '10:00:00'::time,
    '14:00:00'::time,
    false,
    0,
    NOW(),
    NOW()
FROM generate_series(0, 4) AS day_num;  -- MON-FRI (5 rows)

-- Mon-Fri Afternoon: 15:00-20:00 (sequence=1)
-- Break 14:00-15:00!
INSERT INTO operating_hours (id, business_id, day_of_week, open_time, close_time, is_closed, sequence, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    day_num,
    '15:00:00'::time,
    '20:00:00'::time,
    false,
    1,
    NOW(),
    NOW()
FROM generate_series(0, 4) AS day_num;  -- MON-FRI (5 rows)

-- Sat-Sun: 10:00-18:00 (sequence=0, single session)
INSERT INTO operating_hours (id, business_id, day_of_week, open_time, close_time, is_closed, sequence, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    day_num,
    '10:00:00'::time,
    '18:00:00'::time,
    false,
    0,
    NOW(),
    NOW()
FROM generate_series(5, 6) AS day_num;  -- SAT-SUN (2 rows)

-- ========== Business 3 (Cafe - ONDEMAND, no BookingSlots needed) ==========
-- Mon-Sun: 07:00-22:00 (sequence=0, single session)
INSERT INTO operating_hours (id, business_id, day_of_week, open_time, close_time, is_closed, sequence, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000003',
    day_num,
    '07:00:00'::time,
    '22:00:00'::time,
    false,
    0,
    NOW(),
    NOW()
FROM generate_series(0, 6) AS day_num;  -- MON-SUN (7 rows)

-- ========================================
-- 7. Menu (30 menus) - Same as seed-minimal.sql
-- ========================================

-- Business 1 (Hair Salon) - 10 items (RESERVATION_BASED)
INSERT INTO menu (id, business_id, business_category_id, service_name, description, price, duration_minutes, order_type, is_active, created_at, updated_at) VALUES
                                                                                                                                                                ('60000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Basic Haircut', 'Basic Haircut', 15000, 60, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Style Haircut', 'Style Haircut', 25000, 90, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Designer Haircut', 'Designer Haircut', 35000, 90, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000002', 'Digital Perm', 'Digital Perm', 80000, 180, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000002', 'Magic Perm', 'Magic Straight', 90000, 180, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000002', 'Volume Perm', 'Volume Perm', 70000, 150, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Shampoo', 'Basic Shampoo', 5000, 30, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Treatment', 'Hair Treatment', 20000, 60, 'RESERVATION_BASED', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Business 2 (Nail Shop) - 10 items (RESERVATION_BASED)
INSERT INTO menu (id, business_id, business_category_id, service_name, description, price, duration_minutes, order_type, is_active, created_at, updated_at) VALUES
                                                                                                                                                                ('60000000-0000-0000-0000-000000000011', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', 'Basic Nail', 'Basic Nail Care', 20000, 60, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', 'Gel Nail', 'Gel Nail', 35000, 90, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000013', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000004', 'Nail Art', 'Nail Art', 50000, 120, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000014', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000004', 'French Nail', 'French Nail', 40000, 90, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000015', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', 'Pedicure', 'Pedicure', 25000, 60, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000016', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000004', 'Premium Nail Art', 'Premium Nail Art', 70000, 150, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000019', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', 'Nail Care', 'Nail Care', 15000, 45, 'RESERVATION_BASED', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Business 3 (Cafe) - 10 items (ONDEMAND_BASED)
INSERT INTO menu (id, business_id, business_category_id, service_name, description, price, duration_minutes, order_type, is_active, created_at, updated_at) VALUES
                                                                                                                                                                ('60000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000005', 'Americano', 'Basic Americano', 4500, NULL, 'ONDEMAND_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000005', 'Cafe Latte', 'Milk Latte', 5000, NULL, 'ONDEMAND_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000005', 'Cappuccino', 'Cappuccino', 5500, NULL, 'ONDEMAND_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000005', 'Vanilla Latte', 'Vanilla Latte', 5500, NULL, 'ONDEMAND_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000005', 'Caramel Macchiato', 'Caramel Macchiato', 6000, NULL, 'ONDEMAND_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000006', 'Cheesecake', 'Cheese Cake', 7000, NULL, 'ONDEMAND_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000027', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000006', 'Chocolate Cake', 'Chocolate Cake', 7000, NULL, 'ONDEMAND_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000028', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000006', 'Madeleine', 'Madeleine', 3000, NULL, 'ONDEMAND_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000029', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000006', 'Cookie', 'Cookie', 2500, NULL, 'ONDEMAND_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000030', '30000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000006', 'Scone', 'Scone', 3500, NULL, 'ONDEMAND_BASED', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 8. BookingSlot (accurate generation based on OperatingHours sequence + Menu duration)
-- ========================================

-- ========== Business 1 (Hair Salon) ==========
-- Weekday Morning (09:00-12:00, sequence=0) + Afternoon (13:00-18:00, sequence=1)
-- Weekend (09:00-14:00, sequence=0)

-- Menu 1: Basic Haircut (60 min)
-- Weekday Morning: 09:00, 10:00, 11:00
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000001',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 09:00', TIMESTAMP '2024-01-01 11:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

-- Weekday Afternoon: 13:00, 14:00, 15:00, 16:00, 17:00
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000001',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 13:00', TIMESTAMP '2024-01-01 17:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

-- Weekend: 09:00, 10:00, 11:00, 12:00, 13:00
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000001',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 09:00', TIMESTAMP '2024-01-01 13:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) IN (6, 7);

-- Menu 9: Shampoo (30 min)
-- Weekday Morning: 09:00, 09:30, 10:00, 10:30, 11:00, 11:30
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000009',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '30 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 09:00', TIMESTAMP '2024-01-01 11:30', INTERVAL '30 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

-- Weekday Afternoon: 13:00, 13:30, ..., 17:30
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000009',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '30 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 13:00', TIMESTAMP '2024-01-01 17:30', INTERVAL '30 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

-- Weekend: 09:00, 09:30, ..., 13:30
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000009',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '30 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 09:00', TIMESTAMP '2024-01-01 13:30', INTERVAL '30 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) IN (6, 7);

-- Menu 10: Treatment (60 min) - Same pattern as Menu 1
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000010',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 09:00', TIMESTAMP '2024-01-01 11:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000010',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 13:00', TIMESTAMP '2024-01-01 17:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000010',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 09:00', TIMESTAMP '2024-01-01 13:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) IN (6, 7);

-- ========== Business 2 (Nail Shop) ==========
-- Similar pattern: Morning (10:00-14:00), Afternoon (15:00-20:00), Weekend (10:00-18:00)

-- Menu 11: Basic Nail (60 min)
-- Weekday Morning: 10:00, 11:00, 12:00, 13:00
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    '60000000-0000-0000-0000-000000000011',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '20 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 10:00', TIMESTAMP '2024-01-01 13:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

-- Weekday Afternoon: 15:00, 16:00, 17:00, 18:00, 19:00
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    '60000000-0000-0000-0000-000000000011',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '20 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 15:00', TIMESTAMP '2024-01-01 19:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

-- Weekend: 10:00, 11:00, 12:00, 13:00, 14:00, 15:00, 16:00, 17:00
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    '60000000-0000-0000-0000-000000000011',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '20 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 10:00', TIMESTAMP '2024-01-01 17:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) IN (6, 7);

-- Menu 15: Pedicure (60 min) - Same pattern as Menu 11
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    '60000000-0000-0000-0000-000000000015',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '20 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 10:00', TIMESTAMP '2024-01-01 13:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    '60000000-0000-0000-0000-000000000015',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '20 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 15:00', TIMESTAMP '2024-01-01 19:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) BETWEEN 1 AND 5;

INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    '60000000-0000-0000-0000-000000000015',
    date_series::date,
    time_series::time,
    (time_series + INTERVAL '60 minutes')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '20 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 10:00', TIMESTAMP '2024-01-01 17:00', INTERVAL '60 minutes') AS time_series
WHERE EXTRACT(ISODOW FROM date_series) IN (6, 7);

-- ========================================
-- 9. Reservation (10,000 reservations)
-- ========================================
-- Distribute across 97 customers and 3,420 slots
-- Use WITH clause to avoid row_number() in JOIN

WITH numbered_slots AS (
    SELECT
        bs.id,
        bs.business_id,
        bs.menu_id,
        bs.slot_date,
        bs.start_time,
        m.price,
        m.duration_minutes,
        ROW_NUMBER() OVER (ORDER BY random()) as rn
    FROM booking_slot bs
             JOIN menu m ON bs.menu_id = m.id
    WHERE bs.slot_date >= CURRENT_DATE - INTERVAL '15 days'
    ORDER BY random()
    LIMIT 10000
),
     customer_assignment AS (
         SELECT
             ns.*,
             ('20000000-0000-0000-0000-0000000000' || LPAD((((ns.rn - 1) % 97) + 1)::text, 2, '0'))::uuid as customer_id
         FROM numbered_slots ns
     )
INSERT INTO reservation (id, business_id, customer_id, menu_id, booking_slot_id, reservation_date, reservation_time, reservation_price, reservation_duration, customer_name, customer_phone, status, created_at, updated_at)
SELECT
    gen_random_uuid(),
    ca.business_id,
    ca.customer_id,
    ca.menu_id,
    ca.id,
    ca.slot_date,
    ca.start_time,
    ca.price,
    ca.duration_minutes,
    u.name,
    u.phone_number,
    CASE
        WHEN random() < 0.6 THEN 'CONFIRMED'
        WHEN random() < 0.85 THEN 'PENDING'
        WHEN random() < 0.95 THEN 'COMPLETED'
        ELSE 'CANCELLED'
        END,
    NOW() - (random() * INTERVAL '30 days'),
    NOW()
FROM customer_assignment ca
         JOIN users u ON u.id = ca.customer_id;

-- ========================================
-- Completed
-- ========================================
-- Data created:
--   Users: 100 (3 business owners + 97 customers)
--   Business: 3
--   BusinessHours: 21
--   BusinessCategory: 6
--   Menu: 25 (15 RESERVATION_BASED + 10 ONDEMAND_BASED)
--   BookingSlot: ~3,420
--   Reservation: 10,000
-- ========================================
-- For Level 3 Testing:
--   Business 1: ~3,300 reservations
--   Business 2: ~3,300 reservations
--   Business 3: ~3,400 reservations (ONDEMAND)
-- ========================================