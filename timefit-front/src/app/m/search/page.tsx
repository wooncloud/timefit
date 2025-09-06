export default function SearchPage() {
  return (
    <div className="px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">검색</h1>
      <div className="space-y-4">
        <input 
          type="text" 
          placeholder="업체명 또는 서비스를 검색하세요" 
          className="w-full px-4 py-3 border border-input rounded-lg focus:ring-2 focus:ring-ring focus:border-transparent"
        />
        <div className="text-center text-muted-foreground py-8">
          원하는 업체를 검색해보세요
        </div>
      </div>
    </div>
  );
}