import { useState } from 'react';
import { toast } from 'sonner';

import { authService } from '@/services/auth/auth-service.client';

/**
 * 로그아웃 뮤테이션 훅
 * 세션 정리 및 리다이렉트 처리
 */
export function useLogout() {
  const [loading, setLoading] = useState(false);

  const logout = async (redirectTo = '/'): Promise<boolean> => {
    if (loading) return false;

    try {
      setLoading(true);
      const result = await authService.logout();

      if (!result.success) {
        toast.error(result.message || '로그아웃에 실패했습니다.');
        return false;
      }

      localStorage.clear();
      sessionStorage.clear();

      toast.success('로그아웃되었습니다.');
      window.location.href = redirectTo;
      return true;
    } catch (err) {
      console.error('로그아웃 실패:', err);
      toast.error('로그아웃 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { logout, loading };
}
