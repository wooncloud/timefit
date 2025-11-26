'use client';

import { useState } from 'react';

import { WEEKDAYS } from '@/lib/data/schedule/weekdays';
import type { BusinessHours } from '@/lib/data/schedule/weekdays';
import { mockBusinessHours } from '@/lib/mock/business-hours';
import { ScheduleEditorPanel } from '@/components/business/schedule/schedule-editor-panel';
import { WeekdayHoursPanel } from '@/components/business/schedule/weekday-hours-panel';

export default function Page() {
  const [businessHours, setBusinessHours] =
    useState<BusinessHours[]>(mockBusinessHours);
  const [selectedDayId, setSelectedDayId] = useState<string>('mon');

  const selectedDay = businessHours.find(d => d.id === selectedDayId);
  const selectedWeekday = WEEKDAYS.find(w => w.id === selectedDayId);

  const handleToggle = (id: string, enabled: boolean) => {
    setBusinessHours(prev =>
      prev.map(day => (day.id === id ? { ...day, isEnabled: enabled } : day))
    );
  };

  const handleTimeChange = (
    id: string,
    type: 'start' | 'end',
    value: string
  ) => {
    setBusinessHours(prev =>
      prev.map(day =>
        day.id === id
          ? {
              ...day,
              [type === 'start' ? 'startTime' : 'endTime']: value,
            }
          : day
      )
    );
  };

  const handleSelect = (id: string) => {
    setSelectedDayId(id);
  };

  return (
    <div className="grid grid-cols-1 gap-6 lg:grid-cols-[500px_1fr]">
      <WeekdayHoursPanel
        businessHours={businessHours}
        selectedDayId={selectedDayId}
        onToggle={handleToggle}
        onTimeChange={handleTimeChange}
        onSelect={handleSelect}
      />

      <ScheduleEditorPanel
        selectedDay={selectedWeekday?.fullLabel}
        startTime={selectedDay?.startTime || '09:00'}
        endTime={selectedDay?.endTime || '18:00'}
      />
    </div>
  );
}
