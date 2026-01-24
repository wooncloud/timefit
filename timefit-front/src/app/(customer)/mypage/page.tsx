'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { ChevronRight, HelpCircle, LogOut, Pencil, Settings } from 'lucide-react';
import { toast } from 'sonner';

import { ConfirmDialog } from '@/components/ui/confirm-dialog';

// 사용자 더미 데이터
const userData = {
  name: '김민지',
  email: 'minji.kim@timefit.com',
};

const menuItems = [
  {
    id: 'settings',
    label: '설정',
    icon: Settings,
    href: '/mypage/edit',
  },
  {
    id: 'help',
    label: '고객센터',
    icon: HelpCircle,
    href: '/help',
  },
];

export default function MypagePage() {
  const router = useRouter();
  const [isLogoutDialogOpen, setIsLogoutDialogOpen] = useState(false);
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  const handleLogout = async () => {
    try {
      setIsLoggingOut(true);
      const response = await fetch('/api/auth/logout', {
        method: 'POST',
      });

      const data = await response.json();

      if (data.success) {
        // 로컬 스토리지 클리어
        localStorage.clear();
        sessionStorage.clear();

        toast.success('로그아웃되었습니다.');
        router.push('/');
      } else {
        toast.error('로그아웃에 실패했습니다.');
      }
    } catch (error) {
      console.error('로그아웃 오류:', error);
      toast.error('로그아웃 중 오류가 발생했습니다.');
    } finally {
      setIsLoggingOut(false);
      setIsLogoutDialogOpen(false);
    }
  };

  return (
    <div className="flex flex-col bg-white">
      {/* 헤더 */}
      <div className="flex items-center justify-between px-4 py-4">
        <h1 className="text-xl font-bold text-gray-900">마이페이지</h1>
      </div>

      {/* 프로필 섹션 */}
      <div className="flex flex-col items-center px-4 py-6">
        {/* 프로필 이미지 */}
        <div className="relative">
          <div className="h-24 w-24 rounded-full bg-gray-200" />
          <Link
            href="/mypage/edit"
            className="absolute bottom-0 right-0 flex h-8 w-8 items-center justify-center rounded-full bg-[#3ec0c7] text-white shadow-lg"
          >
            <Pencil className="h-4 w-4" />
          </Link>
        </div>

        {/* 이름 및 프로필 편집 링크 */}
        <h2 className="mt-4 text-xl font-bold text-gray-900">{userData.name}</h2>
        <Link
          href="/mypage/edit"
          className="mt-1 text-sm font-medium text-[#3ec0c7]"
        >
          프로필 편집
        </Link>
      </div>

      {/* 메뉴 섹션 */}
      <div className="px-4 py-4">
        <div className="space-y-1">
          {menuItems.map(item => {
            const Icon = item.icon;
            return (
              <Link
                key={item.id}
                href={item.href}
                className="flex items-center justify-between rounded-xl px-3 py-4 transition-colors hover:bg-gray-50"
              >
                <div className="flex items-center gap-3">
                  <Icon className="h-5 w-5 text-gray-500" />
                  <span className="font-medium text-gray-700">{item.label}</span>
                </div>
                <ChevronRight className="h-5 w-5 text-gray-400" />
              </Link>
            );
          })}
        </div>
      </div>

      {/* 로그아웃 버튼 */}
      <div className="mt-auto px-4 py-6">
        <button
          onClick={() => setIsLogoutDialogOpen(true)}
          className="flex w-full items-center justify-center gap-2 rounded-xl border border-gray-200 py-3 text-sm font-medium text-gray-600 transition-colors hover:bg-gray-50"
        >
          <LogOut className="h-4 w-4" />
          <span>로그아웃</span>
        </button>
      </div>

      {/* 로그아웃 확인 다이얼로그 */}
      <ConfirmDialog
        open={isLogoutDialogOpen}
        onOpenChange={setIsLogoutDialogOpen}
        title="로그아웃"
        description="정말 로그아웃 하시겠습니까?"
        confirmText={isLoggingOut ? '로그아웃 중...' : '로그아웃'}
        cancelText="취소"
        onConfirm={handleLogout}
      />
    </div>
  );
}
