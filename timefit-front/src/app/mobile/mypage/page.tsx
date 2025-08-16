import Link from 'next/link';

export default function MyPage() {
  return (
    <div className="px-4 py-6">
      <h1 className="text-2xl font-bold mb-6">내 정보</h1>
      <div className="space-y-4">
        <div className="bg-card rounded-lg p-4 border">
          <h2 className="font-semibold mb-2">프로필</h2>
          <div className="text-muted-foreground">
            로그인이 필요합니다
          </div>
        </div>
        
        <div className="space-y-2">
          <Link 
            href="/mobile/mypage/edit"
            className="block w-full px-4 py-3 text-left bg-card rounded-lg border hover:bg-accent transition-colors"
          >
            프로필 수정
          </Link>
          <Link 
            href="/mobile/signin"
            className="block w-full px-4 py-3 text-left bg-card rounded-lg border hover:bg-accent transition-colors"
          >
            로그인
          </Link>
        </div>
      </div>
    </div>
  );
}