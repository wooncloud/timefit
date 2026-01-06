import { useState } from 'react';
import { toast } from 'sonner';

import { teamService } from '@/services/team/team-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useDeactivateMember(businessId: string) {
  const [loading, setLoading] = useState(false);

  const deactivateMember = async (userId: string): Promise<boolean> => {
    try {
      setLoading(true);

      const result = await teamService.deactivateMember(businessId, userId);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '비활성화에 실패했습니다.');
        return false;
      }

      toast.success('구성원이 비활성화되었습니다.');
      return true;
    } catch (err) {
      console.error('구성원 비활성화 실패:', err);
      toast.error('비활성화 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    deactivateMember,
    loading,
  };
}
