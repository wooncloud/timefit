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
 * 업체 정보 수정 요청
 * 백엔드 BusinessRequest.UpdateBusiness에 대응
 */
export interface UpdateBusinessRequest {
  businessName?: string;
  businessTypes?: string[];
  businessNumber?: string;
  address?: string;
  contactPhone?: string;
  description?: string;
  logoUrl?: string;
  businessNotice?: string;
}

/**
 * 업체 프로필 정보 (수정 후 응답)
 * 백엔드 BusinessResponse.BusinessProfile에 대응
 */
export interface BusinessProfile {
  businessId: string;
  businessName: string;
  businessTypes: string[];
  businessNumber: string;
  address: string;
  contactPhone: string;
  description: string;
  logoUrl?: string;
  businessNotice?: string;
  isActive: boolean;
}

/**
 * 백엔드 API 응답 - 조회
 */
export interface GetBusinessDetailApiResponse {
  data?: PublicBusinessDetail;
  message?: string;
}

/**
 * 백엔드 API 응답 - 수정
 */
export interface UpdateBusinessApiResponse {
  data?: BusinessProfile;
  message?: string;
}

/**
 * Next.js API 라우트 응답 - 조회
 */
export interface GetBusinessDetailHandlerResponse {
  success: boolean;
  data?: PublicBusinessDetail;
  message?: string;
}

/**
 * Next.js API 라우트 응답 - 수정
 */
export interface UpdateBusinessHandlerResponse {
  success: boolean;
  data?: BusinessProfile;
  message?: string;
}
