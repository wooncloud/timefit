'use client';

import Link from 'next/link';
import { useState } from 'react';
import { Menu, X } from 'lucide-react';

export function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-50 bg-background border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link href="/pc" className="text-xl font-bold text-primary">
              TimeFit
            </Link>
          </div>

          {/* Desktop Navigation */}
          <div className="hidden lg:flex items-center space-x-8">
            <Link href="#" className="text-foreground hover:text-primary transition-colors">
              홈
            </Link>
            <Link href="#" className="text-foreground hover:text-primary transition-colors">
              기능
            </Link>
            <Link href="#" className="text-foreground hover:text-primary transition-colors">
              서비스
            </Link>
            <Link href="#" className="text-foreground hover:text-primary transition-colors">
              문의
            </Link>
          </div>

          {/* Desktop Auth Buttons */}
          <div className="hidden md:flex items-center space-x-4">
            <Link 
              href="/pc/signin"
              className="px-4 py-2 text-sm font-medium text-primary-foreground bg-primary rounded-md hover:bg-primary/90 transition-colors"
            >
              로그인
            </Link>
            <Link 
              href="/pc/signup"
              className="px-4 py-2 text-sm font-medium text-primary border border-primary rounded-md hover:bg-primary hover:text-primary-foreground transition-colors"
            >
              회원가입
            </Link>
            <Link 
              href="/pc/business/signin"
              className="px-4 py-2 text-sm font-medium text-secondary-foreground bg-secondary rounded-md hover:bg-secondary/90 transition-colors"
            >
              사업자 로그인
            </Link>
          </div>

          {/* Mobile menu button */}
          <div className="lg:hidden flex items-center">
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="text-foreground hover:text-primary"
            >
              {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="lg:hidden">
            <div className="px-2 pt-2 pb-3 space-y-1 bg-background border-t">
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
                  href="/pc/signin"
                  className="block px-3 py-2 text-primary-foreground bg-primary rounded-md text-center"
                >
                  로그인
                </Link>
                <Link 
                  href="/pc/signup"
                  className="block px-3 py-2 text-primary border border-primary rounded-md text-center"
                >
                  회원가입
                </Link>
                <Link 
                  href="/pc/business/signin"
                  className="block px-3 py-2 text-secondary-foreground bg-secondary rounded-md text-center"
                >
                  사업자 로그인
                </Link>
              </div>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
}