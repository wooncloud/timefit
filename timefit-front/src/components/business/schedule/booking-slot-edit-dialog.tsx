'use client';

import { useEffect, useState } from 'react';

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
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (startTime: string, endTime: string) => void;
}

export function BookingSlotEditDialog({
  slot,
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
    onSubmit(startTime, endTime);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>
            {isEdit ? '예약 슬롯 수정' : '예약 슬롯 추가'}
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
