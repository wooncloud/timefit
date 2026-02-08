/**
 * JWT payload를 파싱하여 반환합니다.
 *
 * @param token - JWT 토큰
 * @returns 디코딩된 페이로드 객체
 */
export function decodeJwt<T = Record<string, unknown>>(
  token: string
): T | null {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );

    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('[JWT 디코드] 오류:', error);
    return null;
  }
}

/**
 * 토큰의 만료 여부를 확인합니다.
 *
 * @param token - JWT 토큰
 * @param bufferSeconds - 만료 시간 이전의 여유 시간 (초)
 * @returns 만료되었거나 임박했는지 여부
 */
export function isTokenExpired(token: string, bufferSeconds = 60): boolean {
  const payload = decodeJwt<{ exp?: number }>(token);
  if (!payload || !payload.exp) return true;

  const now = Math.floor(Date.now() / 1000);
  return payload.exp < now + bufferSeconds;
}
