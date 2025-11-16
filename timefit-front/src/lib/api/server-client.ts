/**
 * 서버 컴포넌트용 API 클라이언트
 *
 * Server Components와 Server Actions에서 사용하는 백엔드 API 클라이언트
 */

import 'server-only';
import { getServerSession } from '@/lib/session/server';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

interface ServerApiFetchOptions extends RequestInit {
  requiresAuth?: boolean;
}

/**
 * 서버 컴포넌트에서 백엔드 API를 호출하는 fetch wrapper
 *
 * @param endpoint - API 엔드포인트 (예: '/api/users')
 * @param options - fetch 옵션
 * @returns fetch Response
 *
 * @example
 * ```typescript
 * const response = await serverApiFetch('/api/users', {
 *   method: 'GET',
 *   requiresAuth: true,
 * });
 * ```
 */
export async function serverApiFetch(
  endpoint: string,
  options: ServerApiFetchOptions = {}
): Promise<Response> {
  const { requiresAuth = true, ...fetchOptions } = options;

  // 기본 헤더 설정
  const headers = new Headers(fetchOptions.headers);
  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  // 인증이 필요한 경우 세션에서 토큰 가져오기
  if (requiresAuth) {
    const session = await getServerSession();
    const accessToken = session.user?.accessToken;

    if (!accessToken) {
      throw new Error('No access token found in session');
    }

    headers.set('Authorization', `Bearer ${accessToken}`);
  }

  const url = endpoint.startsWith('http')
    ? endpoint
    : `${BACKEND_API_URL}${endpoint}`;

  const response = await fetch(url, {
    ...fetchOptions,
    headers,
  });

  // 서버 컴포넌트에서는 토큰 갱신을 자동으로 처리하지 않음
  // 401 에러가 발생하면 클라이언트 측에서 처리하도록 함
  if (response.status === 401 && requiresAuth) {
    throw new Error(
      'Access token expired. Please refresh the page or login again.'
    );
  }

  return response;
}

/**
 * JSON 응답을 기대하는 서버 API 요청
 *
 * @param endpoint - API 엔드포인트
 * @param options - fetch 옵션
 * @returns JSON 파싱된 응답 데이터
 */
export async function serverApiFetchJson<T = any>(
  endpoint: string,
  options: ServerApiFetchOptions = {}
): Promise<T> {
  const response = await serverApiFetch(endpoint, options);

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(
      errorData.message || `API request failed with status ${response.status}`
    );
  }

  return response.json();
}
