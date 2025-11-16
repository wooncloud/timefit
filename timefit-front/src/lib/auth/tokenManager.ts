import type {
  RefreshRequestBody,
  RefreshHandlerResponse,
} from '@/types/auth/refresh';

/**
 * 리프레시 토큰으로 액세스 토큰을 갱신합니다.
 * @param refreshToken 리프레시 토큰
 * @returns 갱신 결과
 */
export async function refreshAccessToken(
  refreshToken: string
): Promise<RefreshHandlerResponse> {
  const requestBody: RefreshRequestBody = { refreshToken };

  const response = await fetch('/api/auth/refresh', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify(requestBody),
  });

  const data = (await response.json()) as RefreshHandlerResponse;
  return data;
}

/**
 * 로그아웃을 수행합니다.
 * @returns 로그아웃 결과
 */
export async function signOut(): Promise<{
  success: boolean;
  message: string;
}> {
  const response = await fetch('/api/auth/signout', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
  });

  const data = await response.json();
  return data;
}

/**
 * 토큰 만료 여부를 확인합니다 (JWT 디코딩 없이 응답 상태로 확인).
 * API 요청 실패 시 토큰 갱신을 시도하고, 갱신 실패 시 로그아웃합니다.
 */
export async function handleTokenRefresh(
  refreshToken: string | undefined
): Promise<boolean> {
  if (!refreshToken) {
    console.warn('리프레시 토큰이 없습니다. 로그아웃 처리합니다.');
    await signOut();
    window.location.href = '/signin';
    return false;
  }

  try {
    const result = await refreshAccessToken(refreshToken);

    if (result.success) {
      console.log('토큰이 갱신되었습니다.');
      return true;
    } else {
      console.error('토큰 갱신 실패:', result.message);
      await signOut();
      window.location.href = '/signin';
      return false;
    }
  } catch (error) {
    console.error('토큰 갱신 중 오류 발생:', error);
    await signOut();
    window.location.href = '/signin';
    return false;
  }
}
