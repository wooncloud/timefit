'use client';

import { useEffect, useState } from 'react';

import type { UserProfile } from '@/types/user/profile';
import { userService } from '@/services/user/user-service.client';

/**
 * 사용자 프로필 조회 훅 (GET /api/customer/profile)
 */
export function useUserProfile() {
  const [data, setData] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refetch = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const response = await userService.getUserProfile();
      if (response.data) {
        setData(response.data);
      }
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : '프로필 정보를 불러오는데 실패했습니다.';
      setError(errorMessage);
      console.error('프로필 조회 오류:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    refetch();
  }, []);

  return { data, isLoading, error, refetch };
}
