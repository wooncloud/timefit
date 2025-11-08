/**
 * 내 사업자 목록 - 사업자 요약 정보
 */
export interface MyBusinessItem {
  businessId: string;
  businessName: string;
  businessTypes: string[];
  address: string;
  logoUrl?: string;
  myRole: string;
  isActive: boolean;
}

/**
 * 백엔드 API 응답
 */
export interface GetMyBusinessApiResponse {
  data?: MyBusinessItem[];
  message?: string;
}

/**
 * Next.js API 라우트 응답
 */
export interface GetMyBusinessHandlerResponse {
  success: boolean;
  data?: MyBusinessItem[];
  message?: string;
}
