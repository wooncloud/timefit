import { useState } from 'react';
import { toast } from 'sonner';

import { teamService } from '@/services/team/team-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useActivateMember(businessId: string) {
  const [loading, setLoading] = useState(false);

  const activateMember = async (userId: string): Promise<boolean> => {
    try {
      setLoading(true);

      const result = await teamService.activateMember(businessId, userId);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '활성화에 실패했습니다.');
        return false;
      }

      toast.success('구성원이 활성화되었습니다.');
      return true;
    } catch (err) {
      console.error('구성원 활성화 실패:', err);
      toast.error('활성화 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    activateMember,
    loading,
  };
}
