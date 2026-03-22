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
  category: string; // 백엔드 카테고리 이름 (동적)
  description?: string | null;
  price: number;
  duration_minutes: number;
  menu_type: MenuType;
  image_url?: string | null;
  is_active: boolean;
  created_at: string;
  updated_at: string;

  // 슬롯 자동 생성 (생성 시에만 사용, 서버에 저장되지 않음)
  auto_generate_slots?: boolean;
  slot_start_date?: string; // YYYY-MM-DD
  slot_end_date?: string; // YYYY-MM-DD
  slot_interval_minutes?: number;
}
