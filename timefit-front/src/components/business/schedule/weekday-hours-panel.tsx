'use client';

import { WEEKDAYS } from '@/lib/data/schedule/weekdays';
import type { BusinessHours } from '@/lib/data/schedule/weekdays';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

import { WeekdayHoursRow } from './weekday-hours-row';

interface WeekdayHoursPanelProps {
  businessHours: BusinessHours[];
  selectedDayId?: string;
  onToggle?: (id: string, enabled: boolean) => void;
  onTimeEdit?: (id: string) => void;
  onSelect?: (id: string) => void;
}

export function WeekdayHoursPanel({
                                    businessHours,
                                    selectedDayId,
                                    onToggle,
                                    onTimeEdit,
                                    onSelect,
                                  }: WeekdayHoursPanelProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>영업 시간</CardTitle>
      </CardHeader>
      <CardContent className="space-y-2">
        {businessHours.map(day => {
          const weekday = WEEKDAYS.find(w => w.id === day.id);
          return (
            <WeekdayHoursRow
              key={day.id}
              day={day}
              dayLabel={weekday?.label || ''}
              isSelected={day.id === selectedDayId}
              onToggle={onToggle}
              onTimeEdit={onTimeEdit}
              onSelect={onSelect}
            />
          );
        })}
      </CardContent>
    </Card>
  );
}