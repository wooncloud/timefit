import { useState } from 'react';
import { toast } from 'sonner';

import { teamService } from '@/services/team/team-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useDeleteMember(businessId: string) {
  const [loading, setLoading] = useState(false);

  const deleteMember = async (userId: string): Promise<boolean> => {
    try {
      setLoading(true);

      const result = await teamService.deleteMember(businessId, userId);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '구성원 삭제에 실패했습니다.');
        return false;
      }

      toast.success('구성원이 삭제되었습니다.');
      return true;
    } catch (err) {
      console.error('구성원 삭제 실패:', err);
      toast.error('구성원 삭제 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    deleteMember,
    loading,
  };
}
