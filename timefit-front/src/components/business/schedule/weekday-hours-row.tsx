'use client';

import type { BusinessHours } from '@/lib/data/schedule/weekdays';
import { cn } from '@/lib/utils';
import { Input } from '@/components/ui/input';
import { Switch } from '@/components/ui/switch';

interface WeekdayHoursRowProps {
  day: BusinessHours;
  dayLabel: string;
  isSelected?: boolean;
  onToggle?: (id: string, enabled: boolean) => void;
  onTimeEdit?: (id: string) => void; // 시간 클릭 → 다이얼로그 오픈
  onSelect?: (id: string) => void;
}

export function WeekdayHoursRow({
                                  day,
                                  dayLabel,
                                  isSelected,
                                  onToggle,
                                  onTimeEdit,
                                  onSelect,
                                }: WeekdayHoursRowProps) {
  return (
    <div
      className={cn(
        'flex cursor-pointer items-center gap-4 rounded-lg border p-2 transition-colors',
        isSelected
          ? 'border-primary bg-accent'
          : 'border-transparent hover:bg-accent/50'
      )}
      onClick={() => onSelect?.(day.id)}
    >
      <div className="w-8 text-center font-medium">{dayLabel}</div>

      {/* 시작 시간 — 읽기 전용, 클릭 시 편집 다이얼로그 오픈 */}
      <Input
        type="time"
        value={day.startTime}
        readOnly
        onClick={e => {
          e.stopPropagation();
          if (day.isEnabled) onTimeEdit?.(day.id);
        }}
        disabled={!day.isEnabled}
        className={cn(
          'w-32 appearance-none bg-background',
          '[&::-webkit-calendar-picker-indicator]:hidden',
          '[&::-webkit-calendar-picker-indicator]:appearance-none',
          day.isEnabled && 'cursor-pointer hover:border-primary'
        )}
      />

      {/* 종료 시간 — 읽기 전용, 클릭 시 편집 다이얼로그 오픈 */}
      <Input
        type="time"
        value={day.endTime}
        readOnly
        onClick={e => {
          e.stopPropagation();
          if (day.isEnabled) onTimeEdit?.(day.id);
        }}
        disabled={!day.isEnabled}
        className={cn(
          'w-32 appearance-none bg-background',
          '[&::-webkit-calendar-picker-indicator]:hidden',
          '[&::-webkit-calendar-picker-indicator]:appearance-none',
          day.isEnabled && 'cursor-pointer hover:border-primary'
        )}
      />

      <Switch
        checked={day.isEnabled}
        onCheckedChange={checked => {
          onToggle?.(day.id, checked);
        }}
        onClick={e => e.stopPropagation()}
      />
    </div>
  );
}