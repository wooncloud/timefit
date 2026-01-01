import { useState } from 'react';
import { toast } from 'sonner';

import { teamService } from '@/services/team/team-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';
import type { BusinessRole } from '@/types/business/team-member';

export function useChangeMemberRole(businessId: string) {
  const [loading, setLoading] = useState(false);

  const changeMemberRole = async (
    userId: string,
    newRole: BusinessRole
  ): Promise<boolean> => {
    try {
      setLoading(true);

      const result = await teamService.changeMemberRole(
        businessId,
        userId,
        newRole
      );

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '권한 변경에 실패했습니다.');
        return false;
      }

      toast.success('권한이 변경되었습니다.');
      return true;
    } catch (err) {
      console.error('구성원 권한 변경 실패:', err);
      toast.error('권한 변경 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    changeMemberRole,
    loading,
  };
}
