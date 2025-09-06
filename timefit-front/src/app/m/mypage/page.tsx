import Link from 'next/link';

export default function MyPage() {
  return (
    <div className="px-4 py-6">
      <h1 className="mb-6 text-2xl font-bold">내 정보</h1>
      <div className="space-y-4">
        <div className="rounded-lg border bg-card p-4">
          <h2 className="mb-2 font-semibold">프로필</h2>
          <div className="text-muted-foreground">로그인이 필요합니다</div>
        </div>

        <div className="space-y-2">
          <Link
            href="/mobile/mypage/edit"
            className="block w-full rounded-lg border bg-card px-4 py-3 text-left transition-colors hover:bg-accent"
          >
            프로필 수정
          </Link>
          <Link
            href="/mobile/signin"
            className="block w-full rounded-lg border bg-card px-4 py-3 text-left transition-colors hover:bg-accent"
          >
            로그인
          </Link>
        </div>
      </div>
    </div>
  );
}
