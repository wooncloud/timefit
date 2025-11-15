'use client';

/**
 * API 응답에서 인증 에러를 감지하고 자동으로 리다이렉트합니다.
 *
 * @param response - fetch 응답 객체 또는 JSON 데이터
 * @returns 인증 에러가 감지되었는지 여부
 *
 * @example
 * const response = await fetch('/api/business/123');
 * const data = await response.json();
 *
 * if (handleAuthError(data)) {
 *   return; // 이미 리다이렉트 처리됨
 * }
 */
export function handleAuthError(data: {
  success?: boolean;
  requiresLogout?: boolean;
  redirectTo?: string;
  message?: string;
}): boolean {
  if (data.requiresLogout && data.redirectTo) {
    // 세션 스토어 클리어 (선택적)
    if (typeof window !== 'undefined') {
      localStorage.clear();
      sessionStorage.clear();
    }

    // 메시지 표시 (선택적)
    if (data.message) {
      console.warn('[Auth Error]', data.message);
    }

    // 리다이렉트
    if (typeof window !== 'undefined') {
      window.location.href = data.redirectTo;
    }

    return true;
  }

  return false;
}
