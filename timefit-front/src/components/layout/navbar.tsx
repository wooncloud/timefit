import Link from 'next/link';
import { Button } from "@/components/ui/button"
import { hasAccessTokenCookie } from '@/lib/cookie';

export async function Navbar() {
  const isAuthenticated = await hasAccessTokenCookie();

  return (
    <nav className="sticky top-0 z-50 border-b bg-background">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 justify-between">
          <div className="flex items-center">
            <Link href="/" className="text-xl font-bold text-primary">
              Timefit
            </Link>
          </div>

          {/* Desktop Navigation */}
          <div className="hidden items-center space-x-8 lg:flex"></div>

          {/* Desktop Auth Buttons */}
          <div className="hidden items-center space-x-4 md:flex">
            {isAuthenticated ? (
              <Link href="/business">
                <Button>사업자 페이지</Button>
              </Link>
            ) : (
              <>
                <Link href="/signin">
                  <Button>로그인</Button>
                </Link>
                <Link href="/signup">
                  <Button variant="outline">회원가입</Button>
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
