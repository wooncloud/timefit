import 'server-only';

import type { Menu, MenuListResponse } from '@/types/menu/menu';
import { getServerSession } from '@/lib/session/server';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

interface GetMenuListOptions {
  serviceName?: string;
  businessCategoryId?: string;
  minPrice?: number;
  maxPrice?: number;
  isActive?: boolean;
}

/**
 * 서버 측 함수: 선택적 필터를 포함한 메뉴 목록 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getMenuList(
  businessId: string,
  options?: GetMenuListOptions
): Promise<MenuListResponse> {
  const session = await getServerSession();

  const params = new URLSearchParams();
  if (options?.serviceName) {
    params.append('serviceName', options.serviceName);
  }
  if (options?.businessCategoryId) {
    params.append('businessCategoryId', options.businessCategoryId);
  }
  if (options?.minPrice !== undefined) {
    params.append('minPrice', options.minPrice.toString());
  }
  if (options?.maxPrice !== undefined) {
    params.append('maxPrice', options.maxPrice.toString());
  }
  if (options?.isActive !== undefined) {
    params.append('isActive', options.isActive.toString());
  }

  const queryString = params.toString();
  const url = `${BACKEND_URL}/api/business/${businessId}/menu${queryString ? `?${queryString}` : ''}`;

  const response = await fetch(url, {
    headers: {
      Authorization: `Bearer ${session.user?.accessToken}`,
      'Content-Type': 'application/json',
    },
    cache: 'no-store',
  });

  if (!response.ok) {
    throw new Error('메뉴 목록을 가져오는 데 실패했습니다.');
  }

  const result = await response.json();

  if (!result.data) {
    throw new Error('메뉴 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}

/**
 * 서버 측 함수: 메뉴 상세 정보 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getMenuDetail(
  businessId: string,
  menuId: string
): Promise<Menu> {
  const session = await getServerSession();

  const response = await fetch(
    `${BACKEND_URL}/api/business/${businessId}/menu/${menuId}`,
    {
      headers: {
        Authorization: `Bearer ${session.user?.accessToken}`,
        'Content-Type': 'application/json',
      },
      cache: 'no-store',
    }
  );

  if (!response.ok) {
    throw new Error('메뉴 상세 정보를 가져오는 데 실패했습니다.');
  }

  const result = await response.json();

  if (!result.data) {
    throw new Error('메뉴 상세 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}
