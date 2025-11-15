/**
 * 업체 상세 정보 (공개 API)
 * 백엔드 BusinessResponse.PublicBusinessDetail에 대응
 */
export interface PublicBusinessDetail {
  businessId: string;
  businessName: string;
  businessTypes: string[];
  address: string;
  contactPhone: string;
  description: string;
  logoUrl?: string;
}

/**
 * 백엔드 API 응답
 */
export interface GetBusinessDetailApiResponse {
  data?: PublicBusinessDetail;
  message?: string;
}

/**
 * Next.js API 라우트 응답
 */
export interface GetBusinessDetailHandlerResponse {
  success: boolean;
  data?: PublicBusinessDetail;
  message?: string;
}
