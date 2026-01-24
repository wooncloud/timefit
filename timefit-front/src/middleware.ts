import { NextRequest, NextResponse } from 'next/server';

import {
  businessAuthGuard,
  customerAuthGuard,
  handleTokenRefresh,
} from '@/lib/middleware';

export async function middleware(request: NextRequest) {
  const pathname = request.nextUrl.pathname;

  // 1. static files 및 API 경로는 미들웨어 최소화
  if (pathname.startsWith('/_next') || pathname.includes('.')) {
    return NextResponse.next();
  }

  // 2. 보호된 고객 페이지 인증 체크
  const customerGuardResult = await customerAuthGuard(request);
  if (!customerGuardResult.isAuthenticated) {
    return customerGuardResult.redirectResponse;
  }

  // 3. 보호된 비즈니스 페이지 인증 체크
  const businessGuardResult = await businessAuthGuard(request);
  if (!businessGuardResult.isAuthenticated) {
    return businessGuardResult.redirectResponse;
  }

  // 4. 세션 토큰 리프레시 로직
  // 페이지 요청(/api 제외)에 대해 토큰 만료를 미리 체크하여 서버 컴포넌트 오류 방지
  if (!pathname.startsWith('/api')) {
    const tokenRefreshResult = await handleTokenRefresh(request);
    return tokenRefreshResult.response;
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     */
    '/((?!api|_next/static|_next/image|favicon.ico).*)',
  ],
};
