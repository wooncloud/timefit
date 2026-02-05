-- ============================================================
-- 카테고리 삭제
-- ============================================================
-- API:        DELETE /api/business/{id}/category/{cid}
-- 핵심 쿼리:  DELETE FROM business_category WHERE id = ?
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
-- 🔍 EXPLAIN: 카테고리 삭제
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
DELETE FROM business_category
WHERE id = '99999999-0000-0000-0000-000000000300';

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Delete on business_category
--                 -> Index Scan using business_category_pkey
--    - Delete 노드: "삭제 적용"
--    - Index Scan: "행을 어떻게 찾는가" (여기가 핵심)
--
-- ✅ Index Scan의 Index Cond: (id = '...')
--    - PK로 단건 조회, 테이블 크기 무관
--
-- ✅ actual rows = 0 (Delete 노드는 항상 0)
--    - DELETE는 결과 행을 반환하지 않음
--    - 아래 Index Scan의 actual rows = 1 확인
--
-- ✅ Buffers: shared hit vs dirtied
--    - dirtied: 삭제된 페이지 (MVCC에서는 실제로 표시만)
--    - FK 제약이 있으면 참조 테이블도 확인 → 추가 Buffers
--
-- 참고: 실제 API는 활성 Menu가 있으면 삭제 불가
--       (Application 레이어에서 체크)

ROLLBACK;
