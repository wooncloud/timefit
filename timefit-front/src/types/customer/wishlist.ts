/**
 * 찜 아이템 (백엔드 DTO 미러링)
 */
export interface WishlistItem {
  wishlistId: string;
  businessId: string;
  businessName: string;
  businessTypes: string[];
  ownerName: string;
  address: string;
  contactPhone: string;
  description: string | null;
  logoUrl: string | null;
  averageRating: number | null;
  reviewCount: number;
  latitude: number | null;
  longitude: number | null;
  createdAt: string;
}

/**
 * 찜 목록 응답 (페이징)
 */
export interface WishlistList {
  wishlists: WishlistItem[];
  totalCount: number;
  page: number;
  size: number;
  totalPages: number;
}

// API 응답 타입 (백엔드 → Next.js 서버)
export interface GetWishlistListApiResponse {
  data?: WishlistList;
  message?: string;
}

export interface CheckWishlistApiResponse {
  data?: boolean;
  message?: string;
}

// 찜 추가 요청
export interface AddWishlistRequest {
  businessId: string;
}

// 핸들러 응답 타입 (Next.js API → 클라이언트)
export interface AddWishlistHandlerResponse {
  success: boolean;
  data?: { businessId: string; message: string };
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface DeleteWishlistHandlerResponse {
  success: boolean;
  data?: { businessId: string; message: string };
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}
