import { LoginForm } from "@/components/auth/login-form";

export default function BusinessSigninPage() {
  return (
    <div className="bg-background flex min-h-svh flex-col items-center justify-center gap-6 p-6 md:p-10">
      <div className="w-full max-w-sm">
        <LoginForm />
      </div>
    </div>

    // <div className="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    //   <div className="max-w-md w-full space-y-8">
    //     <div>
    //       <h2 className="mt-6 text-center text-3xl font-bold text-foreground">
    //         사업자 로그인
    //       </h2>
    //       <p className="mt-2 text-center text-sm text-muted-foreground">
    //         사업자 계정으로 로그인하여 예약 관리를 시작하세요
    //       </p>
    //     </div>

    //     <div className="bg-secondary/10 border border-secondary/20 rounded-lg p-4">
    //       <div className="flex items-center space-x-2">
    //         <div className="w-2 h-2 bg-secondary rounded-full"></div>
    //         <span className="text-sm font-medium text-secondary">사업자 전용 로그인</span>
    //       </div>
    //       <p className="mt-2 text-sm text-muted-foreground">
    //         비즈니스 관리 도구 및 예약 시스템에 접근할 수 있습니다.
    //       </p>
    //     </div>

    //     <div className="space-y-6">
    //       <OAuthProviders />

    //       <div className="relative">
    //         <div className="absolute inset-0 flex items-center">
    //           <div className="w-full border-t border-border" />
    //         </div>
    //         <div className="relative flex justify-center text-sm">
    //           <span className="bg-background px-2 text-muted-foreground">
    //             또는
    //           </span>
    //         </div>
    //       </div>

    //       <EmailSigninForm />

    //       <div className="text-center">
    //         <div className="text-sm text-muted-foreground">
    //           사업자 계정이 없으신가요?{' '}
    //           <a href="/pc/business/signup" className="text-secondary hover:underline font-medium">
    //             사업자 회원가입
    //           </a>
    //         </div>
    //         <div className="mt-2 text-sm text-muted-foreground">
    //           일반 사용자이신가요?{' '}
    //           <a href="/pc/signin" className="text-primary hover:underline font-medium">
    //             일반 로그인
    //           </a>
    //         </div>
    //       </div>
    //     </div>
    //   </div>
    // </div>
  );
}
