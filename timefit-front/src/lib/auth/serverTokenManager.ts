import type { SessionData } from '@/lib/session/options';
import type { IronSession } from 'iron-session';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

interface RefreshTokenResponse {
  success: boolean;
  message?: string;
  data?: {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
  };
}

/**
 * 서버 사이드에서 리프레시 토큰으로 액세스 토큰을 갱신합니다.
 * @param session iron-session 객체
 * @returns 갱신 성공 여부
 */
export async function refreshTokenOnServer(
  session: IronSession<SessionData>
): Promise<boolean> {
  const refreshToken = session.user?.refreshToken;

  if (!refreshToken) {
    console.error('리프레시 토큰이 없습니다.');
    session.destroy();
    return false;
  }

  try {
    const response = await fetch(`${BACKEND_API_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ refreshToken }),
    });

    const data = (await response.json()) as RefreshTokenResponse;

    if (!response.ok || !data.success) {
      console.error('토큰 갱신 실패:', data.message);
      session.destroy();
      return false;
    }

    // 세션 업데이트
    if (session.user && data.data) {
      session.user.accessToken = data.data.accessToken;
      session.user.refreshToken = data.data.refreshToken;
      await session.save();
      console.log('토큰 갱신 성공');
      return true;
    }

    session.destroy();
    return false;
  } catch (error) {
    console.error('토큰 갱신 중 오류:', error);
    session.destroy();
    return false;
  }
}

/**
 * API 요청을 실행하고, 401 에러 발생 시 토큰 갱신을 시도합니다.
 * @param session iron-session 객체
 * @param apiCall API 호출 함수 (accessToken을 인자로 받음)
 * @returns API 응답 또는 null (토큰 갱신 실패 시)
 */
export async function executeWithTokenRefresh<T>(
  session: IronSession<SessionData>,
  apiCall: (accessToken: string) => Promise<Response>
): Promise<Response | null> {
  const accessToken = session.user?.accessToken;

  if (!accessToken) {
    return null;
  }

  // 첫 번째 시도
  let response = await apiCall(accessToken);

  // 401 에러 발생 시 토큰 갱신 시도
  if (response.status === 401) {
    console.log('401 에러 발생, 토큰 갱신 시도...');
    const refreshed = await refreshTokenOnServer(session);

    if (!refreshed) {
      return null;
    }

    // 갱신된 토큰으로 재시도
    const newAccessToken = session.user?.accessToken;
    if (newAccessToken) {
      response = await apiCall(newAccessToken);
    } else {
      return null;
    }
  }

  return response;
}
