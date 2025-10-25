import { TableHead, TableHeader, TableRow } from '@/components/ui/table';

export function CustomerTableHeader() {
  return (
    <TableHeader>
      <TableRow>
        <TableHead>고객명</TableHead>
        <TableHead>연락처</TableHead>
        <TableHead className="text-center">총 방문 횟수</TableHead>
        <TableHead>최근 방문일</TableHead>
        <TableHead>첫 방문일</TableHead>
        <TableHead>메모</TableHead>
        <TableHead className="w-[80px]">작업</TableHead>
      </TableRow>
    </TableHeader>
  );
}
