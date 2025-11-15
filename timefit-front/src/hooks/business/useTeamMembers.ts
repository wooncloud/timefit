import { useState, useEffect } from 'react';
import { toast } from 'sonner';
import { handleAuthError } from '@/lib/api/handle-auth-error';
import type {
  MemberListResponse,
  GetTeamMembersHandlerResponse,
  ChangeMemberRoleRequest,
  ChangeMemberRoleHandlerResponse,
  MemberStatusChangeHandlerResponse,
  DeleteMemberHandlerResponse,
  BusinessRole,
} from '@/types/business/teamMember';

export function useTeamMembers(businessId: string) {
  const [data, setData] = useState<MemberListResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [updating, setUpdating] = useState(false);

  const fetchTeamMembers = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await fetch(`/api/business/${businessId}/members`);
      const result: GetTeamMembersHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return;
      }

      if (!result.success) {
        setError(result.message || '구성원 목록을 가져올 수 없습니다.');
        return;
      }

      setData(result.data || null);
    } catch (err) {
      setError('구성원 목록을 가져오는 중 오류가 발생했습니다.');
      console.error('Failed to fetch team members:', err);
    } finally {
      setLoading(false);
    }
  };

  const changeMemberRole = async (
    userId: string,
    newRole: BusinessRole
  ): Promise<boolean> => {
    try {
      setUpdating(true);

      const body: ChangeMemberRoleRequest = { newRole };
      const response = await fetch(
        `/api/business/${businessId}/member/${userId}/role`,
        {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body),
        }
      );

      const result: ChangeMemberRoleHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '권한 변경에 실패했습니다.');
        return false;
      }

      // Refetch data to update UI
      await fetchTeamMembers();
      return true;
    } catch (err) {
      console.error('Failed to change member role:', err);
      toast.error('권한 변경 중 오류가 발생했습니다.');
      return false;
    } finally {
      setUpdating(false);
    }
  };

  const activateMember = async (userId: string): Promise<boolean> => {
    try {
      setUpdating(true);

      const response = await fetch(
        `/api/business/${businessId}/member/${userId}/activate`,
        {
          method: 'PATCH',
        }
      );

      const result: MemberStatusChangeHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '활성화에 실패했습니다.');
        return false;
      }

      // Refetch data to update UI
      await fetchTeamMembers();
      return true;
    } catch (err) {
      console.error('Failed to activate member:', err);
      toast.error('활성화 중 오류가 발생했습니다.');
      return false;
    } finally {
      setUpdating(false);
    }
  };

  const deactivateMember = async (userId: string): Promise<boolean> => {
    try {
      setUpdating(true);

      const response = await fetch(
        `/api/business/${businessId}/member/${userId}/deactivate`,
        {
          method: 'PATCH',
        }
      );

      const result: MemberStatusChangeHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '비활성화에 실패했습니다.');
        return false;
      }

      // Refetch data to update UI
      await fetchTeamMembers();
      return true;
    } catch (err) {
      console.error('Failed to deactivate member:', err);
      toast.error('비활성화 중 오류가 발생했습니다.');
      return false;
    } finally {
      setUpdating(false);
    }
  };

  const deleteMember = async (userId: string): Promise<boolean> => {
    try {
      setUpdating(true);

      const response = await fetch(
        `/api/business/${businessId}/member/${userId}`,
        {
          method: 'DELETE',
        }
      );

      const result: DeleteMemberHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '구성원 삭제에 실패했습니다.');
        return false;
      }

      // Refetch data to update UI
      await fetchTeamMembers();
      return true;
    } catch (err) {
      console.error('Failed to delete member:', err);
      toast.error('구성원 삭제 중 오류가 발생했습니다.');
      return false;
    } finally {
      setUpdating(false);
    }
  };

  useEffect(() => {
    if (businessId) {
      fetchTeamMembers();
    }
  }, [businessId]);

  return {
    data,
    loading,
    error,
    updating,
    refetch: fetchTeamMembers,
    changeMemberRole,
    activateMember,
    deactivateMember,
    deleteMember,
  };
}
