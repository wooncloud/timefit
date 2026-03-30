'use client';

import { useState } from 'react';

import type { Menu } from '@/types/customer/menu';
import type { BookingTimeRange, OperatingHours } from '@/types/schedule/operating-hours';
import { weekdayIdToDayOfWeek } from '@/types/business/operating-hours';
import { useToggleOperatingHours } from '@/hooks/operating-hours/mutations/use-toggle-operating-hours';
import { useUpdateOperatingHours } from '@/hooks/schedule/mutations/use-update-operating-hours';
import { mapToUpdateOperatingHoursRequest } from '@/lib/data/schedule/map-operating-hours';
import { WEEKDAYS } from '@/lib/data/schedule/weekdays';
import type { BusinessHours } from '@/lib/data/schedule/weekdays';
import { OperatingHoursTimeEditDialog } from '@/components/business/schedule/operating-hours-time-edit-dialog';
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

  // 영업 시간 편집 다이얼로그 상태
  const [timeEditDialog, setTimeEditDialog] = useState<{
    open: boolean;
    dayId: string;
  }>({ open: false, dayId: '' });

  const { toggleOperatingHours } = useToggleOperatingHours(businessId);
  const { updateOperatingHours, loading: isSavingTime } =
    useUpdateOperatingHours(businessId);

  const selectedDay = businessHours.find(d => d.id === selectedDayId);
  const selectedWeekday = WEEKDAYS.find(w => w.id === selectedDayId);
  const selectedSlots = bookingSlotsMap[selectedDayId] || [];

  // 영업/휴무 토글
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

  // 시간 클릭 → 다이얼로그 오픈
  const handleTimeEdit = (id: string) => {
    setTimeEditDialog({ open: true, dayId: id });
  };

  // 다이얼로그 저장 → API 호출
  const handleTimeEditSubmit = async (startTime: string, endTime: string) => {
    const { dayId } = timeEditDialog;
    const prevHours = businessHours;

    const newHours = businessHours.map(day =>
      day.id === dayId ? { ...day, startTime, endTime } : day
    );
    setBusinessHours(newHours);

    const request = mapToUpdateOperatingHoursRequest(newHours, bookingSlotsMap);
    const success = await updateOperatingHours(request);

    if (!success) {
      setBusinessHours(prevHours);
      return;
    }

    setTimeEditDialog({ open: false, dayId: '' });
  };

  const handleSelect = (id: string) => {
    setSelectedDayId(id);
  };

  const handleSlotsChange = (slots: BookingTimeRange[]) => {
    setBookingSlotsMap(prev => ({ ...prev, [selectedDayId]: slots }));
  };

  const editingDay = businessHours.find(d => d.id === timeEditDialog.dayId);
  const editingWeekday = WEEKDAYS.find(w => w.id === timeEditDialog.dayId);

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
            onTimeEdit={handleTimeEdit}
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

        {editingDay && (
          <OperatingHoursTimeEditDialog
            open={timeEditDialog.open}
            dayLabel={editingWeekday?.label ?? ''}
            startTime={editingDay.startTime}
            endTime={editingDay.endTime}
            isSaving={isSavingTime}
            onOpenChange={open => setTimeEditDialog(prev => ({ ...prev, open }))}
            onSubmit={handleTimeEditSubmit}
          />
        )}
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