'use client';

import { useEffect, useState } from 'react';
import { toast } from 'sonner';

import type { BookingTimeRange } from '@/types/schedule/operating-hours';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

interface BookingSlotEditDialogProps {
  slot: BookingTimeRange | null;
  existingSlots?: BookingTimeRange[];
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (startTime: string, endTime: string) => void;
}

function hasOverlap(
  startTime: string,
  endTime: string,
  existingSlots: BookingTimeRange[],
  editingSlotId?: string
): boolean {
  return existingSlots.some(slot => {
    if (slot.id === editingSlotId) return false;
    return startTime < slot.endTime && endTime > slot.startTime;
  });
}

export function BookingSlotEditDialog({
  slot,
  existingSlots = [],
  open,
  onOpenChange,
  onSubmit,
}: BookingSlotEditDialogProps) {
  const [startTime, setStartTime] = useState('09:00');
  const [endTime, setEndTime] = useState('18:00');

  const isEdit = !!slot;

  useEffect(() => {
    if (slot) {
      setStartTime(slot.startTime);
      setEndTime(slot.endTime);
    } else {
      setStartTime('09:00');
      setEndTime('18:00');
    }
  }, [slot, open]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!startTime || !endTime) return;

    if (startTime >= endTime) {
      toast.error('종료 시간은 시작 시간 이후여야 합니다.');
      return;
    }

    if (hasOverlap(startTime, endTime, existingSlots, slot?.id)) {
      toast.error('다른 시간대와 겹칩니다. 시간을 조정해주세요.');
      return;
    }

    onSubmit(startTime, endTime);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>
            {isEdit ? '예약 가능 시간대 수정' : '예약 가능 시간대 추가'}
          </DialogTitle>
          <DialogDescription>
            {isEdit
              ? '예약 가능 시간대를 수정합니다.'
              : '새로운 예약 가능 시간대를 추가합니다.'}
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="start-time">시작 시간</Label>
              <Input
                id="start-time"
                type="time"
                value={startTime}
                onChange={e => setStartTime(e.target.value)}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="end-time">종료 시간</Label>
              <Input
                id="end-time"
                type="time"
                value={endTime}
                onChange={e => setEndTime(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
            >
              취소
            </Button>
            <Button type="submit">{isEdit ? '저장' : '추가'}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}