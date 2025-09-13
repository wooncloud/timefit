'use client';

import Link from 'next/link';
import { useState } from 'react';
import { Menu, X } from 'lucide-react';

export function MobileNavbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-50 border-b bg-background">
      <div className="px-4">
        <div className="flex h-14 items-center justify-between">
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
          <div className="space-y-1 border-t bg-background pb-3">
            <Link
              href="#"
              className="block px-3 py-2 text-foreground hover:text-primary"
            >
              홈
            </Link>
            <Link
              href="#"
              className="block px-3 py-2 text-foreground hover:text-primary"
            >
              기능
            </Link>
            <Link
              href="#"
              className="block px-3 py-2 text-foreground hover:text-primary"
            >
              서비스
            </Link>
            <Link
              href="#"
              className="block px-3 py-2 text-foreground hover:text-primary"
            >
              문의
            </Link>
            <div className="space-y-2 border-t pt-3">
              <Link
                href="/mobile/signin"
                className="block rounded-md bg-primary px-3 py-2 text-center text-primary-foreground"
              >
                로그인
              </Link>
              <Link
                href="/mobile/signup"
                className="block rounded-md border border-primary px-3 py-2 text-center text-primary"
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
