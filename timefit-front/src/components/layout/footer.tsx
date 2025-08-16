import Link from 'next/link';

export function Footer() {
  return (
    <footer className="bg-muted text-muted-foreground py-10 px-6 lg:px-10">
      <div className="max-w-7xl mx-auto">
        <div className="flex flex-col items-center space-y-4 md:space-y-6">
          <nav className="flex flex-col md:flex-row md:space-x-6 space-y-2 md:space-y-0 text-center">
            <Link href="#" className="hover:text-foreground transition-colors text-sm">
              회사 소개
            </Link>
            <Link href="#" className="hover:text-foreground transition-colors text-sm">
              문의하기
            </Link>
            <Link href="#" className="hover:text-foreground transition-colors text-sm">
              채용
            </Link>
            <Link href="#" className="hover:text-foreground transition-colors text-sm">
              보도자료
            </Link>
          </nav>
          <div className="text-center">
            <p className="text-xs sm:text-sm">
              Copyright © 2025 - All rights reserved by Timefit
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
}