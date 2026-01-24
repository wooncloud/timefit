import { ReactNode } from 'react';
import { CustomerHeader } from '@/components/customer/customer-header';
import { CustomerBottomNav } from '@/components/customer/customer-bottom-nav';

interface CustomerLayoutProps {
  children: ReactNode;
}

export default function CustomerLayout({ children }: CustomerLayoutProps) {
  return (
    <div className="min-h-screen bg-gray-50 flex justify-center">
      {/* 모바일 컨테이너 */}
      <div className="w-full max-w-md flex flex-col bg-white min-h-screen shadow-xl relative">
        {/* 상단 헤더 */}
        <CustomerHeader />

        {/* 메인 컨텐츠 */}
        <main className="flex-1 pb-20">{children}</main>

        {/* 하단 네비게이션 */}
        <CustomerBottomNav />
      </div>
    </div>
  );
}
