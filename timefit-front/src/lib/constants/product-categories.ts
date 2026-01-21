import type { MenuType } from '@/types/product/product';

export const menuTypes: Record<MenuType, string> = {
  RESERVATION_BASED: '관리자 확인 후 확정',
  ONDEMAND_BASED: '예약 신청과 동시에 바로 확정',
};
