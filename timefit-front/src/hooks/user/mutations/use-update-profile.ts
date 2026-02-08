'use client';

import { useState } from 'react';

import type { UpdateProfileRequest, UserProfile } from '@/types/user/profile';
import { userService } from '@/services/user/user-service.client';

interface UseUpdateProfileOptions {
  onSuccess?: (data: UserProfile) => void;
  onError?: (error: string) => void;
}

/**
 * 프로필 수정 훅 (PUT /api/customer/profile)
 */
export function useUpdateProfile(options: UseUpdateProfileOptions = {}) {
  const { onSuccess, onError } = options;
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateProfile = async (data: UpdateProfileRequest) => {
    try {
      setIsLoading(true);
      setError(null);

      const response = await userService.updateProfile(data);

      if (response.data) {
        onSuccess?.(response.data);
        return response.data;
      }
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : '프로필 수정에 실패했습니다.';
      setError(errorMessage);
      onError?.(errorMessage);
      console.error('프로필 수정 오류:', err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { updateProfile, isLoading, error };
}
