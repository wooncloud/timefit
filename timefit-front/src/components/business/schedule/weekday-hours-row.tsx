'use client';

import { Input } from '@/components/ui/input';
import { Switch } from '@/components/ui/switch';
import { cn } from '@/lib/utils';
import type { BusinessHours } from '@/lib/data/schedule/weekdays';

interface WeekdayHoursRowProps {
  day: BusinessHours;
  dayLabel: string;
  isSelected?: boolean;
  onToggle?: (id: string, enabled: boolean) => void;
  onTimeChange?: (id: string, type: 'start' | 'end', value: string) => void;
  onSelect?: (id: string) => void;
}

export function WeekdayHoursRow({
  day,
  dayLabel,
  isSelected,
  onToggle,
  onTimeChange,
  onSelect,
}: WeekdayHoursRowProps) {
  return (
    <div
      className={cn(
        'flex items-center gap-4 rounded-lg border p-2 cursor-pointer transition-colors',
        isSelected
          ? 'bg-accent border-primary'
          : 'hover:bg-accent/50 border-transparent'
      )}
      onClick={() => onSelect?.(day.id)}
    >
      <div className="w-8 text-center font-medium">{dayLabel}</div>
      <Input
        type="time"
        value={day.startTime}
        onChange={(e) => {
          e.stopPropagation();
          onTimeChange?.(day.id, 'start', e.target.value);
        }}
        onClick={(e) => e.stopPropagation()}
        disabled={!day.isEnabled}
        className="w-32 bg-background appearance-none [&::-webkit-calendar-picker-indicator]:hidden [&::-webkit-calendar-picker-indicator]:appearance-none"
      />
      <Input
        type="time"
        value={day.endTime}
        onChange={(e) => {
          e.stopPropagation();
          onTimeChange?.(day.id, 'end', e.target.value);
        }}
        onClick={(e) => e.stopPropagation()}
        disabled={!day.isEnabled}
        className="w-32 bg-background appearance-none [&::-webkit-calendar-picker-indicator]:hidden [&::-webkit-calendar-picker-indicator]:appearance-none"
      />
      <Switch
        checked={day.isEnabled}
        onCheckedChange={(checked) => {
          onToggle?.(day.id, checked);
        }}
        onClick={(e) => e.stopPropagation()}
      />
    </div>
  );
}
