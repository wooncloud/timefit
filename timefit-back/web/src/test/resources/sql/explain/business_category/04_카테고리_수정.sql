-- ============================================================
-- 카테고리 수정
-- ============================================================
-- API:        PATCH /api/business/{id}/category/{cid}
-- 핵심 쿼리:  UPDATE business_category WHERE id = ?
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 🔧 픽스처
-- ============================================================
-- business_category 1건만
-- 이유: WHERE id (PK 조건) → 테이블 크기 무관하게 Index Scan

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
-- 🔍 EXPLAIN: 카테고리 수정
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
UPDATE business_category
SET 
    category_name = 'Updated Category',
    category_notice = 'Updated notice',
    updated_at = NOW()
WHERE id = '99999999-0000-0000-0000-000000000300';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Update on business_category
--                 -> Index Scan using business_category_pkey
--    - Update 노드: "변경 적용"
--    - Index Scan: "행을 어떻게 찾는가" (여기가 핵심)
--
-- ✅ Index Scan의 Index Cond: (id = '...')
--    - PK로 단건 조회, 테이블 크기 무관
--
-- ✅ actual rows = 0 (Update 노드는 항상 0)
--    - UPDATE는 결과 행을 반환하지 않음
--    - 아래 Index Scan의 actual rows = 1 확인
--
-- ✅ Buffers: shared hit vs dirtied
--    - dirtied: 수정된 페이지 (WAL 기록)
--    - 인덱스가 많으면 dirtied 증가

ROLLBACK;
