'use client';

import { useEffect, useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Textarea } from '@/components/ui/textarea';

interface CustomerMemoDialogProps {
  isOpen: boolean;
  currentMemo: string | null;
  onClose: () => void;
  onSave: (memo: string | null) => void;
}

const MAX_MEMO_LENGTH = 500;

export function CustomerMemoDialog({
                                     isOpen,
                                     currentMemo,
                                     onClose,
                                     onSave,
                                   }: CustomerMemoDialogProps) {
  const [memo, setMemo] = useState(currentMemo ?? '');

  // 다이얼로그 열릴 때마다 현재 메모로 초기화
  useEffect(() => {
    if (isOpen) setMemo(currentMemo ?? '');
  }, [isOpen, currentMemo]);

  const handleSave = () => {
    onSave(memo.trim() || null);
  };

  return (
    <Dialog open={isOpen} onOpenChange={open => !open && onClose()}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>메모 편집</DialogTitle>
        </DialogHeader>

        <div className="space-y-2">
          <Textarea
            value={memo}
            onChange={e => setMemo(e.target.value)}
            placeholder="고객에 대한 메모를 입력하세요 (최대 500자)"
            className="min-h-[120px] resize-none"
            maxLength={MAX_MEMO_LENGTH}
          />
          <p className="text-xs text-muted-foreground text-right">
            {memo.length}/{MAX_MEMO_LENGTH}
          </p>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={onClose}>취소</Button>
          <Button onClick={handleSave}>저장</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}