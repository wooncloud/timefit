import { useState } from 'react';
import { toast } from 'sonner';

import type { InviteMemberRequest } from '@/types/business/team-member';
import { teamService } from '@/services/team/team-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useInviteMember(businessId: string) {
  const [loading, setLoading] = useState(false);

  const inviteMember = async (data: InviteMemberRequest): Promise<boolean> => {
    try {
      setLoading(true);

      const result = await teamService.inviteMember(businessId, data);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '구성원 초대에 실패했습니다.');
        return false;
      }

      toast.success('구성원을 성공적으로 초대했습니다.');
      return true;
    } catch (err) {
      console.error('구성원 초대 실패:', err);
      toast.error('구성원 초대 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    inviteMember,
    loading,
  };
}
