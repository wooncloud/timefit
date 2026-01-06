import { useState } from 'react';
import { toast } from 'sonner';

import { operatingHoursService } from '@/services/operating-hours/operating-hours-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

/**
 * 운영 시간 토글 Hook
 * @param businessId 업체 ID
 */
export function useToggleOperatingHours(businessId: string) {
  const [loading, setLoading] = useState(false);

  const toggleOperatingHours = async (
    dayOfWeek: number
  ): Promise<boolean> => {
    try {
      setLoading(true);
      const result =
        await operatingHoursService.toggleOperatingHours(businessId, dayOfWeek);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '운영 시간 토글에 실패했습니다.');
        return false;
      }

      toast.success('운영 시간이 업데이트되었습니다.');
      return true;
    } catch (err) {
      console.error('운영 시간 토글 실패:', err);
      toast.error('운영 시간 토글 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { toggleOperatingHours, loading };
}
