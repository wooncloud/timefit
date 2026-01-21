import 'server-only';

import type {
  CategoryListResponse,
  GetCategoryListApiResponse,
} from '@/types/category/category';
import { apiFetch } from '@/lib/api/api-fetch';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 카테고리 목록 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getCategoryList(
  businessId: string
): Promise<CategoryListResponse> {
  const response = await apiFetch(
    `${BACKEND_URL}/api/business/${businessId}/categories`,
    { method: 'GET' }
  );

  if (!response.ok) {
    throw new Error('카테고리 목록을 가져오는 데 실패했습니다.');
  }

  const result: GetCategoryListApiResponse = await response.json();

  if (!result.data) {
    throw new Error('카테고리 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}
