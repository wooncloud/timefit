'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

import type {
  BusinessSignupFormData,
  BusinessSignupFormErrors,
  CreateBusinessRequestBody,
} from '@/types/auth/business/create-business';
import { businessService } from '@/services/business/business-service.client';
import {
  formatBusinessNumber,
  formatContactPhone,
} from '@/lib/formatters/business-formatter';
import {
  initialBusinessSignupForm,
  validateBusinessSignupForm,
} from '@/lib/validators/business-validators';

interface UseBusinessSignupOptions {
  onSuccess?: () => void;
  onError?: (error: string) => void;
}

export function useBusinessSignup(options: UseBusinessSignupOptions = {}) {
  const { onSuccess, onError } = options;
  const router = useRouter();

  const [formData, setFormData] = useState<BusinessSignupFormData>(
    process.env.NODE_ENV === 'development'
      ? {
          businessName: '타임핏 주식회사',
          businessTypes: ['BD005'],
          businessNumber: '123-45-67890',
          address: '서울시 강남구 테헤란로 123',
          contactPhone: '02-1234-5678',
          description: '피트니스/스포츠 사업을 운영하고 있습니다.',
        }
      : initialBusinessSignupForm
  );

  const [errors, setErrors] = useState<BusinessSignupFormErrors>({});
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    const fieldName = name as keyof BusinessSignupFormData;

    let nextValue = value;
    if (fieldName === 'businessNumber') {
      nextValue = formatBusinessNumber(value);
    } else if (fieldName === 'contactPhone') {
      nextValue = formatContactPhone(value);
    }

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

  const handleBusinessTypesChange = (values: string[]) => {
    setFormData(prev => ({
      ...prev,
      businessTypes: values,
    }));

    if (errors.businessTypes) {
      setErrors(prev => ({
        ...prev,
        businessTypes: undefined,
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // 유효성 검증
    const { isValid, errors: validationErrors } =
      validateBusinessSignupForm(formData);
    setErrors(validationErrors);

    if (!isValid) {
      return;
    }

    setIsLoading(true);
    setMessage('');

    try {
      const requestBody: CreateBusinessRequestBody = {
        businessName: formData.businessName.trim(),
        businessTypes: formData.businessTypes,
        businessNumber: formData.businessNumber,
        address: formData.address.trim(),
        contactPhone: formData.contactPhone,
        ...(formData.description.trim()
          ? { description: formData.description.trim() }
          : {}),
      };

      const data = await businessService.createBusiness(requestBody);

      if (data.success) {
        setMessage('사업자 등록이 완료되었습니다. 로그인 페이지로 이동합니다.');
        setFormData({ ...initialBusinessSignupForm });
        setErrors({});

        // 사업자 등록 성공 후 로그인 페이지로 이동
        setTimeout(() => {
          router.replace('/business/signin');
        }, 1500);
        onSuccess?.();
      } else {
        const errorMessage = data.message || '사업자 등록에 실패했습니다.';
        setMessage(errorMessage);
        onError?.(errorMessage);
      }
    } catch (error) {
      const errorMessage =
        error instanceof Error
          ? error.message
          : '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
      console.error('사업자 등록 요청 오류:', error);
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
    handleBusinessTypesChange,
    handleSubmit,
  };
}
