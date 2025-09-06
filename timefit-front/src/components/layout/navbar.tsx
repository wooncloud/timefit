import Link from 'next/link';

export function Navbar() {
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
            <Link
              href="/signin"
              className="rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-colors hover:bg-primary/90"
            >
              로그인
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
}
