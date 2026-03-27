/**
 * 업체 상세 정보 (공개 API - 고객용)
 * 백엔드 BusinessResponse.PublicBusinessResponse에 대응
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
 * 업체 프로필 상세 정보 (사업자용 - 민감 정보 포함)
 * 백엔드 BusinessResponse.BusinessResponse에 대응
 * GET /api/business/{id}/profile 응답
 */
export interface BusinessProfileDetail {
  businessId: string;
  businessName: string;
  businessTypes: string[];
  businessNumber: string;
  ownerName: string;
  address: string;
  contactPhone: string;
  contactEmail: string | null;
  description: string;
  logoUrl?: string;
  businessNotice?: string;
  isActive: boolean;
  myRole: 'OWNER' | 'MANAGER' | 'MEMBER';
  createdAt: string;
  updatedAt: string;
}

/**
 * 업체 정보 수정 요청
 * 백엔드 BusinessRequestDto.UpdateBusinessRequest에 대응
 */
export interface UpdateBusinessRequest {
  businessName?: string;
  businessTypes?: string[];
  businessNumber?: string;
  ownerName?: string;
  address?: string;
  contactPhone?: string;
  contactEmail?: string;
  description?: string;
  logoUrl?: string;
  businessNotice?: string;
}

/**
 * 업체 프로필 정보 (수정 후 응답)
 * 백엔드 BusinessResponse.BusinessResponse에 대응
 */
export interface BusinessProfile {
  businessId: string;
  businessName: string;
  businessTypes: string[];
  businessNumber: string;
  ownerName: string;
  address: string;
  contactPhone: string;
  contactEmail: string | null;
  description: string;
  logoUrl?: string;
  businessNotice?: string;
  isActive: boolean;
  myRole: 'OWNER' | 'MANAGER' | 'MEMBER';
}

/**
 * 백엔드 API 응답 - 사업자용 프로필 조회
 */
export interface GetBusinessProfileApiResponse {
  data?: BusinessProfileDetail;
  message?: string;
}

/**
 * 백엔드 API 응답 - 공개 조회 (기존 유지)
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
 * Next.js API 라우트 응답 - 사업자용 프로필 조회
 */
export interface GetBusinessProfileHandlerResponse {
  success: boolean;
  data?: BusinessProfileDetail;
  message?: string;
}

/**
 * Next.js API 라우트 응답 - 공개 조회 (기존 유지)
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