'use client';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { CalendarClock } from 'lucide-react';
import Link from 'next/link';
import { ChangeEvent, FormEvent, useState } from 'react';
import { useRouter } from 'next/navigation';
import {
  initialBusinessSignupForm,
  validateBusinessSignupForm,
  formatBusinessNumber,
  formatContactPhone,
} from './business';
import {
  BusinessSignupFormData,
  BusinessSignupFormErrors,
  CreateBusinessHandlerResponse,
  CreateBusinessRequestBody,
} from '@/types/auth/business/createBusiness';

/*
[RESPONSE]
businessId
businessName
businessType
businessNumber
address
contactPhone
description
logoUrl
myRole
totalMembers
createdAt
updatedAt
 */

export default function BusinessSignUpPage() {
  const [formData, setFormData] = useState<BusinessSignupFormData>(
    process.env.NODE_ENV === 'development'
      ? {
          businessName: '타임핏 주식회사',
          businessType: 'BD002',
          businessNumber: '123-45-67890',
          address: '서울시 강남구 테헤란로 123',
          contactPhone: '02-1234-5678',
          description: '헬스/피트니스 사업을 운영하고 있습니다.',
        }
      : initialBusinessSignupForm
  );
  const [errors, setErrors] = useState<BusinessSignupFormErrors>({});
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  const handleInputChange = (
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = event.target;
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

    if (errors[fieldName]) {
      setErrors(prev => ({
        ...prev,
        [fieldName]: undefined,
      }));
    }
  };

  const handleBusinessTypeChange = (value: string) => {
    setFormData(prev => ({
      ...prev,
      businessType: value,
    }));

    if (errors.businessType) {
      setErrors(prev => ({
        ...prev,
        businessType: undefined,
      }));
    }
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

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
        businessType: formData.businessType,
        businessNumber: formData.businessNumber,
        address: formData.address.trim(),
        contactPhone: formData.contactPhone,
        ...(formData.description.trim()
          ? { description: formData.description.trim() }
          : {}),
      };

      const response = await fetch('/api/auth/business', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      });

      const data = (await response.json()) as CreateBusinessHandlerResponse;

      if (data.success) {
        setMessage('사업자 등록이 완료되었습니다. 사업자 페이지로 이동합니다.');
        setFormData(() => ({ ...initialBusinessSignupForm }));
        setErrors({});
        setTimeout(() => {
          router.replace('/business');
        }, 1500);
      } else {
        setMessage(data.message || '사업자 등록에 실패했습니다.');
      }
    } catch (error) {
      console.error('사업자 등록 요청 오류:', error);
      setMessage('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-6 bg-background p-6 md:p-10">
      <div className="w-full max-w-sm">
        <form className={cn('grid gap-6')} onSubmit={handleSubmit}>
          <div className="flex flex-col items-center gap-2">
            <Link
              href="/"
              className="flex flex-col items-center gap-2 font-medium"
            >
              <div className="flex size-8 items-center justify-center rounded-md">
                <CalendarClock className="size-6" />
              </div>
              <span className="sr-only">Timefit</span>
            </Link>
            <h1 className="text-xl font-bold">
              Business Join <Link href="/">Timefit</Link>
            </h1>
          </div>

          {message && (
            <div
              className={cn(
                'rounded-md p-3 text-center text-sm',
                message.includes('완료')
                  ? 'border border-green-200 bg-green-50 text-green-700'
                  : 'border border-red-200 bg-red-50 text-red-700'
              )}
            >
              {message}
            </div>
          )}

          <div className="grid gap-1">
            <Label htmlFor="businessName">회사명</Label>
            <Input
              id="businessName"
              name="businessName"
              type="text"
              value={formData.businessName}
              onChange={handleInputChange}
              placeholder="타임핏 주식회사"
              className={errors.businessName ? 'border-red-500' : ''}
              required
            />
            {errors.businessName && (
              <span className="text-sm text-red-500">
                {errors.businessName}
              </span>
            )}
          </div>
          <div className="grid gap-1">
            <Label htmlFor="businessType">업종</Label>
            <Select
              value={formData.businessType}
              onValueChange={handleBusinessTypeChange}
            >
              <SelectTrigger
                id="businessType"
                className={errors.businessType ? 'border-red-500' : ''}
              >
                <SelectValue placeholder="업종 선택" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="BD001">음식점</SelectItem>
                <SelectItem value="BD002">헬스/피트니스</SelectItem>
                <SelectItem value="BD003">교육</SelectItem>
                <SelectItem value="BD004">서비스</SelectItem>
                <SelectItem value="BD005">기타</SelectItem>
              </SelectContent>
            </Select>
            {errors.businessType && (
              <span className="text-sm text-red-500">
                {errors.businessType}
              </span>
            )}
          </div>
          <div className="grid gap-1">
            <Label htmlFor="businessNumber">사업자 번호</Label>
            <Input
              id="businessNumber"
              name="businessNumber"
              type="text"
              placeholder="123-45-67890"
              value={formData.businessNumber}
              onChange={handleInputChange}
              className={errors.businessNumber ? 'border-red-500' : ''}
              required
            />
            {errors.businessNumber && (
              <span className="text-sm text-red-500">
                {errors.businessNumber}
              </span>
            )}
          </div>
          <div className="grid gap-1">
            <Label htmlFor="address">주소</Label>
            <Input
              id="address"
              name="address"
              type="text"
              placeholder="서울시 강남구 테헤란로00길"
              value={formData.address}
              onChange={handleInputChange}
              className={errors.address ? 'border-red-500' : ''}
              required
            />
            {errors.address && (
              <span className="text-sm text-red-500">{errors.address}</span>
            )}
          </div>
          <div className="grid gap-1">
            <Label htmlFor="contactPhone">회사 전화번호</Label>
            <Input
              id="contactPhone"
              name="contactPhone"
              type="tel"
              placeholder="02-1234-5678"
              value={formData.contactPhone}
              onChange={handleInputChange}
              className={errors.contactPhone ? 'border-red-500' : ''}
              required
            />
            {errors.contactPhone && (
              <span className="text-sm text-red-500">
                {errors.contactPhone}
              </span>
            )}
          </div>
          <div className="grid gap-1">
            <Label htmlFor="description">회사 설명</Label>
            <Textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="회사에 대해 설명해주세요!"
            />
          </div>

          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? '등록 중...' : '사업자 등록하기'}
          </Button>

          <div className="*:[a]:hover:text-primary *:[a]:underline *:[a]:underline-offset-4 text-balance text-center text-xs text-muted-foreground">
            By clicking continue, you agree to our{' '}
            <Link href="/policy/service">Terms of Service</Link> and{' '}
            <Link href="/policy/privacy">Privacy Policy</Link>.
          </div>
        </form>
      </div>
    </div>
  );
}
