'use client';

import dayjs from 'dayjs';
import 'dayjs/locale/ko';
import { Eye, MoreVertical, Pencil } from 'lucide-react';

import type { BusinessCustomerItem } from '@/types/business/customer-business';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { TableCell, TableRow } from '@/components/ui/table';

dayjs.locale('ko');

interface CustomerTableRowProps {
  customer: BusinessCustomerItem;
  onDetail: (customerId: string) => void;
  onMemo: (customerId: string, currentMemo: string | null) => void;
}

export function CustomerTableRow({
                                   customer,
                                   onDetail,
                                   onMemo,
                                 }: CustomerTableRowProps) {
  return (
    <TableRow>
      <TableCell className="font-medium">{customer.customerName}</TableCell>
      <TableCell>{customer.customerPhone}</TableCell>
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
            <DropdownMenuItem onClick={() => onDetail(customer.customerId)}>
              <Eye className="mr-2 h-4 w-4" />
              상세보기
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => onMemo(customer.customerId, customer.memo)}>
              <Pencil className="mr-2 h-4 w-4" />
              메모 편집
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </TableCell>
    </TableRow>
  );
}