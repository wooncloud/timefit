import 'server-only';

import type {
  GetBusinessProfileApiResponse,
  BusinessProfileDetail,
} from '@/types/business/business-detail';
import { apiFetch } from '@/lib/api/api-fetch';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 사업자용 업체 프로필 조회 (민감 정보 포함)
 * GET /api/business/{businessId}/profile
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getBusinessDetail(
  businessId: string
): Promise<BusinessProfileDetail> {
  const response = await apiFetch(
    `${BACKEND_URL}/api/business/${businessId}/profile`,
    { method: 'GET' }
  );

  if (!response.ok) {
    throw new Error('업체 상세 정보를 가져오는 데 실패했습니다.');
  }

  const result: GetBusinessProfileApiResponse = await response.json();

  if (!result.data) {
    throw new Error('업체 정보를 찾을 수 없습니다.');
  }

  return result.data;
}