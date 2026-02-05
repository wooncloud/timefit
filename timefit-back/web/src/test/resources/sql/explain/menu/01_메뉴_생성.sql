-- ============================================================
-- 메뉴 생성
-- ============================================================
-- API:        POST /api/business/{id}/menu
-- 핵심 쿼리:  INSERT INTO menu
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 🔧 픽스처
-- ============================================================
-- business_category 1건만
-- 이유: 체인 부모는 항상 최소 1건

INSERT INTO business_category (
    id, 
    business_id, 
    business_type, 
    category_name, 
    is_active, 
    created_at, 
    updated_at
)
VALUES (
    '99999999-0000-0000-0000-000000000300',
    '99999999-0000-0000-0000-000000000100',
    'BD008',
    'Test Category',
    true,
    NOW(),
    NOW()
);

-- ============================================================
-- 🔍 EXPLAIN: 메뉴 생성
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
INSERT INTO menu (
    id,
    business_id,
    business_category_id,
    service_name,
    description,
    price,
    duration_minutes,
    order_type,
    is_active,
    created_at,
    updated_at
)
VALUES (
    gen_random_uuid(),
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000300',
    'Test Service',
    'Test Description',
    50000,
    60,
    'RESERVATION_BASED',
    true,
    NOW(),
    NOW()
);

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ INSERT 기본 cost
-- ✅ FK 제약 확인 비용
--    - business_id → business 테이블 참조 확인
--    - business_category_id → business_category 테이블 참조 확인
-- ✅ 인덱스 생성 비용
--    - PK 인덱스 + FK 인덱스들
-- ✅ Buffers: shared hit vs read
--    - FK 확인 시 참조 테이블도 읽음

ROLLBACK;
