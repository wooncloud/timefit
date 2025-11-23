/**
 * 인증된 사용자의 비즈니스 정보
 */
export interface AuthBusinessInfo {
  businessId?: string;
  businessName?: string;
  businessTypes?: string[];
  address?: string;
  contactPhone?: string;
  description?: string;
  logoUrl?: string;
  role?: string;
  joinedAt?: string;
  isActive?: boolean;
  createdAt?: string;
}

/**
 * 인증된 사용자의 프로필 정보
 */
export interface AuthUserProfile {
  userId?: string;
  email: string;
  name: string;
  phoneNumber?: string;
  role?: string;
  profileImageUrl?: string;
  businesses?: AuthBusinessInfo[];
  createdAt?: string;
  lastLoginAt?: string;
}
