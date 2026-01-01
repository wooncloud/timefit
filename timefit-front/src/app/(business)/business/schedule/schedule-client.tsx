'use client';

import { useState } from 'react';
import { useParams } from 'next/navigation';

import type { BookingTimeRange } from '@/types/schedule/operating-hours';
import { WEEKDAYS } from '@/lib/data/schedule/weekdays';
import type { BusinessHours } from '@/lib/data/schedule/weekdays';
import { ScheduleEditorPanel } from '@/components/business/schedule/schedule-editor-panel';
import { WeekdayHoursPanel } from '@/components/business/schedule/weekday-hours-panel';

interface ScheduleClientProps {
  initialBusinessHours: BusinessHours[];
}

export function ScheduleClient({ initialBusinessHours }: ScheduleClientProps) {
  const params = useParams();
  const businessId = params.businessId as string;

  const [businessHours, setBusinessHours] =
    useState<BusinessHours[]>(initialBusinessHours);
  const [selectedDayId, setSelectedDayId] = useState<string>('mon');
  const [bookingSlotsMap, setBookingSlotsMap] = useState<
    Record<string, BookingTimeRange[]>
  >({});

  const selectedDay = businessHours.find(d => d.id === selectedDayId);
  const selectedWeekday = WEEKDAYS.find(w => w.id === selectedDayId);
  const selectedSlots = bookingSlotsMap[selectedDayId] || [];

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

  const handleSlotsChange = (slots: BookingTimeRange[]) => {
    setBookingSlotsMap(prev => ({
      ...prev,
      [selectedDayId]: slots,
    }));
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
        businessId={businessId}
        selectedDay={selectedWeekday?.fullLabel}
        selectedDayId={selectedDayId}
        startTime={selectedDay?.startTime || '09:00'}
        endTime={selectedDay?.endTime || '18:00'}
        bookingSlots={selectedSlots}
        allBusinessHours={businessHours}
        allBookingSlotsMap={bookingSlotsMap}
        onSlotsChange={handleSlotsChange}
      />
    </div>
  );
}
