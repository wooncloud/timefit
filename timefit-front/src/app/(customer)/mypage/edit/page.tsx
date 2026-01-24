'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Camera, ChevronLeft, ChevronRight } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

// 사용자 더미 데이터
const initialUserData = {
  name: '김민지',
  email: 'minji.kim@timefit.com',
  phone: '010-1234-5678',
  lastLogin: '2024년 1월 24일 09:41',
};

export default function ProfileEditPage() {
  const [formData, setFormData] = useState(initialUserData);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: 프로필 업데이트 API 호출
    console.log('프로필 업데이트:', formData);
  };

  return (
    <div className="flex flex-col bg-white">
      {/* 헤더 */}
      <div className="flex items-center gap-3 border-b px-4 py-3">
        <Link
          href="/mypage"
          className="flex h-10 w-10 items-center justify-center"
        >
          <ChevronLeft className="h-6 w-6 text-gray-700" />
        </Link>
        <h1 className="text-lg font-semibold">프로필 편집</h1>
      </div>

      <form onSubmit={handleSubmit} className="flex flex-1 flex-col">
        {/* 프로필 이미지 */}
        <div className="flex justify-center py-8">
          <div className="relative">
            <div className="h-28 w-28 rounded-full bg-gray-200" />
            <button
              type="button"
              className="absolute bottom-0 right-0 flex h-9 w-9 items-center justify-center rounded-full bg-[#3ec0c7] text-white shadow-lg"
            >
              <Camera className="h-5 w-5" />
            </button>
          </div>
        </div>

        {/* 폼 필드 */}
        <div className="flex-1 space-y-5 px-4">
          {/* 이름 */}
          <div className="space-y-2">
            <Label className="text-xs font-semibold uppercase tracking-wider text-gray-500">
              이름
            </Label>
            <Input
              name="name"
              type="text"
              value={formData.name}
              onChange={handleChange}
              className="h-12 rounded-xl border-gray-200"
            />
          </div>

          {/* 이메일 */}
          <div className="space-y-2">
            <Label className="text-xs font-semibold uppercase tracking-wider text-gray-500">
              이메일
            </Label>
            <Input
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              className="h-12 rounded-xl border-gray-200"
            />
          </div>

          {/* 전화번호 */}
          <div className="space-y-2">
            <Label className="text-xs font-semibold uppercase tracking-wider text-gray-500">
              전화번호
            </Label>
            <Input
              name="phone"
              type="tel"
              value={formData.phone}
              onChange={handleChange}
              className="h-12 rounded-xl border-gray-200"
            />
          </div>

          {/* 비밀번호 변경 링크 */}
          <Link
            href="/mypage/password"
            className="flex items-center justify-between rounded-xl border border-gray-200 px-4 py-4"
          >
            <span className="font-medium text-gray-700">비밀번호 변경</span>
            <ChevronRight className="h-5 w-5 text-gray-400" />
          </Link>
        </div>

        {/* 하단 버튼 및 마지막 로그인 */}
        <div className="px-4 py-6">
          <Button
            type="submit"
            className="h-12 w-full rounded-xl bg-[#3ec0c7] text-base font-semibold text-white hover:bg-[#35adb3]"
          >
            변경사항 저장
          </Button>
          <p className="mt-4 text-center text-sm text-gray-400">
            마지막 로그인: {formData.lastLogin}
          </p>
        </div>
      </form>
    </div>
  );
}
