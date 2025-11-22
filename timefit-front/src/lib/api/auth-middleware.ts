import { NextRequest, NextResponse } from 'next/server';

import { clearSessionAndLogout } from './logout-helper';

import { getServerSession } from '@/lib/session/server';
import type { SessionUser } from '@/lib/session/options';

type AuthenticatedHandler<T = unknown> = (
  request: NextRequest,
  context: {
    accessToken: string;
    userId: string;
    session: Awaited<ReturnType<typeof getServerSession>>;
    isRetry?: boolean;
  }
) => Promise<NextResponse<T>>;

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

      // 1. 세션에 accessToken이 없으면 즉시 로그아웃
      if (!accessToken || !userId) {
        return (await clearSessionAndLogout(
          '인증이 필요합니다.'
        )) as NextResponse<T>;
      }

      // 2. 핸들러 실행
      const response = await handler(request, { accessToken, userId, session });

      // 3. 백엔드에서 401 응답이 온 경우 처리 (재시도가 아닐 때만)
      if (response.status === 401) {
        const refreshToken = session.user?.refreshToken;

        // 3-1. refreshToken이 있으면 토큰 갱신 시도
        if (refreshToken) {
          const refreshResult = await refreshAccessToken(refreshToken);

          if (refreshResult) {
            // 토큰 갱신 성공: 세션 업데이트 후 핸들러 재실행
            session.user = {
              ...(session.user as SessionUser),
              accessToken: refreshResult.accessToken,
              refreshToken: refreshResult.refreshToken,
            };
            await session.save();

            // 핸들러 재실행 (새 토큰으로, isRetry 플래그 설정)
            const retryResponse = await handler(request, {
              accessToken: refreshResult.accessToken,
              userId,
              session,
              isRetry: true,
            });

            // 재시도 후에도 401이면 로그아웃
            if (retryResponse.status === 401) {
              console.log('[Auth Middleware] 토큰 갱신 후에도 401 응답, 로그아웃 처리');
              return (await clearSessionAndLogout(
                '세션이 만료되었습니다. 다시 로그인해주세요.'
              )) as NextResponse<T>;
            }

            return retryResponse;
          }
        }

        // 3-2. refreshToken이 없거나 갱신 실패: 로그아웃
        return (await clearSessionAndLogout(
          '세션이 만료되었습니다. 다시 로그인해주세요.'
        )) as NextResponse<T>;
      }

      // 4. 정상 응답 반환
      return response;
    } catch (error) {
      console.error('[Auth Middleware Error]', error);
      return NextResponse.json(
        {
          success: false,
          message: '서버 오류가 발생했습니다.',
        } as T,
        { status: 500 }
      );
    }
  };
}

/**
 * Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.
 *
 * @param refreshToken - 저장된 refresh token
 * @returns 새로운 accessToken과 refreshToken, 실패 시 null
 */
async function refreshAccessToken(
  refreshToken: string
): Promise<{ accessToken: string; refreshToken: string } | null> {
  try {
    const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

    if (!BACKEND_API_URL) {
      console.error('[Token Refresh] NEXT_PUBLIC_BACKEND_URL이 설정되지 않았습니다.');
      return null;
    }

    console.log('[Token Refresh] 토큰 갱신 시도 중...');

    const response = await fetch(`${BACKEND_API_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
      console.log('[Token Refresh] 토큰 갱신 실패:', response.status);
      return null;
    }

    const result = await response.json();

    // 백엔드 응답 형식: { data: { accessToken, refreshToken, tokenType, expiresIn } }
    if (!result.data || !result.data.accessToken || !result.data.refreshToken) {
      console.log('[Token Refresh] 응답 형식이 올바르지 않습니다:', result);
      return null;
    }

    console.log('[Token Refresh] 토큰 갱신 성공');

    return {
      accessToken: result.data.accessToken,
      refreshToken: result.data.refreshToken,
    };
  } catch (error) {
    console.error('[Token Refresh Error]', error);
    return null;
  }
}
