-- ============================================================
-- 카테고리 목록 조회
-- ============================================================
-- API:        GET /api/business/{id}/categories
-- 핵심 쿼리:  SELECT * FROM business_category WHERE business_id = ?
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 🔧 픽스처
-- ============================================================
-- business_category 100건 생성
-- 이유: WHERE business_id (비-PK 조건) → 테이블이 커야 Index Scan 등장

INSERT INTO business_category (
    id, 
    business_id, 
    business_type, 
    category_name, 
    is_active, 
    created_at, 
    updated_at
)
SELECT 
    gen_random_uuid(),
    '99999999-0000-0000-0000-000000000100',
    'BD008',
    'Category ' || i,
    true,
    NOW(),
    NOW()
FROM generate_series(1, 100) AS i;

-- ============================================================
-- 🔍 EXPLAIN: 카테고리 목록 조회 (활성만)
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
SELECT 
    id,
    business_id,
    business_type,
    category_name,
    category_notice,
    is_active,
    created_at,
    updated_at
FROM business_category
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND is_active = true
ORDER BY business_type ASC, category_name ASC;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ Scan 타입: Seq Scan vs Index Scan vs Bitmap Heap Scan
--    - business_id에 인덱스가 있으면 Index/Bitmap 선택
--    - 없으면 Seq Scan + Filter (Rows Removed by Filter 확인)
--
-- ✅ Filter vs Index Cond 구분
--    - Index Cond: (business_id = '...')  → 인덱스가 조건 처리
--    - Filter: (is_active = true)         → 읽은 후에 거르기
--
-- ✅ Sort 노드 존재 여부
--    - ORDER BY business_type, category_name
--    - 인덱스가 정렬 순서와 맞으면 Sort 노드 생략 가능
--
-- ✅ actual rows vs estimated rows 비교
--    - 통계가 정확한지 확인 (크게 다르면 ANALYZE 필요)

ROLLBACK;
