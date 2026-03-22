'use client';

import { useEffect, useState } from 'react';
import { Coffee, Pencil, Plus, Trash2 } from 'lucide-react';

import type { BookingTimeRange } from '@/types/schedule/operating-hours';
import { useUpdateOperatingHours } from '@/hooks/schedule/mutations/use-update-operating-hours';
import { mapToUpdateOperatingHoursRequest } from '@/lib/data/schedule/map-operating-hours';
import type { BusinessHours } from '@/lib/data/schedule/weekdays';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';

import { BookingSlotEditDialog } from './booking-slot-edit-dialog';

interface ScheduleEditorPanelProps {
  businessId: string;
  selectedDay?: string;
  selectedDayId: string;
  bookingSlots?: BookingTimeRange[];
  allBusinessHours: BusinessHours[];
  allBookingSlotsMap: Record<string, BookingTimeRange[]>;
  onSlotsChange?: (slots: BookingTimeRange[]) => void;
}

export function ScheduleEditorPanel({
  businessId,
  selectedDay,
  selectedDayId,
  bookingSlots = [],
  allBusinessHours,
  allBookingSlotsMap,
  onSlotsChange,
}: ScheduleEditorPanelProps) {
  const [slots, setSlots] = useState<BookingTimeRange[]>(bookingSlots);
  const { updateOperatingHours } = useUpdateOperatingHours(businessId);
  const [editingSlot, setEditingSlot] = useState<BookingTimeRange | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [slotToDelete, setSlotToDelete] = useState<string | null>(null);

  // Sync state when props change (day change)
  useEffect(() => {
    setSlots(bookingSlots);
  }, [bookingSlots]);

  const sortedSlots = [...slots].sort((a, b) =>
    a.startTime.localeCompare(b.startTime)
  );

  const saveChanges = async (newSlots: BookingTimeRange[]) => {
    setSlots(newSlots);
    onSlotsChange?.(newSlots);

    const newBookingSlotsMap = {
      ...allBookingSlotsMap,
      [selectedDayId]: newSlots,
    };

    const request = mapToUpdateOperatingHoursRequest(
      allBusinessHours,
      newBookingSlotsMap
    );

    try {
      await updateOperatingHours(request);
    } catch {
      // Error handled in hook
    }
  };

  const handleAddClick = () => {
    setEditingSlot(null);
    setEditDialogOpen(true);
  };

  const handleEditClick = (slot: BookingTimeRange) => {
    setEditingSlot(slot);
    setEditDialogOpen(true);
  };

  const handleDialogSubmit = (startTime: string, endTime: string) => {
    if (editingSlot) {
      const updatedSlots = slots.map(slot =>
        slot.id === editingSlot.id ? { ...slot, startTime, endTime } : slot
      );
      saveChanges(updatedSlots);
    } else {
      const newSlot: BookingTimeRange = {
        id: `slot-${Date.now()}`,
        startTime,
        endTime,
      };
      saveChanges([...slots, newSlot]);
    }
    setEditDialogOpen(false);
    setEditingSlot(null);
  };

  const handleDeleteClick = (id: string) => {
    setSlotToDelete(id);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = () => {
    if (!slotToDelete) return;
    const updatedSlots = slots.filter(slot => slot.id !== slotToDelete);
    saveChanges(updatedSlots);
    setDeleteDialogOpen(false);
    setSlotToDelete(null);
  };

  return (
    <Card className="flex-1">
      <CardHeader>
        <CardTitle>
          {selectedDay
            ? `${selectedDay} 예약 가능 시간대 편집`
            : '요일 예약 가능 시간대 편집'}
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <Button onClick={handleAddClick} className="w-full">
          <Plus className="mr-2 h-4 w-4" />
          시간대 추가
        </Button>

        <div className="max-h-[500px] space-y-1 overflow-y-auto pr-2">
          {sortedSlots.length === 0 ? (
            <div className="py-12 text-center text-sm text-muted-foreground">
              예약 가능 시간대를 추가해주세요.
              <br />
              시간대를 분리하면 사이 시간이 휴식 시간이 됩니다.
            </div>
          ) : (
            sortedSlots.map((slot, index) => {
              const prevSlot = index > 0 ? sortedSlots[index - 1] : null;
              const hasBreak =
                prevSlot && prevSlot.endTime < slot.startTime;

              return (
                <div key={slot.id}>
                  {hasBreak && (
                    <div className="flex items-center gap-2 py-1.5 text-xs text-muted-foreground">
                      <div className="h-px flex-1 bg-border" />
                      <Coffee className="h-3 w-3" />
                      <span>
                        휴식 {prevSlot.endTime} ~ {slot.startTime}
                      </span>
                      <div className="h-px flex-1 bg-border" />
                    </div>
                  )}
                  <div className="flex items-center gap-3 rounded-lg border p-4 shadow-sm">
                    <div className="grid flex-1 grid-cols-2 gap-4">
                      <div>
                        <div className="mb-1 text-[11px] font-medium uppercase tracking-wider text-muted-foreground">
                          시작 시간
                        </div>
                        <div className="font-semibold">{slot.startTime}</div>
                      </div>
                      <div>
                        <div className="mb-1 text-[11px] font-medium uppercase tracking-wider text-muted-foreground">
                          종료 시간
                        </div>
                        <div className="font-semibold">{slot.endTime}</div>
                      </div>
                    </div>
                    <div className="flex gap-1">
                      <Button
                        variant="ghost"
                        size="icon"
                        aria-label="시간대 수정"
                        onClick={() => handleEditClick(slot)}
                        className="h-9 w-9 text-blue-600 hover:bg-blue-50 hover:text-blue-700"
                      >
                        <Pencil className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        aria-label="시간대 삭제"
                        onClick={() => handleDeleteClick(slot.id!)}
                        className="h-9 w-9 text-red-600 hover:bg-red-50 hover:text-red-700"
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              );
            })
          )}
        </div>

        <BookingSlotEditDialog
          slot={editingSlot}
          existingSlots={slots}
          open={editDialogOpen}
          onOpenChange={open => {
            setEditDialogOpen(open);
            if (!open) setEditingSlot(null);
          }}
          onSubmit={handleDialogSubmit}
        />

        <ConfirmDialog
          open={deleteDialogOpen}
          onOpenChange={setDeleteDialogOpen}
          title="시간대 삭제"
          description="이 예약 가능 시간대를 삭제하시겠습니까? 이 작업은 취소할 수 없습니다."
          confirmText="삭제"
          cancelText="취소"
          variant="destructive"
          onConfirm={handleDeleteConfirm}
        />
      </CardContent>
    </Card>
  );
}
