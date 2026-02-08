// 사용자 프로필 관련 타입 정의

/**
 * 현재 사용자 정보 (GET /api/user/me)
 */
export interface CurrentUser {
  userId: string;
  email: string;
  name: string;
  phoneNumber: string;
  profileImageUrl: string | null;
  createdAt: string;
  lastLoginAt: string;
  businesses: BusinessInfo[];
}

/**
 * 업체 정보
 */
export interface BusinessInfo {
  businessId: string;
  businessName: string;
  logoUrl: string | null;
  myRole: string;
  isActive: boolean;
}

/**
 * 사용자 프로필 (GET /api/customer/profile)
 */
export interface UserProfile {
  userId: string;
  email: string;
  name: string;
  phoneNumber: string;
  profileImageUrl: string | null;
  createdAt: string;
  lastLoginAt: string;
  statistics: UserStatistics;
}

/**
 * 사용자 통계
 */
export interface UserStatistics {
  totalReservations: number;
  wishlistCount: number;
  reviewCount: number;
}

/**
 * 프로필 수정 요청 (PUT /api/customer/profile)
 */
export interface UpdateProfileRequest {
  name?: string;
  phoneNumber?: string;
  profileImageUrl?: string;
}

/**
 * 비밀번호 변경 요청 (PUT /api/customer/profile/password)
 */
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  newPasswordConfirm: string;
}

/**
 * 백엔드 API 응답 래퍼 (ResponseData 구조)
 */
export interface ApiResponse<T> {
  data?: T;
  errorResponse?: {
    code: string;
    message: string;
    field?: string;
  };
}
