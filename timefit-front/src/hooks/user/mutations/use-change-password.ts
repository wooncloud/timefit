'use client';

import { useState } from 'react';

import type { ChangePasswordRequest } from '@/types/user/profile';
import { userService } from '@/services/user/user-service.client';

interface UseChangePasswordOptions {
  onSuccess?: () => void;
  onError?: (error: string) => void;
}

/**
 * 비밀번호 변경 훅 (PUT /api/customer/profile/password)
 */
export function useChangePassword(options: UseChangePasswordOptions = {}) {
  const { onSuccess, onError } = options;
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const changePassword = async (data: ChangePasswordRequest) => {
    try {
      setIsLoading(true);
      setError(null);

      // 비밀번호 확인 검증
      if (data.newPassword !== data.newPasswordConfirm) {
        const errorMessage = '새 비밀번호가 일치하지 않습니다.';
        setError(errorMessage);
        onError?.(errorMessage);
        return;
      }

      await userService.changePassword(data);
      onSuccess?.();
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : '비밀번호 변경에 실패했습니다.';
      setError(errorMessage);
      onError?.(errorMessage);
      console.error('비밀번호 변경 오류:', err);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { changePassword, isLoading, error };
}
