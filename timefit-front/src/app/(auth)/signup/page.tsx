import { OAuthProviders } from '@/components/auth/oauth-providers';

export default function PCSignupPage() {
  return (
    <div className="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-bold text-foreground">
            회원가입
          </h2>
          <p className="mt-2 text-center text-sm text-muted-foreground">
            TimeFit 계정을 만들어 시작하세요
          </p>
        </div>
        
        <div className="space-y-6">
          <OAuthProviders />
          
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-border" />
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="bg-background px-2 text-muted-foreground">
                소셜 로그인으로 간편하게 가입하세요
              </span>
            </div>
          </div>
          
          <div className="text-center">
            <div className="text-sm text-muted-foreground">
              이미 계정이 있으신가요?{' '}
              <a href="/pc/signin" className="text-primary hover:underline font-medium">
                로그인
              </a>
            </div>
            <div className="mt-2 text-sm text-muted-foreground">
              사업자이신가요?{' '}
              <a href="/pc/business/signin" className="text-secondary hover:underline font-medium">
                사업자 로그인
              </a>
            </div>
          </div>
          
          <div className="mt-6 text-xs text-center text-muted-foreground">
            가입 시 TimeFit의{' '}
            <a href="#" className="text-primary hover:underline">이용약관</a>과{' '}
            <a href="#" className="text-primary hover:underline">개인정보처리방침</a>에 
            동의하는 것으로 간주됩니다.
          </div>
        </div>
      </div>
    </div>
  );
}