/**
 * 인증 미들웨어
 *
 * API 요청 시 인증 토큰을 자동으로 관리하는 미들웨어
 *
 * ## 주요 기능
 * - Access Token 자동 첨부
 * - Access Token 만료 시 Refresh Token으로 자동 갱신
 * - Refresh Token 만료 시 자동 로그아웃
 *
 * ## 사용법
 *
 * 클라이언트 컴포넌트에서:
 * ```typescript
 * import { apiFetch, apiFetchJson } from '@/lib/api/auth-middleware';
 *
 * // 일반 fetch 래퍼
 * const response = await apiFetch('/api/users', {
 *   method: 'GET',
 *   requiresAuth: true,
 * });
 *
 * // JSON 응답을 자동으로 파싱
 * const users = await apiFetchJson<User[]>('/api/users', {
 *   method: 'GET',
 * });
 * ```
 *
 * 서버 컴포넌트에서:
 * ```typescript
 * import { serverApiFetch, serverApiFetchJson } from '@/lib/api/auth-middleware';
 *
 * const users = await serverApiFetchJson<User[]>('/api/users');
 * ```
 */

// 클라이언트 측 API 클라이언트 (브라우저에서 실행)
export { apiFetch, apiFetchJson } from './client';

// 서버 측 API 클라이언트 (Server Components에서 실행)
export { serverApiFetch, serverApiFetchJson } from './server-client';

/**
 * 토큰 갱신 처리
 *
 * 이 함수는 내부적으로 사용되며, apiFetch에서 자동으로 호출됩니다.
 * 직접 호출할 필요가 없습니다.
 *
 * @deprecated 이 함수는 apiFetch 내부에서 자동으로 처리됩니다.
 */
export async function refreshAccessToken(): Promise<boolean> {
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
 *
 * 세션을 종료하고 로그인 페이지로 리다이렉트합니다.
 */
export async function logout(): Promise<void> {
  try {
    await fetch('/api/auth/logout', {
      method: 'POST',
    });
  } catch (error) {
    console.error('Logout error:', error);
  } finally {
    window.location.href = '/signin';
  }
}

/**
 * 인증 상태 확인
 *
 * 클라이언트 측에서 현재 사용자의 인증 상태를 확인합니다.
 * 세션 정보가 필요한 경우 서버 컴포넌트에서 getServerSession을 사용하세요.
 */
export async function checkAuthStatus(): Promise<boolean> {
  try {
    const response = await fetch('/api/auth/status', {
      method: 'GET',
    });

    return response.ok;
  } catch (error) {
    console.error('Auth status check failed:', error);
    return false;
  }
}
