import 'server-only';

import type {
  GetBusinessDetailApiResponse,
  PublicBusinessDetail,
} from '@/types/business/business-detail';
import { getServerSession } from '@/lib/session/server';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 비즈니스 상세 정보 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getBusinessDetail(
  businessId: string
): Promise<PublicBusinessDetail> {
  const session = await getServerSession();

  const response = await fetch(`${BACKEND_URL}/api/business/${businessId}`, {
    headers: {
      Authorization: `Bearer ${session.user?.accessToken}`,
      'Content-Type': 'application/json',
    },
    cache: 'no-store',
  });

  if (!response.ok) {
    throw new Error('업체 상세 정보를 가져오는 데 실패했습니다.');
  }

  const result: GetBusinessDetailApiResponse = await response.json();

  if (!result.data) {
    throw new Error('업체 정보를 찾을 수 없습니다.');
  }

  return result.data;
}
