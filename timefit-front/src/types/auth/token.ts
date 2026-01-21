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
