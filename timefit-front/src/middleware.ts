import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';

import { attemptTokenRefresh } from '@/lib/api/token-refresh';
import { sessionOptions } from '@/lib/session/options';
import type { SessionData, SessionUser } from '@/lib/session/options';
import { isTokenExpired } from '@/lib/utils/jwt';

export async function middleware(request: NextRequest) {
  const url = request.nextUrl.clone();
  const pathname = url.pathname;

  // 1. static files 및 API 경로는 미들웨어 최소화
  if (pathname.startsWith('/_next') || pathname.includes('.')) {
    return NextResponse.next();
  }

  // 3. 세션 토큰 리프레시 로직
  // 페이지 요청(/api 제외)에 대해 토큰 만료를 미리 체크하여 서버 컴포넌트 오류 방지
  if (!pathname.startsWith('/api')) {
    const response = NextResponse.next();
    const session = await getIronSession<SessionData>(
      request,
      response,
      sessionOptions
    );

    const user = session.user;
    if (user?.accessToken && user?.refreshToken) {
      // 토큰이 만료되었거나 임박(5분 전)한 경우 리프레시 시도
      if (isTokenExpired(user.accessToken, 300)) {
        console.log(
          '[Middleware] 토큰 만료 임박 감지, 리프레시 시도:',
          pathname
        );
        const newTokens = await attemptTokenRefresh(user.refreshToken);

        if (newTokens) {
          session.user = {
            ...(user as SessionUser),
            accessToken: newTokens.accessToken,
            refreshToken: newTokens.refreshToken,
          };
          await session.save();
          console.log('[Middleware] 토큰 리프레시 및 세션 저장 완료');
        } else {
          console.log('[Middleware] 토큰 리프레시 실패, 세션 초기화');
          session.destroy();
          // 여기서 강제 로그아웃 리다이렉트를 할 수도 있지만,
          // 일단은 세션만 비우고 각 페이지의 권한 체크 로직에 맡깁니다.
        }
      }
    }
    return response;
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
