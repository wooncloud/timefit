export default function MobileHomePage() {
  return (
    <div className="px-4 py-8">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-primary mb-4">
          TimeFit
        </h1>
        <p className="text-muted-foreground mb-8">
          예약 관리를 더 쉽게
        </p>
        <div className="space-y-3">
          <a 
            href="/mobile/signin" 
            className="block w-full px-6 py-3 text-base font-medium text-primary-foreground bg-primary rounded-lg hover:bg-primary/90 transition-colors"
          >
            로그인
          </a>
          <a 
            href="/mobile/signup" 
            className="block w-full px-6 py-3 text-base font-medium text-primary bg-transparent border border-primary rounded-lg hover:bg-primary hover:text-primary-foreground transition-colors"
          >
            회원가입
          </a>
        </div>
      </div>
    </div>
  );
}