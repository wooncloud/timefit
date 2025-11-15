import { useState, useEffect } from 'react';
import { handleAuthError } from '@/lib/api/handle-auth-error';
import type {
  MemberListResponse,
  GetTeamMembersHandlerResponse,
} from '@/types/business/teamMember';

export function useTeamMembers(businessId: string) {
  const [data, setData] = useState<MemberListResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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

  useEffect(() => {
    if (businessId) {
      fetchTeamMembers();
    }
  }, [businessId]);

  return {
    data,
    loading,
    error,
    refetch: fetchTeamMembers,
  };
}
