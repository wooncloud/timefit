import {
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

export function TeamTableHeader() {
  return (
    <TableHeader>
      <TableRow>
        <TableHead>이름</TableHead>
        <TableHead>이메일</TableHead>
        <TableHead>권한</TableHead>
        <TableHead>가입일</TableHead>
        <TableHead>상태</TableHead>
        <TableHead className="text-right">액션</TableHead>
      </TableRow>
    </TableHeader>
  );
}

