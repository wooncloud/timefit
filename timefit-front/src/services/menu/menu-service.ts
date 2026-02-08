import 'server-only';

import type { GetMenuListApiResponse, MenuList } from '@/types/customer/menu';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 업체의 메뉴 리스트 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 *
 * @param businessId - 업체 ID
 * @param filters - 필터링 옵션 (선택적)
 * @returns 메뉴 리스트
 */
export async function getMenuList(
  businessId: string,
  filters?: {
    serviceName?: string;
    businessCategoryId?: string;
    minPrice?: number;
    maxPrice?: number;
    isActive?: boolean;
  }
): Promise<MenuList> {
  const params = new URLSearchParams();

  if (filters?.serviceName) params.append('serviceName', filters.serviceName);
  if (filters?.businessCategoryId)
    params.append('businessCategoryId', filters.businessCategoryId);
  if (filters?.minPrice !== undefined)
    params.append('minPrice', filters.minPrice.toString());
  if (filters?.maxPrice !== undefined)
    params.append('maxPrice', filters.maxPrice.toString());
  if (filters?.isActive !== undefined)
    params.append('isActive', filters.isActive.toString());

  const queryString = params.toString();
  const url = `${BACKEND_URL}/api/business/${businessId}/menu${queryString ? `?${queryString}` : ''}`;

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    cache: 'no-store',
  });

  if (!response.ok) {
    throw new Error('메뉴 리스트를 가져오는 데 실패했습니다.');
  }

  const result: GetMenuListApiResponse = await response.json();

  if (!result.data) {
    throw new Error('메뉴 리스트 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}
