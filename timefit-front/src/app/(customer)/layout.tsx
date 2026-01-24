import { ReactNode } from 'react';

import { CustomerBottomNav } from '@/components/customer/layout/customer-bottom-nav';
import { CustomerHeader } from '@/components/customer/layout/customer-header';

interface CustomerLayoutProps {
  children: ReactNode;
}

export default function CustomerLayout({ children }: CustomerLayoutProps) {
  return (
    <div className="flex min-h-screen justify-center bg-gray-50">
      {/* 모바일 컨테이너 */}
      <div className="relative flex min-h-screen w-full max-w-md flex-col bg-white shadow-xl">
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
