import type { ToggleOperatingHoursHandlerResponse } from '@/types/business/operating-hours';

/**
 * 클라이언트 측 클래스: 운영 시간 변경 (Mutations)
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class OperatingHoursService {
  /**
   * 운영 시간 토글 (활성화/비활성화)
   * API 라우트를 통한 클라이언트 측 호출
   */
  async toggleOperatingHours(
    businessId: string,
    dayOfWeek: number
  ): Promise<ToggleOperatingHoursHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/operating-hours/${dayOfWeek}/toggle`,
      {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
      }
    );

    return response.json();
  }
}

export const operatingHoursService = new OperatingHoursService();
