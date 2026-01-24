import { ReactNode } from 'react';

interface CustomerLayoutProps {
  children: ReactNode;
}

export default function CustomerLayout({ children }: CustomerLayoutProps) {
  return (
    <div className="min-h-screen pb-16">
      {/* 메인 컨텐츠 */}
      <main>{children}</main>

      {/* 하단 탭 바 (Bottom Navigation) */}
      <nav className="fixed bottom-0 left-0 right-0 border-t bg-background">
        <div className="flex h-16 items-center justify-around">
          <a href="/" className="flex flex-col items-center gap-1">
            <span>홈</span>
          </a>
          <a href="/search" className="flex flex-col items-center gap-1">
            <span>검색</span>
          </a>
          <a href="/bookings" className="flex flex-col items-center gap-1">
            <span>예약</span>
          </a>
          <a href="/wishlist" className="flex flex-col items-center gap-1">
            <span>찜</span>
          </a>
          <a href="/mypage" className="flex flex-col items-center gap-1">
            <span>마이</span>
          </a>
        </div>
      </nav>
    </div>
  );
}
