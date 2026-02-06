import 'server-only';

import type {
  CheckWishlistApiResponse,
  GetWishlistListApiResponse,
  WishlistList,
} from '@/types/customer/wishlist';
import { apiFetch } from '@/lib/api/api-fetch';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 찜 목록 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getWishlistList(
  page: number = 0,
  size: number = 20
): Promise<WishlistList> {
  const response = await apiFetch(
    `${BACKEND_URL}/api/customer/wishlist?page=${page}&size=${size}`,
    { method: 'GET' }
  );

  if (!response.ok) {
    throw new Error('찜 목록을 가져오는 데 실패했습니다.');
  }

  const result: GetWishlistListApiResponse = await response.json();

  if (!result.data) {
    throw new Error('찜 목록 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}

/**
 * 서버 측 함수: 찜 여부 확인
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function checkWishlist(menuId: string): Promise<boolean> {
  try {
    const response = await apiFetch(
      `${BACKEND_URL}/api/customer/wishlist/check/${menuId}`,
      { method: 'GET' }
    );

    if (!response.ok) {
      return false;
    }

    const result: CheckWishlistApiResponse = await response.json();
    return result.data ?? false;
  } catch (error) {
    console.error('찜 여부 확인 실패:', error);
    return false;
  }
}
