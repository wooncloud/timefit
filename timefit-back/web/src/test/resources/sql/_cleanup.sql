-- ============================================================
-- EXPLAIN 테스트 — 전체 데이터 Cleanup
-- ============================================================
-- 목적: 테스트용 데이터 완전 제거
-- 범위: 99999999- prefix 전체 (setup + 각 파일이 생성한 데이터)
-- 실행: 필요 시 언제든 (_setup.sql 재실행 전 권장)
-- ============================================================
-- 특징: FK 제약을 고려하여 역순 삭제
-- ============================================================

-- ========================================
-- 역순 삭제 (FK 제약 고려)
-- ========================================

-- Level 5: Reservation (최상위 의존)
DELETE FROM reservation 
WHERE id::text LIKE '99999999%' 
   OR business_id::text LIKE '99999999%'
   OR customer_id::text LIKE '99999999%'
   OR menu_id::text LIKE '99999999%'
   OR booking_slot_id::text LIKE '99999999%';

-- Level 4: Review (Reservation 의존)
DELETE FROM review 
WHERE id::text LIKE '99999999%'
   OR business_id::text LIKE '99999999%'
   OR user_id::text LIKE '99999999%'
   OR reservation_id::text LIKE '99999999%';

-- Level 4: Wishlist (Menu 의존)
DELETE FROM wishlist 
WHERE id::text LIKE '99999999%'
   OR user_id::text LIKE '99999999%'
   OR menu_id::text LIKE '99999999%';

-- Level 4: BookingSlot (Menu 의존)
DELETE FROM booking_slot 
WHERE id::text LIKE '99999999%' 
   OR business_id::text LIKE '99999999%'
   OR menu_id::text LIKE '99999999%';

-- Level 3: Menu (BusinessCategory 의존)
DELETE FROM menu 
WHERE id::text LIKE '99999999%'
   OR business_id::text LIKE '99999999%'
   OR business_category_id::text LIKE '99999999%';

-- Level 3: BusinessCategory (Business 의존)
DELETE FROM business_category 
WHERE id::text LIKE '99999999%'
   OR business_id::text LIKE '99999999%';

-- Level 2: OperatingHours (Business 의존)
DELETE FROM operating_hours 
WHERE id::text LIKE '99999999%'
   OR business_id::text LIKE '99999999%';

-- Level 2: BusinessType (_setup.sql에서 생성)
DELETE FROM business_type 
WHERE business_id::text LIKE '99999999%';

-- Level 2: UserBusinessRole (_setup.sql에서 생성)
DELETE FROM user_business_role 
WHERE id::text LIKE '99999999%'
   OR user_id::text LIKE '99999999%'
   OR business_id::text LIKE '99999999%';

-- Level 1: Business (_setup.sql에서 생성)
DELETE FROM business 
WHERE id::text LIKE '99999999%';

-- Level 1: Users (_setup.sql에서 생성)
DELETE FROM users 
WHERE id::text LIKE '99999999%';

-- ========================================
-- 완료 메시지
-- ========================================

SELECT 
    'Cleanup completed!' as status,
    '삭제 완료: 99999999- prefix 전체 데이터' as info,
    '  - _setup.sql이 생성한 루트 조건' as detail_1,
    '  - 각 .sql 파일이 생성한 픽스처' as detail_2,
    '' as separator,
    '다음 단계: _setup.sql 재실행' as next_step;
