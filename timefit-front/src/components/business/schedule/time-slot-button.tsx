'use client';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils';
import {
  timeSlotStatusConfig,
  timeSlotStatuses,
  type TimeSlotStatus,
} from '@/lib/data/schedule/timeSlotStatus';

interface TimeSlotButtonProps {
  time: string;
  status: TimeSlotStatus;
  onStatusChange?: (status: TimeSlotStatus) => void;
}

export function TimeSlotButton({
  time,
  status,
  onStatusChange,
}: TimeSlotButtonProps) {
  const config = timeSlotStatusConfig[status];

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="outline"
          className={cn('h-16 w-full flex-col gap-1', config.className)}
        >
          <span className="text-sm font-medium">{time}</span>
          {config.label && (
            <span className="text-xs text-muted-foreground">
              {config.label}
            </span>
          )}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        {timeSlotStatuses.map(statusType => (
          <DropdownMenuItem
            key={statusType}
            onClick={() => onStatusChange?.(statusType)}
          >
            {timeSlotStatusConfig[statusType].menuLabel}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
