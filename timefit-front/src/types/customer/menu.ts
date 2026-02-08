/**
 * 메뉴 관련 타입 정의
 */

/**
 * 주문/예약 유형
 */
export type OrderType = 'RESERVATION_BASED' | 'ONDEMAND_BASED';

/**
 * 메뉴 상세 정보
 */
export interface Menu {
  menuId: string;
  businessId: string;
  serviceName: string;
  businessCategoryId: string;
  businessType: string;
  categoryName: string;
  price: number;
  description: string | null;
  orderType: OrderType;
  durationMinutes: number | null;
  imageUrl: string | null;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * 메뉴 목록 응답
 */
export interface MenuList {
  menus: Menu[];
  totalCount: number;
}

/**
 * 메뉴 목록 조회 API 응답
 */
export interface GetMenuListApiResponse {
  data?: MenuList;
  message?: string;
}
