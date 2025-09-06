export default function SearchPage() {
  return (
    <div className="px-4 py-6">
      <h1 className="mb-4 text-2xl font-bold">검색</h1>
      <div className="space-y-4">
        <input
          type="text"
          placeholder="업체명 또는 서비스를 검색하세요"
          className="w-full rounded-lg border border-input px-4 py-3 focus:border-transparent focus:ring-2 focus:ring-ring"
        />
        <div className="py-8 text-center text-muted-foreground">
          원하는 업체를 검색해보세요
        </div>
      </div>
    </div>
  );
}
