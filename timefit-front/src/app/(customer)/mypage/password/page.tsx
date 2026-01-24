'use client';

import { useState } from 'react';
import Link from 'next/link';
import { ChevronLeft, Eye, EyeOff, ShieldCheck } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';

export default function PasswordChangePage() {
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [formData, setFormData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: 비밀번호 변경 API 호출
    console.log('비밀번호 변경:', formData);
  };

  const passwordRequirements = [
    '최소 8자 이상',
    '특수문자 포함 (예: !@#$)',
    '이전 비밀번호와 다른 비밀번호'
  ];

  return (
    <div className="flex flex-col bg-white">
      {/* 헤더 */}
      <div className="flex items-center gap-3 border-b px-4 py-3">
        <Link href="/mypage/edit" className="flex h-10 w-10 items-center justify-center">
          <ChevronLeft className="h-6 w-6 text-gray-700" />
        </Link>
        <h1 className="text-lg font-semibold">비밀번호 변경</h1>
      </div>

      <form onSubmit={handleSubmit} className="flex flex-1 flex-col px-4 py-6">
        <div className="flex-1 space-y-5">
          {/* 현재 비밀번호 */}
          <div className="space-y-2">
            <Label className="text-sm font-medium text-gray-700">현재 비밀번호</Label>
            <div className="relative">
              <Input
                name="currentPassword"
                type={showCurrentPassword ? 'text' : 'password'}
                placeholder="현재 비밀번호를 입력하세요"
                value={formData.currentPassword}
                onChange={handleChange}
                className="h-12 rounded-xl border-gray-200 pr-10"
              />
              <button
                type="button"
                onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
              >
                {showCurrentPassword ? (
                  <EyeOff className="h-5 w-5" />
                ) : (
                  <Eye className="h-5 w-5" />
                )}
              </button>
            </div>
          </div>

          {/* 새 비밀번호 */}
          <div className="space-y-2">
            <Label className="text-sm font-medium text-gray-700">새 비밀번호</Label>
            <div className="relative">
              <Input
                name="newPassword"
                type={showNewPassword ? 'text' : 'password'}
                placeholder="새 비밀번호를 입력하세요"
                value={formData.newPassword}
                onChange={handleChange}
                className="h-12 rounded-xl border-gray-200 pr-10"
              />
              <button
                type="button"
                onClick={() => setShowNewPassword(!showNewPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
              >
                {showNewPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
              </button>
            </div>
          </div>

          {/* 새 비밀번호 확인 */}
          <div className="space-y-2">
            <Label className="text-sm font-medium text-gray-700">새 비밀번호 확인</Label>
            <div className="relative">
              <Input
                name="confirmPassword"
                type={showConfirmPassword ? 'text' : 'password'}
                placeholder="새 비밀번호를 다시 입력하세요"
                value={formData.confirmPassword}
                onChange={handleChange}
                className="h-12 rounded-xl border-gray-200 pr-10"
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
              >
                {showConfirmPassword ? (
                  <EyeOff className="h-5 w-5" />
                ) : (
                  <Eye className="h-5 w-5" />
                )}
              </button>
            </div>
          </div>

          {/* 비밀번호 요구사항 */}
          <div className="rounded-xl bg-gray-50 p-4">
            <div className="flex items-center gap-2 text-sm font-medium text-gray-700">
              <ShieldCheck className="h-5 w-5 text-[#3ec0c7]" />
              <span>비밀번호 요구사항</span>
            </div>
            <ul className="mt-3 space-y-2">
              {passwordRequirements.map((requirement, index) => (
                <li key={index} className="flex items-center gap-2 text-sm text-gray-500">
                  <span className="h-1.5 w-1.5 rounded-full bg-gray-400" />
                  {requirement}
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* 하단 버튼 */}
        <div className="mt-8">
          <Button
            type="submit"
            className="h-12 w-full rounded-xl bg-[#3ec0c7] text-base font-semibold text-white hover:bg-[#35adb3]"
          >
            비밀번호 변경
          </Button>
        </div>
      </form>
    </div>
  );
}
