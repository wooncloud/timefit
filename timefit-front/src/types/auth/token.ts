/**
 * 토큰 쌍 (accessToken + refreshToken)
 */
export interface TokenPair {
  accessToken: string;
  refreshToken: string;
}

/**
 * 백엔드 토큰 갱신 API 응답 형식
 * Endpoint: POST /api/auth/refresh
 */
export interface RefreshTokenResponse {
  data: {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
  };
}

/**
 * HTTP 상태 코드 상수
 */
export const HTTP_STATUS = {
  UNAUTHORIZED: 401,
  INTERNAL_SERVER_ERROR: 500,
} as const;

/**
 * 인증 관련 메시지 상수
 */
export const AUTH_MESSAGES = {
  AUTHENTICATION_REQUIRED: '인증이 필요합니다.',
  SESSION_EXPIRED: '세션이 만료되었습니다. 다시 로그인해주세요.',
  SERVER_ERROR: '서버 오류가 발생했습니다.',
} as const;
