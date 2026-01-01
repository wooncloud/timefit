import type {
  UpdateOperatingHoursRequest,
  UpdateOperatingHoursResponse,
} from '@/types/schedule/operating-hours';

/**
 * 클라이언트 측 함수: 영업시간 및 예약 슬롯 업데이트
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
export async function updateOperatingHours(
  businessId: string,
  request: UpdateOperatingHoursRequest
): Promise<UpdateOperatingHoursResponse & { message?: string }> {
  const response = await fetch(`/api/business/${businessId}/operating-hours`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  return response.json();
}
