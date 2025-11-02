import { TableCell, TableRow } from '@/components/ui/table';

export function CustomerTableEmpty() {
  return (
    <TableRow>
      <TableCell colSpan={7} className="text-center text-muted-foreground">
        검색 결과가 없습니다.
      </TableCell>
    </TableRow>
  );
}
