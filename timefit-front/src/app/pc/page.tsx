import Link from 'next/link';

export default function PCHomePage() {
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="text-center">
        <h1 className="text-4xl font-bold text-primary mb-4">
          TimeFit
        </h1>
        <p className="text-lg text-muted-foreground mb-8">
          예약 관리 시스템으로 비즈니스를 더 효율적으로 관리하세요
        </p>
        <div className="space-x-4">
          <Link 
            href="/pc/signin" 
            className="inline-flex items-center justify-center px-6 py-3 text-base font-medium text-primary-foreground bg-primary rounded-lg hover:bg-primary/90 transition-colors"
          >
            로그인
          </Link>
          <Link 
            href="/pc/signup" 
            className="inline-flex items-center justify-center px-6 py-3 text-base font-medium text-primary bg-transparent border border-primary rounded-lg hover:bg-primary hover:text-primary-foreground transition-colors"
          >
            회원가입
          </Link>
          <Link 
            href="/pc/business/signin" 
            className="inline-flex items-center justify-center px-6 py-3 text-base font-medium text-secondary-foreground bg-secondary rounded-lg hover:bg-secondary/90 transition-colors"
          >
            사업자 로그인
          </Link>
        </div>
      </div>
    </div>
  );
}