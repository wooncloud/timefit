-- ============================================================
-- 과거 슬롯 일괄 삭제
-- ============================================================
-- API:        DELETE /api/business/{id}/booking-slot/past
-- 핵심 쿼리:  DELETE FROM booking_slot 
--            WHERE business_id = ? AND slot_date < CURRENT_DATE
-- 사전조건:   _setup.sql (User, Business)
-- ============================================================

BEGIN;

-- ============================================================
-- 🔧 픽스처
-- ============================================================
-- business_category 1건 (체인 부모)
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

-- menu 1건 (체인 부모)
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
    '99999999-0000-0000-0000-000000000400',
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

-- booking_slot 400건 생성 (과거 20일 + 미래 20일, 각 10슬롯/일)
-- 이유: DELETE WHERE slot_date < (비-PK 조건) → 테이블이 커야 Index Scan 등장
INSERT INTO booking_slot (
    id,
    business_id,
    menu_id,
    slot_date,
    slot_time,
    capacity,
    remaining_capacity,
    is_active,
    created_at,
    updated_at
)
SELECT 
    gen_random_uuid(),
    '99999999-0000-0000-0000-000000000100',
    '99999999-0000-0000-0000-000000000400',
    CURRENT_DATE + (day_offset || ' days')::interval,
    ('09:00:00'::time + (slot_offset || ' hours')::interval),
    3,
    3,
    true,
    NOW(),
    NOW()
FROM generate_series(-20, 19) AS day_offset,  -- 과거 20일 + 미래 20일
     generate_series(0, 9) AS slot_offset;

-- ============================================================
-- 🔍 EXPLAIN: 과거 슬롯 일괄 삭제
-- ============================================================

EXPLAIN (ANALYZE, BUFFERS)
DELETE FROM booking_slot
WHERE business_id = '99999999-0000-0000-0000-000000000100'
  AND slot_date < CURRENT_DATE;

-- ============================================================
-- 💡 확인 포인트
-- ============================================================
-- ✅ 노드 구조: Delete on booking_slot
--                 -> Index Scan / Bitmap Heap Scan
--    - Delete 노드: "삭제 적용"
--    - Scan 노드: "삭제할 행들을 어떻게 찾는가" (여기가 핵심)
--
-- ✅ Scan 타입: Index Scan vs Bitmap Index Scan
--    - (business_id, slot_date) 복합 인덱스가 있으면 Index Range Scan
--    - slot_date < 조건은 범위 검색
--    - 많은 행을 삭제하므로 Bitmap 방식 선택될 수 있음
--
-- ✅ Index Cond: (business_id = '...' AND slot_date < ...)
--    - 인덱스가 조건 처리
--
-- ✅ actual rows = 0 (Delete 노드는 항상 0)
--    - Scan 노드의 actual rows 확인 (약 200건 예상)
--    - 과거 20일 × 10슬롯/일 = 200건 삭제
--
-- ✅ Buffers: dirtied 대량
--    - 200건 삭제이므로 많은 페이지 dirtied
--    - MVCC에서는 실제 삭제가 아닌 표시
--    - WAL 기록으로 인한 추가 I/O
--
-- ✅ 성능 고려사항
--    - 대량 삭제는 트랜잭션 크기에 영향
--    - 배치 처리로 나누는 것이 더 안전할 수 있음
--    - FK 제약이 있으면 참조 테이블도 확인 → 추가 cost
--
-- 참고: 실제 운영에서는 예약이 없는 슬롯만 삭제
--       (Application 레이어에서 추가 조건 체크)

ROLLBACK;
