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
  }
) => Promise<NextResponse<T>>;

/**
 * 인증이 필요한 API 라우트를 래핑하는 미들웨어
 *
 * 기능:
 * 1. 세션 검증 및 accessToken 확인
 * 2. 핸들러 실행 및 백엔드 401 응답 감지
 * 3. 토큰 리프레시 시도 (구현 예정)
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

      // 3. 백엔드에서 401 응답이 온 경우 처리
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

            // 핸들러 재실행 (새 토큰으로)
            return handler(request, {
              accessToken: refreshResult.accessToken,
              userId,
              session,
            });
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
 * TODO: 백엔드 /api/auth/refresh 엔드포인트 구현 후 활성화
 *
 * @param refreshToken - 저장된 refresh token
 * @returns 새로운 accessToken과 refreshToken, 실패 시 null
 */
async function refreshAccessToken(
  refreshToken: string
): Promise<{ accessToken: string; refreshToken: string } | null> {
  try {
    // TODO: 백엔드 리프레시 엔드포인트 호출
    // const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/auth/refresh`, {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ refreshToken }),
    // });
    //
    // if (!response.ok) {
    //   return null;
    // }
    //
    // const result = await response.json();
    // return {
    //   accessToken: result.data.accessToken,
    //   refreshToken: result.data.refreshToken,
    // };

    console.log('[Token Refresh] 토큰 갱신 로직은 아직 구현되지 않았습니다.', {
      refreshToken: refreshToken.substring(0, 10) + '...',
    });

    // 현재는 항상 null 반환 (로그아웃 처리)
    return null;
  } catch (error) {
    console.error('[Token Refresh Error]', error);
    return null;
  }
}
