import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { MoreHorizontal } from 'lucide-react';

interface TeamActionsDropdownProps {
  memberId: string;
  onChangeRole?: (memberId: string) => void;
  onChangeStatus?: (memberId: string) => void;
  onDelete?: (memberId: string) => void;
}

export function TeamActionsDropdown({
  memberId,
  onChangeRole,
  onChangeStatus,
  onDelete,
}: TeamActionsDropdownProps) {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem onClick={() => onChangeRole?.(memberId)}>
          권한 변경
        </DropdownMenuItem>
        <DropdownMenuItem onClick={() => onChangeStatus?.(memberId)}>
          상태 변경
        </DropdownMenuItem>
        <DropdownMenuItem
          className="text-destructive"
          onClick={() => onDelete?.(memberId)}
        >
          삭제
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

