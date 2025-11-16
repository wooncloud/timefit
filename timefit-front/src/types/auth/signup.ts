/**
 * 백엔드 회원가입 엔드포인트가 기대하는 요청 본문 형태.
 */
export interface SignupRequestBody {
  email: string;
  password: string;
  name: string;
  phoneNumber: string;
}

/**
 * 백엔드 회원가입 엔드포인트가 반환하는 기본 응답 구조.
 */
export interface SignupApiResponse {
  message?: string;
  data?: SignupSuccessPayload;
}

/**
 * 회원가입 성공 시 포함되는 사용자 데이터.
 */
export interface SignupSuccessPayload extends AuthUserProfile {
  accessToken?: string;
  refreshToken?: string;
}

/**
 * 클라이언트 회원가입 폼 상태. 비밀번호 확인 필드를 포함.
 */
export interface SignupFormData extends SignupRequestBody {
  confirmPassword: string;
}

/**
 * 폼 필드 이름과 검증 오류 메시지를 매핑.
 */
export type SignupFormErrors = Partial<Record<keyof SignupFormData, string>>;

/**
 * Next.js 라우트가 반환하는 성공 응답 형식.
 */
export interface SignupHandlerSuccessResponse {
  success: true;
  message: string;
  data: SignupSuccessPayload;
}

/**
 * Next.js 라우트가 반환하는 실패 응답 형식.
 */
export interface SignupHandlerErrorResponse {
  success: false;
  message: string;
}

/**
 * 회원가입 라우트 핸들러가 반환할 수 있는 응답 타입의 유니언.
 */
export type SignupHandlerResponse =
  | SignupHandlerSuccessResponse
  | SignupHandlerErrorResponse;
import type { AuthUserProfile } from '@/types/auth/user';
