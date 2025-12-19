-- ========================================
-- Timefit Minimal Test Data v4
-- For Development and Basic Testing
-- ========================================
-- Changes from v3:
-- - OperatingHours added with sequence field
-- - BusinessHours: Weekend 09:00-14:00 (not closed)
-- - BookingSlot: Accurate duration (60min intervals)
-- - Reservation: Fixed row_number() error
-- ========================================

-- Enable pgcrypto extension (for UUID generation)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ========================================
-- 1. Users (10 people) - Verified password123 hash
-- ========================================

-- Business Owners (3)
INSERT INTO users (id, email, password_hash, name, phone_number, role, created_at, updated_at) VALUES
                                                                                                   ('10000000-0000-0000-0000-000000000001', 'owner1@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Owner Kim', '01011111111', 'BUSINESS', NOW(), NOW()),
                                                                                                   ('10000000-0000-0000-0000-000000000002', 'owner2@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Owner Lee', '01011112222', 'BUSINESS', NOW(), NOW()),
                                                                                                   ('10000000-0000-0000-0000-000000000003', 'owner3@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Owner Park', '01011113333', 'BUSINESS', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET password_hash = EXCLUDED.password_hash;

-- Customers (7)
INSERT INTO users (id, email, password_hash, name, phone_number, role, created_at, updated_at) VALUES
                                                                                                   ('20000000-0000-0000-0000-000000000001', 'customer1@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Customer 1', '01022221111', 'USER', NOW(), NOW()),
                                                                                                   ('20000000-0000-0000-0000-000000000002', 'customer2@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Customer 2', '01022222222', 'USER', NOW(), NOW()),
                                                                                                   ('20000000-0000-0000-0000-000000000003', 'customer3@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Customer 3', '01022223333', 'USER', NOW(), NOW()),
                                                                                                   ('20000000-0000-0000-0000-000000000004', 'customer4@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Customer 4', '01022224444', 'USER', NOW(), NOW()),
                                                                                                   ('20000000-0000-0000-0000-000000000005', 'customer5@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Customer 5', '01022225555', 'USER', NOW(), NOW()),
                                                                                                   ('20000000-0000-0000-0000-000000000006', 'customer6@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Customer 6', '01022226666', 'USER', NOW(), NOW()),
                                                                                                   ('20000000-0000-0000-0000-000000000007', 'customer7@timefit.com', '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW', 'Customer 7', '01022227777', 'USER', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET password_hash = EXCLUDED.password_hash;

-- ========================================
-- 2. Business (3 businesses)
-- ========================================
INSERT INTO business (id, business_name, business_number, owner_name, address, contact_phone, description, is_active, created_at, updated_at) VALUES
                                                                                                                                                  ('30000000-0000-0000-0000-000000000001', 'Timefit Hair Salon', '1234567890', 'Owner Kim', 'Seoul Gangnam 123', '0211111111', 'Test Hair Salon', true, NOW(), NOW()),
                                                                                                                                                  ('30000000-0000-0000-0000-000000000002', 'Timefit Nail Shop', '2345678901', 'Owner Lee', 'Seoul Gangnam 456', '0222222222', 'Test Nail Shop', true, NOW(), NOW()),
                                                                                                                                                  ('30000000-0000-0000-0000-000000000003', 'Timefit Cafe', '3456789012', 'Owner Park', 'Seoul Seocho 789', '0233333333', 'Test Cafe', true, NOW(), NOW())
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
-- 5. BusinessHours (Total business hours boundary)
-- ========================================
-- day_of_week: 0=MONDAY, 1=TUESDAY, ..., 6=SUNDAY (ORDINAL)

-- Business 1 (Hair Salon): Mon-Fri 09:00-18:00
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
FROM generate_series(0, 4) AS day_num;

-- Business 1 (Hair Salon): Sat-Sun 09:00-14:00 (Shortened hours)
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
FROM generate_series(5, 6) AS day_num;

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
FROM generate_series(0, 4) AS day_num;

-- Business 2 (Nail Shop): Sat-Sun 10:00-18:00
INSERT INTO business_hours (id, business_id, day_of_week, open_time, close_time, is_closed, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    day_num,
    '10:00:00'::time,
    '18:00:00'::time,
    false,
    NOW(),
    NOW()
FROM generate_series(5, 6) AS day_num;

-- Business 3 (Cafe): Every day 07:00-22:00
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
FROM generate_series(0, 6) AS day_num;

-- ========================================
-- 6. OperatingHours (Bookable time slots with break times)
-- ========================================
-- Business 1 (Hair Salon) - Weekday with lunch break 12:00-13:00

-- Mon-Fri Morning Session (09:00-12:00, sequence=0)
INSERT INTO operating_hours (id, business_id, day_of_week, sequence, open_time, close_time, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    day_num,
    0,
    '09:00:00'::time,
    '12:00:00'::time,
    NOW(),
    NOW()
FROM generate_series(0, 4) AS day_num;

-- Mon-Fri Afternoon Session (13:00-18:00, sequence=1)
INSERT INTO operating_hours (id, business_id, day_of_week, sequence, open_time, close_time, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    day_num,
    1,
    '13:00:00'::time,
    '18:00:00'::time,
    NOW(),
    NOW()
FROM generate_series(0, 4) AS day_num;

-- Sat-Sun Single Session (09:00-14:00, sequence=0)
INSERT INTO operating_hours (id, business_id, day_of_week, sequence, open_time, close_time, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    day_num,
    0,
    '09:00:00'::time,
    '14:00:00'::time,
    NOW(),
    NOW()
FROM generate_series(5, 6) AS day_num;

-- Business 2 (Nail Shop) - Simplified (no break time for minimal data)

-- Mon-Sun Single Session (10:00-18:00, sequence=0)
INSERT INTO operating_hours (id, business_id, day_of_week, sequence, open_time, close_time, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    day_num,
    0,
    '10:00:00'::time,
    '18:00:00'::time,
    NOW(),
    NOW()
FROM generate_series(0, 6) AS day_num;

-- Business 3 (Cafe) - ONDEMAND only, single session

-- Every day (07:00-22:00, sequence=0)
INSERT INTO operating_hours (id, business_id, day_of_week, sequence, open_time, close_time, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000003',
    day_num,
    0,
    '07:00:00'::time,
    '22:00:00'::time,
    NOW(),
    NOW()
FROM generate_series(0, 6) AS day_num;

-- ========================================
-- 7. BusinessCategory (2 categories per business)
-- ========================================
INSERT INTO business_category (id, business_id, category_name, created_at, updated_at) VALUES
                                                                                           ('50000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'Hair', NOW(), NOW()),
                                                                                           ('50000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', 'Perm Dye', NOW(), NOW()),
                                                                                           ('50000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000002', 'Basic Care', NOW(), NOW()),
                                                                                           ('50000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000002', 'Art', NOW(), NOW()),
                                                                                           ('50000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000003', 'Coffee', NOW(), NOW()),
                                                                                           ('50000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000003', 'Dessert', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 8. Menu (25 items)
-- ========================================

-- Business 1 (Hair Salon) - 8 items (RESERVATION_BASED)
INSERT INTO menu (id, business_id, business_category_id, service_name, description, price, duration_minutes, order_type, is_active, created_at, updated_at) VALUES
                                                                                                                                                                ('60000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Basic Haircut', 'Basic Haircut', 15000, 60, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Style Haircut', 'Style Haircut', 25000, 90, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Designer Haircut', 'Designer Haircut', 35000, 90, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000002', 'Digital Perm', 'Digital Perm', 80000, 180, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000002', 'Magic Perm', 'Magic Perm', 90000, 180, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000002', 'Volume Perm', 'Volume Perm', 70000, 150, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Shampoo', 'Shampoo', 5000, 30, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'Treatment', 'Treatment', 20000, 60, 'RESERVATION_BASED', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Business 2 (Nail Shop) - 7 items (RESERVATION_BASED)
INSERT INTO menu (id, business_id, business_category_id, service_name, description, price, duration_minutes, order_type, is_active, created_at, updated_at) VALUES
                                                                                                                                                                ('60000000-0000-0000-0000-000000000011', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', 'Basic Nail', 'Basic Nail', 20000, 60, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', 'Gel Nail', 'Gel Nail', 35000, 90, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000013', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000004', 'Nail Art', 'Nail Art', 50000, 120, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000014', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000004', 'French Nail', 'French Nail', 40000, 90, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000015', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', 'Pedicure', 'Pedicure', 25000, 60, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000016', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', 'Foot Care', 'Foot Care', 30000, 60, 'RESERVATION_BASED', true, NOW(), NOW()),
                                                                                                                                                                ('60000000-0000-0000-0000-000000000019', '30000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', 'Polish Change', 'Polish Change', 10000, 30, 'RESERVATION_BASED', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Business 3 (Cafe) - 10 items (ONDEMAND only)
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
-- 9. BookingSlot (~150 slots)
-- ========================================

-- Business 1 (Hair Salon) - Basic Haircut only (60min)
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000001',
    '60000000-0000-0000-0000-000000000001',
    date_series,
    time_series,
    (time_series + INTERVAL '1 hour')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '4 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 09:00', TIMESTAMP '2024-01-01 17:00', INTERVAL '1 hour') AS time_series
LIMIT 100;

-- Business 2 (Nail Shop) - Basic Nail only (60min)
INSERT INTO booking_slot (id, business_id, menu_id, slot_date, start_time, end_time, is_available, created_at, updated_at)
SELECT
    gen_random_uuid(),
    '30000000-0000-0000-0000-000000000002',
    '60000000-0000-0000-0000-000000000011',
    date_series,
    time_series,
    (time_series + INTERVAL '1 hour')::time,
    true,
    NOW(),
    NOW()
FROM
    generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '3 days', INTERVAL '1 day') AS date_series,
    generate_series(TIMESTAMP '2024-01-01 10:00', TIMESTAMP '2024-01-01 17:00', INTERVAL '1 hour') AS time_series
LIMIT 50;

-- ========================================
-- 10. Reservation (50 reservations)
-- ========================================
-- Fixed row_number() error: Use WITH clause with row number

WITH slot_with_customer AS (
    SELECT
        bs.id as booking_slot_id,
        bs.business_id,
        bs.menu_id,
        bs.slot_date,
        bs.start_time,
        m.price,
        m.duration_minutes,
        ('20000000-0000-0000-0000-00000000000' || ((row_number() OVER (ORDER BY bs.id) % 7) + 1)::text)::uuid as customer_id,
        row_number() OVER (ORDER BY bs.id) as rn
    FROM booking_slot bs
             JOIN menu m ON bs.menu_id = m.id
    WHERE bs.slot_date >= CURRENT_DATE
    ORDER BY random()
    LIMIT 50
)
INSERT INTO reservation (id, business_id, customer_id, menu_id, booking_slot_id, reservation_date, reservation_time, reservation_price, reservation_duration, customer_name, customer_phone, status, created_at, updated_at)
SELECT
    gen_random_uuid(),
    swc.business_id,
    swc.customer_id,
    swc.menu_id,
    swc.booking_slot_id,
    swc.slot_date,
    swc.start_time,
    swc.price,
    swc.duration_minutes,
    u.name,
    u.phone_number,
    CASE
        WHEN random() < 0.6 THEN 'CONFIRMED'
        WHEN random() < 0.85 THEN 'PENDING'
        ELSE 'COMPLETED'
        END,
    NOW(),
    NOW()
FROM slot_with_customer swc
         JOIN users u ON u.id = swc.customer_id;

-- ========================================
-- Completed
-- ========================================
-- Data created:
--   Users: 10 (3 business, 7 customers)
--   Business: 3
--   BusinessHours: 21 (3 business * 7 days)
--   OperatingHours: 19 (with sequence field)
--   BusinessCategory: 6
--   Menu: 25 (15 RESERVATION + 10 ONDEMAND)
--   BookingSlot: ~150
--   Reservation: 50
-- ========================================