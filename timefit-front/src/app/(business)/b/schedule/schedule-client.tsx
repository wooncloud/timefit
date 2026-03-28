'use client';

import { useState } from 'react';

import { weekdayIdToDayOfWeek } from '@/types/business/operating-hours';
import type { BookingTimeRange } from '@/types/schedule/operating-hours';
import { useToggleOperatingHours } from '@/hooks/operating-hours/mutations/use-toggle-operating-hours';
import { useUpdateOperatingHours } from '@/hooks/schedule/mutations/use-update-operating-hours';
import { mapToUpdateOperatingHoursRequest } from '@/lib/data/schedule/map-operating-hours';
import { WEEKDAYS } from '@/lib/data/schedule/weekdays';
import type { BusinessHours } from '@/lib/data/schedule/weekdays';
import { OperatingHoursTimeEditDialog } from '@/components/business/schedule/operating-hours-time-edit-dialog';
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

    // 낙관적 업데이트
    const newHours = businessHours.map(day =>
      day.id === dayId ? { ...day, startTime, endTime } : day
    );
    setBusinessHours(newHours);

    const request = mapToUpdateOperatingHoursRequest(newHours, bookingSlotsMap);
    const success = await updateOperatingHours(request);

    if (!success) {
      // 실패 시 롤백, 다이얼로그는 열린 상태 유지
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

      {/* 영업 시간 편집 다이얼로그 */}
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
    </div>
  );
}
