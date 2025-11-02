import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { MoreHorizontal } from 'lucide-react';

interface ReservationActionsDropdownProps {
  reservationId: string;
}

export function ReservationActionsDropdown({
  reservationId,
}: ReservationActionsDropdownProps) {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem>상세 보기</DropdownMenuItem>
        <DropdownMenuItem>수정</DropdownMenuItem>
        <DropdownMenuItem>취소</DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
