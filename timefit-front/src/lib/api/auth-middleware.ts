import { NextRequest, NextResponse } from 'next/server';

import { clearSessionAndLogout } from './logout-helper';

import { getServerSession } from '@/lib/session/server';
import type { SessionUser } from '@/lib/session/options';
import type { RefreshTokenResponse, TokenPair } from '@/types/auth/token';
import { HTTP_STATUS, AUTH_MESSAGES } from '@/types/auth/token';

// ==================== 타입 정의 ====================

type AuthenticatedHandler<T = unknown> = (
  request: NextRequest,
  context: {
    accessToken: string;
    userId: string;
    session: Awaited<ReturnType<typeof getServerSession>>;
    isRetry?: boolean;
  }
) => Promise<NextResponse<T>>;

// ==================== 헬퍼 함수 ====================

/**
 * 세션 만료 처리 및 로그아웃을 수행합니다.
 */
async function handleSessionExpiration<T>(message: string): Promise<NextResponse<T>> {
  console.log(`[Auth] 세션 만료 처리: ${message}`);
  return (await clearSessionAndLogout(message)) as NextResponse<T>;
}

/**
 * 401 Unauthorized 응답을 처리합니다.
 * - refreshToken이 있으면 토큰 갱신 시도
 * - 갱신 성공 시 세션 업데이트 후 핸들러 재실행
 * - 갱신 실패 시 로그아웃
 */
async function handle401Response<T>(
  request: NextRequest,
  handler: AuthenticatedHandler<T>,
  session: Awaited<ReturnType<typeof getServerSession>>,
  userId: string
): Promise<NextResponse<T>> {
  const refreshToken = session.user?.refreshToken;

  if (!refreshToken) {
    console.log('[Auth] refreshToken 없음, 로그아웃 처리');
    return handleSessionExpiration(AUTH_MESSAGES.SESSION_EXPIRED);
  }

  console.log('[Auth] 토큰 갱신 시도');
  const refreshResult = await refreshAccessToken(refreshToken);

  if (!refreshResult) {
    console.log('[Auth] 토큰 갱신 실패, 로그아웃 처리');
    return handleSessionExpiration(AUTH_MESSAGES.SESSION_EXPIRED);
  }

  // 세션 업데이트
  console.log('[Auth] 토큰 갱신 성공, 세션 업데이트 중');
  session.user = {
    ...(session.user as SessionUser),
    accessToken: refreshResult.accessToken,
    refreshToken: refreshResult.refreshToken,
  };
  await session.save();
  console.log('[Auth] 세션 저장 완료');

  // 핸들러 재실행
  console.log('[Auth] 새 토큰으로 요청 재시도');
  const retryResponse = await handler(request, {
    accessToken: refreshResult.accessToken,
    userId,
    session,
    isRetry: true,
  });

  // 재시도 후에도 401이면 로그아웃
  if (retryResponse.status === HTTP_STATUS.UNAUTHORIZED) {
    console.log('[Auth] 재시도 후에도 401 응답, 로그아웃 처리');
    return handleSessionExpiration(AUTH_MESSAGES.SESSION_EXPIRED);
  }

  console.log('[Auth] 재시도 성공');
  return retryResponse;
}

/**
 * Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.
 *
 * @param refreshToken - 저장된 refresh token
 * @returns 새로운 accessToken과 refreshToken, 실패 시 null
 */
async function refreshAccessToken(refreshToken: string): Promise<TokenPair | null> {
  try {
    const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

    if (!BACKEND_API_URL) {
      console.error('[Token Refresh] NEXT_PUBLIC_BACKEND_URL이 설정되지 않았습니다.');
      return null;
    }

    const response = await fetch(`${BACKEND_API_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
      console.log('[Token Refresh] 토큰 갱신 실패:', response.status);
      return null;
    }

    const result = (await response.json()) as RefreshTokenResponse;

    if (!result.data?.accessToken || !result.data?.refreshToken) {
      console.log('[Token Refresh] 응답 형식이 올바르지 않습니다:', result);
      return null;
    }

    return {
      accessToken: result.data.accessToken,
      refreshToken: result.data.refreshToken,
    };
  } catch (error) {
    console.error('[Token Refresh] 오류 발생:', error);
    return null;
  }
}

// ==================== 메인 미들웨어 ====================

/**
 * 인증이 필요한 API 라우트를 래핑하는 미들웨어
 *
 * 기능:
 * 1. 세션 검증 및 accessToken 확인
 * 2. 핸들러 실행 및 백엔드 401 응답 감지
 * 3. 토큰 리프레시 시도 (refreshToken으로 새 accessToken 발급)
 * 4. 리프레시 실패 시 세션 클리어 및 로그아웃
 *
 * @example
 * export const GET = withAuth(async (request, { accessToken, userId }) => {
 *   const response = await fetch(`${BACKEND_API_URL}/api/business/my`, {
 *     headers: { Authorization: `Bearer ${accessToken}` }
 *   });
 *   return NextResponse.json(await response.json());
 * });
 */
export function withAuth<T = unknown>(handler: AuthenticatedHandler<T>) {
  return async (request: NextRequest): Promise<NextResponse<T>> => {
    try {
      const session = await getServerSession();
      const accessToken = session.user?.accessToken;
      const userId = session.user?.userId;

      // 1. 세션 검증
      if (!accessToken || !userId) {
        return handleSessionExpiration(AUTH_MESSAGES.AUTHENTICATION_REQUIRED);
      }

      // 2. 핸들러 실행
      const response = await handler(request, { accessToken, userId, session });

      // 3. 401 응답 처리
      if (response.status === HTTP_STATUS.UNAUTHORIZED) {
        return handle401Response(request, handler, session, userId);
      }

      // 4. 정상 응답 반환
      return response;
    } catch (error) {
      console.error('[Auth Middleware] 예외 발생:', error);
      return NextResponse.json(
        {
          success: false,
          message: AUTH_MESSAGES.SERVER_ERROR,
        } as T,
        { status: HTTP_STATUS.INTERNAL_SERVER_ERROR }
      );
    }
  };
}
