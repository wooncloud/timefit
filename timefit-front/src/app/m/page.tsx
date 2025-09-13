export default function MobileHomePage() {
  return (
    <div className="px-4 py-8">
      <div className="text-center">
        <h1 className="mb-4 text-3xl font-bold text-primary">TimeFit</h1>
        <p className="mb-8 text-muted-foreground">예약 관리를 더 쉽게</p>
        <div className="space-y-3">
          <a
            href="/mobile/signin"
            className="block w-full rounded-lg bg-primary px-6 py-3 text-base font-medium text-primary-foreground transition-colors hover:bg-primary/90"
          >
            로그인
          </a>
          <a
            href="/mobile/signup"
            className="block w-full rounded-lg border border-primary bg-transparent px-6 py-3 text-base font-medium text-primary transition-colors hover:bg-primary hover:text-primary-foreground"
          >
            회원가입
          </a>
        </div>
      </div>
    </div>
  );
}
