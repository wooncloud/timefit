import { useState } from 'react';
import { toast } from 'sonner';

import type { UpdateBusinessRequest } from '@/types/business/business-detail';
import { businessDetailService } from '@/services/business/business-detail-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useUpdateBusiness(businessId: string) {
  const [updating, setUpdating] = useState(false);

  const updateBusiness = async (
    data: UpdateBusinessRequest
  ): Promise<boolean> => {
    try {
      setUpdating(true);

      const result = await businessDetailService.updateBusiness(
        businessId,
        data
      );

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '업체 정보 수정에 실패했습니다.');
        return false;
      }

      toast.success('업체 정보가 수정되었습니다.');
      return true;
    } catch (err) {
      console.error('업체 정보 수정 실패:', err);
      toast.error('업체 정보 수정 중 오류가 발생했습니다.');
      return false;
    } finally {
      setUpdating(false);
    }
  };

  return {
    updateBusiness,
    updating,
  };
}
