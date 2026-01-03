'use client';

import { useState } from 'react';

import type { BookingTimeRange } from '@/types/schedule/operating-hours';
import { WEEKDAYS } from '@/lib/data/schedule/weekdays';
import type { BusinessHours } from '@/lib/data/schedule/weekdays';
import { weekdayIdToDayOfWeek } from '@/types/business/operating-hours';
import { useToggleOperatingHours } from '@/hooks/operating-hours/mutations/use-toggle-operating-hours';
import { ScheduleEditorPanel } from '@/components/business/schedule/schedule-editor-panel';
import { WeekdayHoursPanel } from '@/components/business/schedule/weekday-hours-panel';

interface ScheduleClientProps {
  businessId: string;
  initialBusinessHours: BusinessHours[];
  initialBookingSlotsMap: Record<string, BookingTimeRange[]>;
}

export function ScheduleClient({
  businessId,
  initialBusinessHours,
  initialBookingSlotsMap,
}: ScheduleClientProps) {
  const [businessHours, setBusinessHours] =
    useState<BusinessHours[]>(initialBusinessHours);
  const [selectedDayId, setSelectedDayId] = useState<string>('mon');
  const [bookingSlotsMap, setBookingSlotsMap] = useState<
    Record<string, BookingTimeRange[]>
  >(initialBookingSlotsMap);

  const { toggleOperatingHours } = useToggleOperatingHours(businessId);

  const selectedDay = businessHours.find(d => d.id === selectedDayId);
  const selectedWeekday = WEEKDAYS.find(w => w.id === selectedDayId);
  const selectedSlots = bookingSlotsMap[selectedDayId] || [];

  const handleToggle = async (id: string, enabled: boolean) => {
    // 낙관적 UI 업데이트
    setBusinessHours(prev =>
      prev.map(day => (day.id === id ? { ...day, isEnabled: enabled } : day))
    );

    // API 호출
    const weekday = WEEKDAYS.find(w => w.id === id);
    if (!weekday) return;

    const dayOfWeek = weekdayIdToDayOfWeek(weekday.id);
    const success = await toggleOperatingHours(dayOfWeek);

    // 실패 시 롤백
    if (!success) {
      setBusinessHours(prev =>
        prev.map(day =>
          day.id === id ? { ...day, isEnabled: !enabled } : day
        )
      );
    }
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
