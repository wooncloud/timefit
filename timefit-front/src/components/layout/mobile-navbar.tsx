'use client';

import Link from 'next/link';
import { useState } from 'react';
import { Menu, X } from 'lucide-react';

export function MobileNavbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-50 bg-background border-b">
      <div className="px-4">
        <div className="flex justify-between items-center h-14">
          <Link href="/mobile" className="text-lg font-bold text-primary">
            TimeFit
          </Link>
          
          <button
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className="text-foreground hover:text-primary"
          >
            {isMenuOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>

        {/* Mobile Navigation Menu */}
        {isMenuOpen && (
          <div className="pb-3 space-y-1 bg-background border-t">
            <Link href="#" className="block px-3 py-2 text-foreground hover:text-primary">
              홈
            </Link>
            <Link href="#" className="block px-3 py-2 text-foreground hover:text-primary">
              기능
            </Link>
            <Link href="#" className="block px-3 py-2 text-foreground hover:text-primary">
              서비스
            </Link>
            <Link href="#" className="block px-3 py-2 text-foreground hover:text-primary">
              문의
            </Link>
            <div className="border-t pt-3 space-y-2">
              <Link 
                href="/mobile/signin"
                className="block px-3 py-2 text-primary-foreground bg-primary rounded-md text-center"
              >
                로그인
              </Link>
              <Link 
                href="/mobile/signup"
                className="block px-3 py-2 text-primary border border-primary rounded-md text-center"
              >
                회원가입
              </Link>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
}