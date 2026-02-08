-- ============================================================
-- EXPLAIN 테스트 — 루트 조건 Setup
-- ============================================================
-- 목적: 모든 EXPLAIN 파일의 공통 사전조건 생성
-- 범위: User, Business, UserBusinessRole, BusinessType만
-- 실행: 최초 1회 (또는 _cleanup.sql 후 재실행)
-- ============================================================
-- 특징: 99999999- prefix로 실제 데이터와 충돌 방지
-- ============================================================

-- ========================================
-- 1. Test Users (2명: 고객 1 + 사업자 1)
-- ========================================

-- 테스트용 고객
INSERT INTO users (id, email, password_hash, name, phone_number, role, created_at, updated_at)
VALUES (
    '99999999-0000-0000-0000-000000000001',
    'test_customer@timefit.test',
    '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW',
    'Test Customer',
    '01099999999',
    'USER',
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

-- 테스트용 사업자
INSERT INTO users (id, email, password_hash, name, phone_number, role, created_at, updated_at)
VALUES (
    '99999999-0000-0000-0000-000000000002',
    'test_owner@timefit.test',
    '$2a$10$yG3RdagbjgYSykQ5J8vjaO4CZGFQ62p7qHJwISdBY/pV7rhghPRIW',
    'Test Owner',
    '01099999998',
    'BUSINESS',
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 2. Test Business (1개)
-- ========================================

INSERT INTO business (
    id, business_name, business_number, owner_name, 
    address, contact_phone, description, logo_url, business_notice,
    is_active, average_rating, review_count, latitude, longitude,
    created_at, updated_at
)
VALUES (
    '99999999-0000-0000-0000-000000000100',
    'Test Business',
    '9999999999',
    'Test Owner',
    'Seoul Test District 123',
    '0299999999',
    'EXPLAIN Test Business',
    NULL,
    NULL,
    true,
    0.0,
    0,
    NULL,
    NULL,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 3. UserBusinessRole (Owner 매핑)
-- ========================================

INSERT INTO user_business_role (id, user_id, business_id, role, is_active, joined_at, created_at, updated_at)
VALUES (
    '99999999-0000-0000-0000-000000000200',
    '99999999-0000-0000-0000-000000000002',
    '99999999-0000-0000-0000-000000000100',
    'OWNER',
    true,
    NOW(),
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 4. BusinessType (업종 매핑)
-- ========================================

INSERT INTO business_type (business_id, type_code)
VALUES ('99999999-0000-0000-0000-000000000100', 'BD008')
ON CONFLICT (business_id, type_code) DO NOTHING;

-- ========================================
-- 완료 메시지
-- ========================================

SELECT 
    'Setup completed!' as status,
    '생성 완료:' as info,
    '  - Users: 2 (customer 1, owner 1)' as detail_1,
    '  - Business: 1' as detail_2,
    '  - UserBusinessRole: 1 (OWNER)' as detail_3,
    '  - BusinessType: 1 (BD008)' as detail_4,
    '' as separator,
    '다음 단계: 원하는 도메인 폴더의 .sql 파일 실행' as next_step,
    '  예: business_category/02_카테고리_목록_조회.sql' as example;
