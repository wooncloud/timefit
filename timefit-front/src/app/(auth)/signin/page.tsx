'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Eye, EyeOff, Mail, Timer } from 'lucide-react';

import { useSignin } from '@/hooks/auth/use-signin';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

export default function SigninPage() {
  const [showPassword, setShowPassword] = useState(false);

  const {
    formData,
    errors,
    isLoading,
    message,
    handleInputChange,
    handleSubmit,
  } = useSignin({ redirectTo: '/' });

  return (
    <div className="flex min-h-screen flex-col items-center justify-center px-6 py-12">
      {/* 로고 영역 */}
      <div className="mb-8 flex flex-col items-center">
        <div className="mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-[#e8f7f8]">
          <Timer className="h-10 w-10 text-[#3ec0c7]" />
        </div>
        <h1 className="text-2xl font-bold text-gray-900">TimeFit</h1>
        <p className="mt-1 text-sm text-gray-500">다시 오신 것을 환영합니다</p>
      </div>

      {/* 메시지 표시 */}
      {message && (
        <div
          className={cn(
            'mb-4 w-full max-w-sm rounded-md p-3 text-center text-sm',
            message.includes('성공')
              ? 'border border-green-200 bg-green-50 text-green-700'
              : 'border border-red-200 bg-red-50 text-red-700'
          )}
        >
          {message}
        </div>
      )}

      {/* 로그인 폼 */}
      <form onSubmit={handleSubmit} className="w-full max-w-sm space-y-6">
        {/* 이메일 입력 */}
        <div className="space-y-2">
          <Label htmlFor="email" className="text-sm font-medium text-gray-700">
            이메일
          </Label>
          <div className="relative">
            <Input
              id="email"
              name="email"
              type="email"
              placeholder="이메일을 입력하세요"
              value={formData.email}
              onChange={handleInputChange}
              className={cn(
                'h-12 rounded-xl border-gray-200 pr-10',
                errors.email && 'border-red-500'
              )}
              required
            />
            <Mail className="absolute right-3 top-1/2 h-5 w-5 -translate-y-1/2 text-[#3ec0c7]" />
          </div>
          {errors.email && (
            <span className="text-sm text-red-500">{errors.email}</span>
          )}
        </div>

        {/* 비밀번호 입력 */}
        <div className="space-y-2">
          <Label
            htmlFor="password"
            className="text-sm font-medium text-gray-700"
          >
            비밀번호
          </Label>
          <div className="relative">
            <Input
              id="password"
              name="password"
              type={showPassword ? 'text' : 'password'}
              placeholder="비밀번호를 입력하세요"
              value={formData.password}
              onChange={handleInputChange}
              className={cn(
                'h-12 rounded-xl border-gray-200 pr-10',
                errors.password && 'border-red-500'
              )}
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-[#3ec0c7]"
            >
              {showPassword ? (
                <EyeOff className="h-5 w-5" />
              ) : (
                <Eye className="h-5 w-5" />
              )}
            </button>
          </div>
          {errors.password && (
            <span className="text-sm text-red-500">{errors.password}</span>
          )}
          <div className="text-right">
            <Link
              href="/forgot-password"
              className="text-sm text-[#3ec0c7] hover:underline"
            >
              비밀번호를 잊으셨나요?
            </Link>
          </div>
        </div>

        {/* 로그인 버튼 */}
        <Button
          type="submit"
          className="h-12 w-full rounded-xl bg-[#3ec0c7] text-base font-semibold text-white hover:bg-[#35adb3]"
          disabled={isLoading}
        >
          {isLoading ? '로그인 중...' : '로그인'}
        </Button>

        {/* 구분선 */}
        <div className="flex items-center gap-4">
          <div className="h-px flex-1 bg-gray-200" />
          <span className="text-xs text-gray-400">또는 다음으로 계속하기</span>
          <div className="h-px flex-1 bg-gray-200" />
        </div>

        {/* 소셜 로그인 버튼 */}
        <div className="flex justify-center gap-4">
          {/* Google */}
          <button
            type="button"
            className="flex h-12 w-12 items-center justify-center rounded-full border border-gray-200 transition-colors hover:bg-gray-50"
          >
            <svg className="h-6 w-6" viewBox="0 0 24 24">
              <path
                fill="#4285F4"
                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
              />
              <path
                fill="#34A853"
                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
              />
              <path
                fill="#FBBC05"
                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
              />
              <path
                fill="#EA4335"
                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
              />
            </svg>
          </button>

          {/* Kakao */}
          <button
            type="button"
            className="flex h-12 w-12 items-center justify-center rounded-full bg-[#FEE500] transition-opacity hover:opacity-90"
          >
            <svg className="h-6 w-6" viewBox="0 0 24 24">
              <path
                fill="#000000"
                d="M12 3c-5.52 0-10 3.58-10 8 0 2.84 1.88 5.34 4.72 6.76-.15.53-.97 3.43-.99 3.64 0 0-.02.17.09.24.1.06.23.01.23.01.31-.04 3.55-2.32 4.11-2.72.6.09 1.22.13 1.84.13 5.52 0 10-3.58 10-8s-4.48-8-10-8z"
              />
            </svg>
          </button>
        </div>

        {/* 회원가입 링크 */}
        <p className="text-center text-sm text-gray-500">
          계정이 없으신가요?{' '}
          <Link
            href="/signup"
            className="font-semibold text-[#3ec0c7] hover:underline"
          >
            회원가입
          </Link>
        </p>
      </form>
    </div>
  );
}
