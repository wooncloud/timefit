import 'server-only';

import type { RefreshTokenResponse, TokenPair } from '@/types/auth/token';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * Refresh Token으로 새 토큰 발급
 *
 * @param refreshToken - 유효한 리프레시 토큰
 * @returns 새 토큰 페어 또는 null (실패 시)
 */
export async function attemptTokenRefresh(
  refreshToken: string
): Promise<TokenPair | null> {
  try {
    if (!BACKEND_URL) {
      console.error('[Token Refresh] BACKEND_URL이 설정되지 않았습니다.');
      return null;
    }

    // 일반 fetch 사용 (무한 루프 방지)
    const response = await fetch(`${BACKEND_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
      console.error('[Token Refresh] 갱신 실패:', response.status);
      return null;
    }

    const result: RefreshTokenResponse = await response.json();

    if (!result.data?.accessToken || !result.data?.refreshToken) {
      console.error('[Token Refresh] 응답 형식 오류:', result);
      return null;
    }

    console.error('[Token Refresh] 갱신 성공');
    return {
      accessToken: result.data.accessToken,
      refreshToken: result.data.refreshToken,
    };
  } catch (error) {
    console.error('[Token Refresh] 예외 발생:', error);
    return null;
  }
}
