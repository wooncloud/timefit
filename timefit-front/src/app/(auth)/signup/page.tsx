'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Timer, User, Mail, Phone, Eye, EyeOff } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';

export default function SignupPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: ''
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: 회원가입 로직 구현
    console.log('회원가입 시도:', formData);
  };

  return (
    <div className="flex min-h-screen flex-col items-center px-6 py-12">
      {/* 로고 영역 */}
      <div className="mb-8 flex flex-col items-center">
        <div className="mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-[#e8f7f8]">
          <Timer className="h-10 w-10 text-[#3ec0c7]" />
        </div>
        <h1 className="text-2xl font-bold text-gray-900">TimeFit</h1>
        <p className="mt-1 text-sm text-gray-500">계정을 생성하세요</p>
      </div>

      {/* 회원가입 폼 */}
      <form onSubmit={handleSubmit} className="w-full max-w-sm space-y-5">
        {/* 이름 입력 */}
        <div className="space-y-2">
          <Label htmlFor="name" className="text-sm font-medium text-gray-700">
            이름
          </Label>
          <div className="relative">
            <Input
              id="name"
              name="name"
              type="text"
              placeholder="이름을 입력하세요"
              value={formData.name}
              onChange={handleChange}
              className="h-12 rounded-xl border-gray-200 pr-10"
            />
            <User className="absolute right-3 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" />
          </div>
        </div>

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
              onChange={handleChange}
              className="h-12 rounded-xl border-gray-200 pr-10"
            />
            <Mail className="absolute right-3 top-1/2 h-5 w-5 -translate-y-1/2 text-[#3ec0c7]" />
          </div>
        </div>

        {/* 전화번호 입력 */}
        <div className="space-y-2">
          <Label htmlFor="phone" className="text-sm font-medium text-gray-700">
            전화번호
          </Label>
          <div className="relative">
            <Input
              id="phone"
              name="phone"
              type="tel"
              placeholder="010-0000-0000"
              value={formData.phone}
              onChange={handleChange}
              className="h-12 rounded-xl border-gray-200 pr-10"
            />
            <Phone className="absolute right-3 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" />
          </div>
        </div>

        {/* 비밀번호 입력 */}
        <div className="space-y-2">
          <Label htmlFor="password" className="text-sm font-medium text-gray-700">
            비밀번호
          </Label>
          <div className="relative">
            <Input
              id="password"
              name="password"
              type={showPassword ? 'text' : 'password'}
              placeholder="비밀번호를 입력하세요"
              value={formData.password}
              onChange={handleChange}
              className="h-12 rounded-xl border-gray-200 pr-10"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
            >
              {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
            </button>
          </div>
        </div>

        {/* 비밀번호 확인 입력 */}
        <div className="space-y-2">
          <Label htmlFor="confirmPassword" className="text-sm font-medium text-gray-700">
            비밀번호 확인
          </Label>
          <div className="relative">
            <Input
              id="confirmPassword"
              name="confirmPassword"
              type={showConfirmPassword ? 'text' : 'password'}
              placeholder="비밀번호를 다시 입력하세요"
              value={formData.confirmPassword}
              onChange={handleChange}
              className="h-12 rounded-xl border-gray-200 pr-10"
            />
            <button
              type="button"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
            >
              {showConfirmPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
            </button>
          </div>
        </div>

        {/* 회원가입 버튼 */}
        <Button
          type="submit"
          className="h-12 w-full rounded-xl bg-[#3ec0c7] text-base font-semibold text-white hover:bg-[#35adb3]"
        >
          회원가입
        </Button>

        {/* 로그인 링크 */}
        <p className="text-center text-sm text-gray-500">
          이미 계정이 있으신가요?{' '}
          <Link href="/signin" className="font-semibold text-[#3ec0c7] hover:underline">
            로그인
          </Link>
        </p>
      </form>
    </div>
  );
}
