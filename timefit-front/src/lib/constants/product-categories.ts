import type { ProductCategory, MenuType } from '@/types/product/product';

export const productCategories: Record<ProductCategory, string> = {
  HAIRCUT: '커트',
  STYLING: '스타일링',
  PERM: '펌',
  COLOR: '염색',
  TREATMENT: '트리트먼트',
};

export const menuTypes: Record<MenuType, string> = {
  RESERVATION_BASED: '관리자 확인 후 확정',
  ONDEMAND_BASED: '예약 신청과 동시에 바로 확정',
};
