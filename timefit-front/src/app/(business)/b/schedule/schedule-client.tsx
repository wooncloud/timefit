'use client';

import { useState } from 'react';

import type { Menu } from '@/types/customer/menu';
import type { BookingTimeRange, OperatingHours } from '@/types/schedule/operating-hours';
import { weekdayIdToDayOfWeek } from '@/types/business/operating-hours';
import { useToggleOperatingHours } from '@/hooks/operating-hours/mutations/use-toggle-operating-hours';
import { WEEKDAYS } from '@/lib/data/schedule/weekdays';
import type { BusinessHours } from '@/lib/data/schedule/weekdays';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ScheduleEditorPanel } from '@/components/business/schedule/schedule-editor-panel';
import { SlotGenerationPanel } from '@/components/business/schedule/slot-generation-panel';
import { SlotManagementPanel } from '@/components/business/schedule/slot-management-panel';
import { WeekdayHoursPanel } from '@/components/business/schedule/weekday-hours-panel';

interface ScheduleClientProps {
  businessId: string;
  initialBusinessHours: BusinessHours[];
  initialBookingSlotsMap: Record<string, BookingTimeRange[]>;
  operatingHours: OperatingHours;
  reservationMenus: Menu[];
}

export function ScheduleClient({
  businessId,
  initialBusinessHours,
  initialBookingSlotsMap,
  operatingHours,
  reservationMenus,
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
    setBusinessHours(prev =>
      prev.map(day => (day.id === id ? { ...day, isEnabled: enabled } : day))
    );

    const weekday = WEEKDAYS.find(w => w.id === id);
    if (!weekday) return;

    const dayOfWeek = weekdayIdToDayOfWeek(weekday.id);
    const success = await toggleOperatingHours(dayOfWeek);

    if (!success) {
      setBusinessHours(prev =>
        prev.map(day => (day.id === id ? { ...day, isEnabled: !enabled } : day))
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
    <Tabs defaultValue="operating-hours" className="space-y-6">
      <TabsList>
        <TabsTrigger value="operating-hours">영업시간 설정</TabsTrigger>
        <TabsTrigger value="slot-generation">슬롯 생성</TabsTrigger>
        <TabsTrigger value="slot-management">슬롯 관리</TabsTrigger>
      </TabsList>

      <TabsContent value="operating-hours">
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
            bookingSlots={selectedSlots}
            allBusinessHours={businessHours}
            allBookingSlotsMap={bookingSlotsMap}
            onSlotsChange={handleSlotsChange}
          />
        </div>
      </TabsContent>

      <TabsContent value="slot-generation">
        <SlotGenerationPanel
          businessId={businessId}
          menus={reservationMenus}
          operatingHours={operatingHours}
        />
      </TabsContent>

      <TabsContent value="slot-management">
        <SlotManagementPanel businessId={businessId} />
      </TabsContent>
    </Tabs>
  );
}
