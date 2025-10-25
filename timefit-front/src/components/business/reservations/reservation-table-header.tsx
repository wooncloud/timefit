import {
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

export function ReservationTableHeader() {
  return (
    <TableHeader>
      <TableRow>
        <TableHead>예약번호</TableHead>
        <TableHead>날짜/시간</TableHead>
        <TableHead>고객정보</TableHead>
        <TableHead>서비스</TableHead>
        <TableHead>상태</TableHead>
        <TableHead className="text-right">액션</TableHead>
      </TableRow>
    </TableHeader>
  );
}
