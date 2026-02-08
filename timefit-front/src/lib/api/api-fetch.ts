import 'server-only';

import { getServerSession } from '@/lib/session/server';

import { clearSession, updateSessionTokens } from './session-helpers';
import { attemptTokenRefresh } from './token-refresh';

/**
 * 서버측 인증된 fetch 래퍼
 *
 * 기능:
 * - 자동으로 세션에서 accessToken 추출 및 Authorization 헤더 추가
 * - Content-Type: application/json 헤더 자동 추가
 * - cache: 'no-store' 기본값 설정 (덮어쓰기 가능)
 * - 401 응답 시 자동으로 토큰 리프레시 시도
 * - 리프레시 성공 시 원래 요청 재시도 (1회만)
 * - 리프레시 실패 시 세션 삭제 및 에러 throw
 *
 * @param url - 요청 URL
 * @param options - fetch options (Authorization, Content-Type 헤더와 cache는 자동 설정됨)
 * @returns Response 객체
 * @throws {Error} 인증 실패 또는 리프레시 실패 시
 *
 * @example
 * // 기본값 사용 (cache: 'no-store')
 * const response = await apiFetch(
 *   `${BACKEND_URL}/api/business/${id}`,
 *   { method: 'GET' }
 * );
 *
 * @example
 * // 캐싱이 필요한 경우 (기본값 덮어쓰기)
 * const response = await apiFetch(
 *   `${BACKEND_URL}/api/business/${id}`,
 *   { method: 'GET', next: { revalidate: 60 } }
 * );
 *
 * @example
 * // API Route에서
 * const response = await apiFetch(
 *   `${BACKEND_URL}/api/business/${id}`,
 *   { method: 'POST', body: JSON.stringify(data) }
 * );
 */
export async function apiFetch(
  url: string,
  options: RequestInit = {}
): Promise<Response> {
  const session = await getServerSession();
  const accessToken = session.user?.accessToken;

  // 1. 세션 검증
  if (!accessToken) {
    await clearSession();
    throw new Error('세션이 만료되었습니다. 다시 로그인해주세요.');
  }

  // 2. 헤더 자동 추가
  const headers = new Headers(options.headers);
  headers.set('Authorization', `Bearer ${accessToken}`);

  // Content-Type이 명시적으로 설정되지 않은 경우에만 추가
  // (multipart/form-data 등 다른 타입이 필요한 경우를 위해)
  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  // 3. 기본 옵션 설정 (덮어쓰기 가능)
  const fetchOptions: RequestInit = {
    cache: 'no-store', // 기본값: 캐싱하지 않음
    ...options, // 사용자 옵션이 기본값을 덮어씀
    headers,
  };

  // 3. 초기 요청 실행
  let response = await fetch(url, fetchOptions);

  // 4. 401이 아니면 그대로 반환
  if (response.status !== 401) {
    return response;
  }

  // 5. 401 감지 - 토큰 리프레시 시도
  console.error('[API Fetch] 401 감지, 토큰 리프레시 시도');

  const refreshToken = session.user?.refreshToken;

  if (!refreshToken) {
    console.error('[API Fetch] refreshToken 없음');
    await clearSession();
    throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
  }

  const newTokens = await attemptTokenRefresh(refreshToken);

  if (!newTokens) {
    console.error('[API Fetch] 리프레시 실패');
    await clearSession();
    throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
  }

  // 6. 세션 업데이트
  await updateSessionTokens(newTokens.accessToken, newTokens.refreshToken);
  console.error('[API Fetch] 세션 토큰 업데이트 완료');

  // 7. 새 토큰으로 재시도
  headers.set('Authorization', `Bearer ${newTokens.accessToken}`);
  response = await fetch(url, { ...options, headers });

  // 8. 재시도 후에도 401이면 에러
  if (response.status === 401) {
    console.error('[API Fetch] 재시도 후에도 401');
    await clearSession();
    throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
  }

  console.error('[API Fetch] 재시도 성공');
  return response;
}
