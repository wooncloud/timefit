/**
 * 백엔드 토큰 갱신 요청 본문.
 */
export interface RefreshRequestBody {
  refreshToken: string;
}

/**
 * 백엔드 토큰 갱신 응답 데이터.
 */
export interface RefreshTokenData {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

/**
 * 백엔드 토큰 갱신 응답 구조.
 */
export interface RefreshApiResponse {
  success: boolean;
  message?: string;
  data?: RefreshTokenData;
}

/**
 * Next.js 토큰 갱신 라우트 성공 응답.
 */
export interface RefreshHandlerSuccessResponse {
  success: true;
  message: string;
  data: RefreshTokenData;
}

/**
 * Next.js 토큰 갱신 라우트 실패 응답.
 */
export interface RefreshHandlerErrorResponse {
  success: false;
  message: string;
}

/**
 * 토큰 갱신 라우트의 응답 타입.
 */
export type RefreshHandlerResponse =
  | RefreshHandlerSuccessResponse
  | RefreshHandlerErrorResponse;
