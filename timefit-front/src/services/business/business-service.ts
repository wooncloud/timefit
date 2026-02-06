import 'server-only';

import type {
  ApiResponse,
  PublicBusinessDetail,
} from '@/types/business/business';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 업체 상세 정보 조회 (공개 API)
 * SSR을 위한 서버 컴포넌트에서 사용됨
 * 인증 불필요
 */
export async function getBusinessDetail(
  businessId: string
): Promise<PublicBusinessDetail> {
  const response = await fetch(`${BACKEND_URL}/api/business/${businessId}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    cache: 'no-store',
  });

  if (!response.ok) {
    throw new Error('업체 정보를 가져오는 데 실패했습니다.');
  }

  const result: ApiResponse<PublicBusinessDetail> = await response.json();

  if (!result.data) {
    throw new Error('업체 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}
