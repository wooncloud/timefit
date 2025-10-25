import dayjs from 'dayjs';
import 'dayjs/locale/ko';
import { TableCell, TableRow } from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { MoreVertical, Eye, Pencil } from 'lucide-react';
import type { Customer } from '@/types/customer/customer';

dayjs.locale('ko');

interface CustomerTableRowProps {
  customer: Customer;
}

export function CustomerTableRow({ customer }: CustomerTableRowProps) {
  return (
    <TableRow>
      <TableCell className="font-medium">{customer.name}</TableCell>
      <TableCell>{customer.phone}</TableCell>
      <TableCell className="text-center">{customer.totalVisits}회</TableCell>
      <TableCell>
        {dayjs(customer.lastVisitDate).format('YYYY.MM.DD (ddd)')}
      </TableCell>
      <TableCell>
        {dayjs(customer.firstVisitDate).format('YYYY.MM.DD (ddd)')}
      </TableCell>
      <TableCell>
        <div className="max-w-[200px] truncate text-muted-foreground">
          {customer.memo || '-'}
        </div>
      </TableCell>
      <TableCell>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon">
              <MoreVertical className="h-4 w-4" />
              <span className="sr-only">작업 메뉴</span>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem>
              <Eye className="mr-2 h-4 w-4" />
              상세보기
            </DropdownMenuItem>
            <DropdownMenuItem>
              <Pencil className="mr-2 h-4 w-4" />
              메모 편집
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </TableCell>
    </TableRow>
  );
}
