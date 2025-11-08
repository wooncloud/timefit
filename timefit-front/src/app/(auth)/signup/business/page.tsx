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
import { useBusinessSignup } from '@/hooks/business/useBusinessSignup';

export default function BusinessSignUpPage() {
  const {
    formData,
    errors,
    isLoading,
    message,
    handleInputChange,
    handleBusinessTypesChange,
    handleSubmit,
  } = useBusinessSignup();

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
            <Label htmlFor="businessTypes">업종</Label>
            <Select
              value={formData.businessTypes[0] || ''}
              onValueChange={(value) => handleBusinessTypesChange([value])}
            >
              <SelectTrigger
                id="businessTypes"
                className={errors.businessTypes ? 'border-red-500' : ''}
              >
                <SelectValue placeholder="업종 선택" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="BD000">음식점업</SelectItem>
                <SelectItem value="BD001">숙박업</SelectItem>
                <SelectItem value="BD002">소매/유통업</SelectItem>
                <SelectItem value="BD003">미용/뷰티업</SelectItem>
                <SelectItem value="BD004">의료업</SelectItem>
                <SelectItem value="BD005">피트니스/스포츠업</SelectItem>
                <SelectItem value="BD006">교육/문화업</SelectItem>
                <SelectItem value="BD007">전문서비스업</SelectItem>
                <SelectItem value="BD008">생활서비스업</SelectItem>
                <SelectItem value="BD009">제조/생산업</SelectItem>
              </SelectContent>
            </Select>
            {errors.businessTypes && (
              <span className="text-sm text-red-500">
                {errors.businessTypes}
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
