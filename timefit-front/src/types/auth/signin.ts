/**
 * 백엔드 회원 로그인 요청 본문.
 */
export interface SigninRequestBody {
  email: string;
  password: string;
}

/**
 * 백엔드 회원 로그인 응답 구조.
 */
export interface SigninApiResponse {
  message?: string;
  data?: SigninSuccessPayload;
}

/**
 * 백엔드 로그인 성공 시 포함되는 사용자 데이터.
 */
export interface SigninSuccessPayload extends Record<string, unknown> {
  accessToken?: string;
}

/**
 * Next.js 로그인 라우트 성공 응답.
 */
export interface SigninHandlerSuccessResponse {
  success: true;
  message: string;
  data: SigninSuccessPayload;
}

/**
 * Next.js 로그인 라우트 실패 응답.
 */
export interface SigninHandlerErrorResponse {
  success: false;
  message: string;
}

/**
 * 로그인 라우트의 응답 타입.
 */
export type SigninHandlerResponse =
  | SigninHandlerSuccessResponse
  | SigninHandlerErrorResponse;

/**
 * 로그인 폼 상태.
 */
export interface SigninFormData {
  email: string;
  password: string;
}

/**
 * 로그인 폼 유효성 검사 오류.
 */
export type SigninFormErrors = Partial<Record<keyof SigninFormData, string>>;
