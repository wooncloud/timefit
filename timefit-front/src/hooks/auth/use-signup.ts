'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

import type {
  SignupFormData,
  SignupFormErrors,
  SignupRequestBody,
} from '@/types/auth/signup';
import { authService } from '@/services/auth/auth-service';
import { formatPhoneNumber } from '@/lib/formatters/phone-formatter';
import { validateSignupForm } from '@/lib/validators/auth-validators';

interface UseSignupOptions {
  redirectTo?: string;
  onSuccess?: () => void;
  onError?: (error: string) => void;
}

export function useSignup(options: UseSignupOptions = {}) {
  const { redirectTo = '/business/signup/step2', onSuccess, onError } = options;
  const router = useRouter();

  const [formData, setFormData] = useState<SignupFormData>({
    email: process.env.NODE_ENV === 'development' ? 'test@example.com' : '',
    password: process.env.NODE_ENV === 'development' ? 'qwer1234!' : '',
    confirmPassword: process.env.NODE_ENV === 'development' ? 'qwer1234!' : '',
    name: process.env.NODE_ENV === 'development' ? '홍길동' : '',
    phoneNumber: process.env.NODE_ENV === 'development' ? '010-1234-5678' : '',
  });

  const [errors, setErrors] = useState<SignupFormErrors>({});
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const fieldName = name as keyof SignupFormData;
    const nextValue =
      fieldName === 'phoneNumber' ? formatPhoneNumber(value) : value;

    setFormData(prev => ({
      ...prev,
      [fieldName]: nextValue,
    }));

    // 입력시 해당 필드의 에러 메시지 제거
    if (errors[fieldName]) {
      setErrors(prev => ({
        ...prev,
        [fieldName]: undefined,
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // 유효성 검증
    const { isValid, errors: validationErrors } = validateSignupForm(formData);
    setErrors(validationErrors);

    if (!isValid) {
      return;
    }

    setIsLoading(true);
    setMessage('');

    try {
      const requestBody: SignupRequestBody = {
        email: formData.email,
        password: formData.password,
        name: formData.name,
        phoneNumber: formData.phoneNumber,
      };

      const data = await authService.signup(requestBody);

      if (data.success) {
        router.push(redirectTo);
        onSuccess?.();
      } else {
        const errorMessage = data.message || '회원가입에 실패했습니다.';
        setMessage(errorMessage);
        onError?.(errorMessage);
      }
    } catch (error) {
      const errorMessage =
        error instanceof Error
          ? error.message
          : '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
      console.error('회원가입 오류:', error);
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
