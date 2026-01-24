import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';

import { attemptTokenRefresh } from '@/lib/api/token-refresh';
import { sessionOptions } from '@/lib/session/options';
import type { SessionData, SessionUser } from '@/lib/session/options';
import { isTokenExpired } from '@/lib/utils/jwt';

/**
 * 토큰 만료 임박 시간 (초)
 * 토큰 만료 5분 전에 리프레시 시도
 */
const TOKEN_REFRESH_THRESHOLD_SECONDS = 300;

interface TokenRefreshResult {
  response: NextResponse;
  refreshed: boolean;
  sessionDestroyed: boolean;
}

/**
 * 세션 토큰 리프레시 핸들러
 * 페이지 요청에 대해 토큰 만료를 미리 체크하여 서버 컴포넌트 오류 방지
 */
export async function handleTokenRefresh(
  request: NextRequest
): Promise<TokenRefreshResult> {
  const response = NextResponse.next();
  const session = await getIronSession<SessionData>(
    request,
    response,
    sessionOptions
  );

  const user = session.user;

  // 토큰이 없으면 리프레시 불필요
  if (!user?.accessToken || !user?.refreshToken) {
    return { response, refreshed: false, sessionDestroyed: false };
  }

  // 토큰이 만료되었거나 임박(5분 전)한 경우 리프레시 시도
  if (!isTokenExpired(user.accessToken, TOKEN_REFRESH_THRESHOLD_SECONDS)) {
    return { response, refreshed: false, sessionDestroyed: false };
  }

  console.warn(
    '[Middleware] 토큰 만료 임박 감지, 리프레시 시도:',
    request.nextUrl.pathname
  );

  const newTokens = await attemptTokenRefresh(user.refreshToken);

  if (newTokens) {
    session.user = {
      ...(user as SessionUser),
      accessToken: newTokens.accessToken,
      refreshToken: newTokens.refreshToken,
    };
    await session.save();
    console.warn('[Middleware] 토큰 리프레시 및 세션 저장 완료');
    return { response, refreshed: true, sessionDestroyed: false };
  }

  // 리프레시 실패 시 세션 초기화
  console.error('[Middleware] 토큰 리프레시 실패, 세션 초기화');
  session.destroy();
  return { response, refreshed: false, sessionDestroyed: true };
}
