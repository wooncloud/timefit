/**
 * 사업자 회원가입 요청 본문 구조.
 */
export interface CreateBusinessRequestBody {
  businessName: string;
  businessTypes: string[];
  businessNumber: string;
  address: string;
  contactPhone: string;
  description?: string;
}

/**
 * 백엔드 사업자 등록 API 응답.
 */
export interface CreateBusinessApiResponse {
  message?: string;
  data?: CreateBusinessSuccessPayload;
}

/**
 * 사업자 등록 성공 시 백엔드에서 반환하는 데이터.
 */
export interface CreateBusinessSuccessPayload extends Record<string, unknown> {
  businessId?: string;
  businessName?: string;
  businessTypes?: string[];
  businessNumber?: string;
  address?: string;
  contactPhone?: string;
  description?: string;
  logoUrl?: string;
  myRole?: string;
  totalMembers?: number;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * Next.js 사업자 등록 라우트 성공 응답.
 */
export interface CreateBusinessHandlerSuccessResponse {
  success: true;
  message: string;
  data: CreateBusinessSuccessPayload;
}

/**
 * Next.js 사업자 등록 라우트 실패 응답.
 */
export interface CreateBusinessHandlerErrorResponse {
  success: false;
  message: string;
}

/**
 * Next.js 사업자 등록 라우트 응답 타입.
 */
export type CreateBusinessHandlerResponse =
  | CreateBusinessHandlerSuccessResponse
  | CreateBusinessHandlerErrorResponse;

/**
 * 사업자 등록 폼 상태.
 */
export interface BusinessSignupFormData {
  businessName: string;
  businessTypes: string[];
  businessNumber: string;
  address: string;
  contactPhone: string;
  description: string;
}

/**
 * 사업자 등록 폼 오류 상태.
 */
export type BusinessSignupFormErrors = Partial<
  Record<keyof BusinessSignupFormData, string>
>;
