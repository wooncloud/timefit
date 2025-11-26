/**
 * 상품 카테고리 타입
 */
export type ProductCategory =
  | 'HAIRCUT'
  | 'STYLING'
  | 'PERM'
  | 'COLOR'
  | 'TREATMENT';

/**
 * 메뉴 타입 (예약 기반 / 당일 접수 기반)
 */
export type MenuType = 'RESERVATION_BASED' | 'ONDEMAND_BASED';

/**
 * 상품(서비스) 정보
 */
export interface Product {
  id: string;
  business_id: string;
  service_name: string;
  category: ProductCategory;
  description?: string;
  price: number;
  duration_minutes: number;
  menu_type: MenuType;
  image_url?: string;
  is_active: boolean;
  created_at: string;
  updated_at: string;
}
