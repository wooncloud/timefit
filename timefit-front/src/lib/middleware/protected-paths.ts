/**
 * 로그인이 필요한 고객 페이지 경로 목록
 * glob 패턴 지원: * 는 단일 세그먼트 매칭
 */
export const PROTECTED_CUSTOMER_PATHS = [
  '/bookings',
  '/mypage',
  '/wishlist',
  '/places/*/reserve', // 예약 페이지도 로그인 필요
];

/**
 * 로그인이 필요한 비즈니스 페이지 경로 목록
 */
export const PROTECTED_BUSINESS_PATHS = ['/b'];

/**
 * 경로가 보호된 페이지인지 확인
 * glob 패턴 지원: * 는 단일 세그먼트 매칭
 */
export function isProtectedPath(
  pathname: string,
  protectedPaths: string[]
): boolean {
  return protectedPaths.some(pattern => {
    // glob 패턴을 정규식으로 변환
    if (pattern.includes('*')) {
      const regexPattern = pattern
        .replace(/\*/g, '[^/]+') // * -> 단일 세그먼트
        .replace(/\[^\/\]\+\[^\/\]\+/g, '.*'); // ** -> 여러 세그먼트
      const regex = new RegExp(`^${regexPattern}(/.*)?$`);
      return regex.test(pathname);
    }
    // 일반 경로 매칭
    return pathname === pattern || pathname.startsWith(`${pattern}/`);
  });
}

/**
 * 고객 보호 경로인지 확인
 */
export function isProtectedCustomerPath(pathname: string): boolean {
  return isProtectedPath(pathname, PROTECTED_CUSTOMER_PATHS);
}

/**
 * 비즈니스 보호 경로인지 확인
 */
export function isProtectedBusinessPath(pathname: string): boolean {
  return isProtectedPath(pathname, PROTECTED_BUSINESS_PATHS);
}
