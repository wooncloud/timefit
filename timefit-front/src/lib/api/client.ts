/**
 * API 클라이언트
 *
 * 백엔드 API를 호출하고 자동으로 토큰 갱신을 처리하는 fetch wrapper
 */

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

interface ApiFetchOptions extends RequestInit {
  requiresAuth?: boolean;
}

let isRefreshing = false;
let refreshPromise: Promise<boolean> | null = null;

/**
 * 토큰 갱신 요청
 *
 * @returns 성공 시 true, 실패 시 false
 */
async function refreshTokens(): Promise<boolean> {
  try {
    const response = await fetch('/api/auth/refresh', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      return false;
    }

    const data = await response.json();
    return data.success === true;
  } catch (error) {
    console.error('Token refresh failed:', error);
    return false;
  }
}

/**
 * 로그아웃 처리
 */
async function handleLogout() {
  try {
    // 로그아웃 API 호출
    await fetch('/api/auth/logout', {
      method: 'POST',
    });
  } catch (error) {
    console.error('Logout error:', error);
  } finally {
    // 로그인 페이지로 리다이렉트
    window.location.href = '/signin';
  }
}

/**
 * API 요청을 보내고 자동으로 토큰 갱신을 처리하는 fetch wrapper
 *
 * @param endpoint - API 엔드포인트 (예: '/api/users')
 * @param options - fetch 옵션
 * @returns fetch Response
 *
 * @example
 * ```typescript
 * const response = await apiFetch('/api/users', {
 *   method: 'GET',
 *   requiresAuth: true,
 * });
 * ```
 */
export async function apiFetch(
  endpoint: string,
  options: ApiFetchOptions = {}
): Promise<Response> {
  const { requiresAuth = true, ...fetchOptions } = options;

  // 기본 헤더 설정
  const headers = new Headers(fetchOptions.headers);
  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  const url = endpoint.startsWith('http')
    ? endpoint
    : `${BACKEND_API_URL}${endpoint}`;

  // 첫 번째 요청
  let response = await fetch(url, {
    ...fetchOptions,
    headers,
    credentials: 'include', // 쿠키 포함
  });

  // 401 에러가 발생하고 인증이 필요한 요청인 경우
  if (response.status === 401 && requiresAuth) {
    // 이미 토큰 갱신 중인 경우 대기
    if (isRefreshing && refreshPromise) {
      const refreshSuccess = await refreshPromise;

      if (refreshSuccess) {
        // 토큰 갱신 성공 시 재시도
        response = await fetch(url, {
          ...fetchOptions,
          headers,
          credentials: 'include',
        });
      } else {
        // 토큰 갱신 실패 시 로그아웃
        await handleLogout();
        throw new Error('Authentication failed. Please login again.');
      }
    } else {
      // 토큰 갱신 시작
      isRefreshing = true;
      refreshPromise = refreshTokens();

      try {
        const refreshSuccess = await refreshPromise;

        if (refreshSuccess) {
          // 토큰 갱신 성공 시 재시도
          response = await fetch(url, {
            ...fetchOptions,
            headers,
            credentials: 'include',
          });
        } else {
          // 토큰 갱신 실패 시 로그아웃
          await handleLogout();
          throw new Error('Authentication failed. Please login again.');
        }
      } finally {
        isRefreshing = false;
        refreshPromise = null;
      }
    }
  }

  return response;
}

/**
 * JSON 응답을 기대하는 API 요청
 *
 * @param endpoint - API 엔드포인트
 * @param options - fetch 옵션
 * @returns JSON 파싱된 응답 데이터
 */
export async function apiFetchJson<T = any>(
  endpoint: string,
  options: ApiFetchOptions = {}
): Promise<T> {
  const response = await apiFetch(endpoint, options);

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(
      errorData.message || `API request failed with status ${response.status}`
    );
  }

  return response.json();
}
