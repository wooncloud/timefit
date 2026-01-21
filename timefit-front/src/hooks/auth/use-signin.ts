'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

import type {
  SigninFormData,
  SigninFormErrors,
  SigninRequestBody,
} from '@/types/auth/signin';
import { authService } from '@/services/auth/auth-service';
import { validateSigninForm } from '@/lib/validators/auth-validators';

interface UseSigninOptions {
  onSuccess?: () => void;
  onError?: (error: string) => void;
}

export function useSignin(options: UseSigninOptions = {}) {
  const { onSuccess, onError } = options;
  const router = useRouter();

  const [formData, setFormData] = useState<SigninFormData>({
    email: process.env.NODE_ENV === 'development' ? 'test@example.com' : '',
    password: process.env.NODE_ENV === 'development' ? 'qwer1234!' : '',
  });

  const [errors, setErrors] = useState<SigninFormErrors>({});
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const fieldName = name as keyof SigninFormData;

    setFormData(prev => ({
      ...prev,
      [fieldName]: value,
    }));

    // 입력시 해당 필드의 에러 메시지 제거
    if (errors[fieldName]) {
      setErrors(prev => ({
        ...prev,
        [fieldName]: undefined,
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // 유효성 검증
    const { isValid, errors: validationErrors } = validateSigninForm(formData);
    setErrors(validationErrors);

    if (!isValid) {
      return;
    }

    setIsLoading(true);
    setMessage('');

    try {
      const requestBody: SigninRequestBody = {
        email: formData.email,
        password: formData.password,
      };

      const data = await authService.signin(requestBody);

      if (data.success) {
        setMessage('로그인에 성공했습니다. 메인 페이지로 이동합니다.');
        setTimeout(() => {
          router.replace('/business');
        }, 1500);
        onSuccess?.();
      } else {
        const errorMessage = data.message || '로그인에 실패했습니다.';
        setMessage(errorMessage);
        onError?.(errorMessage);
      }
    } catch (error) {
      const errorMessage =
        error instanceof Error
          ? error.message
          : '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
      console.error('로그인 요청 오류:', error);
      setMessage(errorMessage);
      onError?.(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    formData,
    setFormData,
    errors,
    isLoading,
    message,
    handleInputChange,
    handleSubmit,
  };
}
