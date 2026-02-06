'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Camera, ChevronLeft, ChevronRight } from 'lucide-react';

import { useUpdateProfile } from '@/hooks/user/mutations/use-update-profile';
import { useUserProfile } from '@/hooks/user/use-user-profile';
import { formatDateTime } from '@/lib/formatters/date-formatter';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

export default function ProfileEditPage() {
  const router = useRouter();
  const { data: userProfile, isLoading, refetch } = useUserProfile();
  const [formData, setFormData] = useState({
    name: '',
    phoneNumber: '',
    profileImageUrl: '',
  });
  const [message, setMessage] = useState('');

  const { updateProfile, isLoading: isUpdating } = useUpdateProfile({
    onSuccess: () => {
      setMessage('프로필이 성공적으로 수정되었습니다.');
      refetch(); // 프로필 다시 불러오기
      setTimeout(() => {
        router.push('/mypage');
      }, 1500);
    },
    onError: error => {
      setMessage(error);
    },
  });

  // 프로필 데이터 로드 시 폼에 설정
  useEffect(() => {
    if (userProfile) {
      setFormData({
        name: userProfile.name,
        phoneNumber: userProfile.phoneNumber,
        profileImageUrl: userProfile.profileImageUrl || '',
      });
    }
  }, [userProfile]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage('');

    try {
      await updateProfile({
        name: formData.name,
        phoneNumber: formData.phoneNumber,
        profileImageUrl: formData.profileImageUrl || undefined,
      });
    } catch (_error) {
      // 에러는 useUpdateProfile의 onError에서 처리됨
    }
  };

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-white">
        <div className="text-gray-500">로딩 중...</div>
      </div>
    );
  }

  if (!userProfile) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-white">
        <div className="text-gray-500">프로필 정보를 불러올 수 없습니다.</div>
      </div>
    );
  }

  return (
    <div className="flex flex-col bg-white">
      {/* 헤더 */}
      <div className="flex items-center gap-3 px-4 py-3">
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

          {/* 이메일 (읽기 전용) */}
          <div className="space-y-2">
            <Label className="text-xs font-semibold uppercase tracking-wider text-gray-500">
              이메일
            </Label>
            <Input
              name="email"
              type="email"
              value={userProfile.email}
              disabled
              className="h-12 rounded-xl border-gray-200 bg-gray-50"
            />
          </div>

          {/* 전화번호 */}
          <div className="space-y-2">
            <Label className="text-xs font-semibold uppercase tracking-wider text-gray-500">
              전화번호
            </Label>
            <Input
              name="phoneNumber"
              type="tel"
              value={formData.phoneNumber}
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
          {message && (
            <div
              className={`mb-4 rounded-xl p-3 text-center text-sm ${
                message.includes('성공')
                  ? 'bg-green-50 text-green-700'
                  : 'bg-red-50 text-red-700'
              }`}
            >
              {message}
            </div>
          )}
          <Button
            type="submit"
            disabled={isUpdating}
            className="h-12 w-full rounded-xl bg-[#3ec0c7] text-base font-semibold text-white hover:bg-[#35adb3]"
          >
            {isUpdating ? '저장 중...' : '변경사항 저장'}
          </Button>
          <p className="mt-4 text-center text-sm text-gray-400">
            마지막 로그인: {formatDateTime(userProfile.lastLoginAt)}
          </p>
        </div>
      </form>
    </div>
  );
}
