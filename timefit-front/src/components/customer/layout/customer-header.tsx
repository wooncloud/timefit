'use client';

import Link from 'next/link';
import { Search, Timer } from 'lucide-react';

export function CustomerHeader() {
  return (
    <header className="sticky top-0 z-50 border-b bg-white">
      <div className="flex h-14 items-center justify-between px-4">
        {/* 로고 */}
        <Link href="/" className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-[#e8f7f8]">
            <Timer className="h-5 w-5 text-[#3ec0c7]" />
          </div>
          <span className="text-lg font-bold text-gray-900">TimeFit</span>
        </Link>

        {/* 아이콘 버튼들 */}
        <div className="flex items-center gap-2">
          <Link
            href="/search"
            className="flex h-10 w-10 items-center justify-center rounded-full hover:bg-gray-100"
          >
            <Search className="h-5 w-5 text-gray-600" />
          </Link>
        </div>
      </div>
    </header>
  );
}
