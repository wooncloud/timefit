'use client';

import { useEffect, useState } from 'react';

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

interface OperatingHoursTimeEditDialogProps {
  open: boolean;
  dayLabel: string;       // 예: "월", "화"
  startTime: string;      // 현재 시작 시간 HH:mm
  endTime: string;        // 현재 종료 시간 HH:mm
  isSaving?: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (startTime: string, endTime: string) => void;
}

export function OperatingHoursTimeEditDialog({
                                               open,
                                               dayLabel,
                                               startTime: initialStart,
                                               endTime: initialEnd,
                                               isSaving = false,
                                               onOpenChange,
                                               onSubmit,
                                             }: OperatingHoursTimeEditDialogProps) {
  const [startTime, setStartTime] = useState(initialStart);
  const [endTime, setEndTime] = useState(initialEnd);
  const [error, setError] = useState<string | null>(null);

  // 다이얼로그 열릴 때마다 현재 값으로 초기화
  useEffect(() => {
    if (open) {
      setStartTime(initialStart);
      setEndTime(initialEnd);
      setError(null);
    }
  }, [open, initialStart, initialEnd]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!startTime || !endTime) {
      setError('시작 시간과 종료 시간을 모두 입력해주세요.');
      return;
    }
    if (startTime >= endTime) {
      setError('종료 시간은 시작 시간보다 늦어야 합니다.');
      return;
    }

    onSubmit(startTime, endTime);
  };

  return (
    <Dialog open={open} onOpenChange={open => !isSaving && onOpenChange(open)}>
      <DialogContent className="sm:max-w-[360px]">
        <DialogHeader>
          <DialogTitle>{dayLabel}요일 영업 시간 수정</DialogTitle>
          <DialogDescription>
            영업 시작/종료 시간을 설정합니다.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit}>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="open-time">시작 시간</Label>
              <Input
                id="open-time"
                type="time"
                value={startTime}
                onChange={e => setStartTime(e.target.value)}
                disabled={isSaving}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="close-time">종료 시간</Label>
              <Input
                id="close-time"
                type="time"
                value={endTime}
                onChange={e => setEndTime(e.target.value)}
                disabled={isSaving}
              />
            </div>
            {error && (
              <p className="text-sm text-destructive">{error}</p>
            )}
          </div>

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={isSaving}
            >
              취소
            </Button>
            <Button type="submit" disabled={isSaving}>
              {isSaving ? '저장 중...' : '저장'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}