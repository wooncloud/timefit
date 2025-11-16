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
