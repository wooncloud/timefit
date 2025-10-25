export type ProductCategory = 'HAIRCUT' | 'STYLING' | 'PERM' | 'COLOR' | 'TREATMENT';
export type MenuType = 'RESERVATION_BASED' | 'ONDEMAND_BASED';

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
