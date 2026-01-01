import { useState } from 'react';
import { toast } from 'sonner';

import type { UpdateOperatingHoursRequest } from '@/types/schedule/operating-hours';
import { updateOperatingHours } from '@/services/schedule/operating-hours-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useUpdateOperatingHours(businessId: string) {
  const [loading, setLoading] = useState(false);

  const update = async (
    data: UpdateOperatingHoursRequest
  ): Promise<boolean> => {
    try {
      setLoading(true);

      const result = await updateOperatingHours(businessId, data);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '영업시간 저장에 실패했습니다.');
        return false;
      }

      toast.success('영업시간이 저장되었습니다.');
      return true;
    } catch (err) {
      console.error('영업시간 저장 실패:', err);
      toast.error('영업시간 저장 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    updateOperatingHours: update,
    loading,
  };
}
