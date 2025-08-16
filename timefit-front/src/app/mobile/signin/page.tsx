import { OAuthProviders } from '@/components/auth/oauth-providers';
import { EmailSigninForm } from '@/components/auth/email-signin-form';

export default function MobileSigninPage() {
  return (
    <div className="px-4 py-8">
      <div className="max-w-md mx-auto">
        <h1 className="text-2xl font-bold text-center mb-8">
          로그인
        </h1>
        
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
          
          <div className="text-center text-sm text-muted-foreground">
            계정이 없으신가요?{' '}
            <a href="/mobile/signup" className="text-primary hover:underline">
              회원가입
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}