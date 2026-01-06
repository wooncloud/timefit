import 'server-only';

import type {
  GetOperatingHoursApiResponse,
  OperatingHours,
} from '@/types/schedule/operating-hours';
import { getServerSession } from '@/lib/session/server';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 업체의 영업시간 및 예약 가능 시간대 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 * 권한: 인증 필요
 */
export async function getOperatingHours(
  businessId: string
): Promise<OperatingHours> {
  const session = await getServerSession();

  const response = await fetch(
    `${BACKEND_URL}/api/business/${businessId}/operating-hours`,
    {
      headers: {
        Authorization: `Bearer ${session.user?.accessToken}`,
        'Content-Type': 'application/json',
      },
      cache: 'no-store',
    }
  );

  if (!response.ok) {
    throw new Error('영업시간 정보를 가져오는 데 실패했습니다.');
  }

  const result: GetOperatingHoursApiResponse = await response.json();


  if (!result.data) {
    throw new Error('영업시간 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}
