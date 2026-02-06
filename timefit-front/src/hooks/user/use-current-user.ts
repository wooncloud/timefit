'use client';

import { useEffect, useState } from 'react';

import type { CurrentUser } from '@/types/user/profile';
import { userService } from '@/services/user/user-service.client';

/**
 * 현재 사용자 정보 조회 훅 (GET /api/user/me)
 */
export function useCurrentUser() {
  const [data, setData] = useState<CurrentUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCurrentUser = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const response = await userService.getCurrentUser();
        if (response.data) {
          setData(response.data);
        }
      } catch (err) {
        const errorMessage =
          err instanceof Error
            ? err.message
            : '사용자 정보를 불러오는데 실패했습니다.';
        setError(errorMessage);
        console.error('사용자 정보 조회 오류:', err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCurrentUser();
  }, []);

  return { data, isLoading, error };
}
