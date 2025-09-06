import { OAuthProviders } from '@/components/auth/oauth-providers';
import { EmailSigninForm } from '@/components/auth/email-signin-form';

export default function PCSigninPage() {
  return (
    <div className="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-bold text-foreground">
            로그인
          </h2>
          <p className="mt-2 text-center text-sm text-muted-foreground">
            계정에 로그인하여 TimeFit을 이용하세요
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
                또는
              </span>
            </div>
          </div>
          
          <EmailSigninForm />
          
          <div className="text-center">
            <div className="text-sm text-muted-foreground">
              계정이 없으신가요?{' '}
              <a href="/pc/signup" className="text-primary hover:underline font-medium">
                회원가입
              </a>
            </div>
            <div className="mt-2 text-sm text-muted-foreground">
              사업자이신가요?{' '}
              <a href="/pc/business/signin" className="text-secondary hover:underline font-medium">
                사업자 로그인
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}