'use client';

import { useState, useEffect } from 'react';
import { toast } from 'sonner';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import type { TeamMember } from './team-table-row';

interface ChangeStatusDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  member: TeamMember | null;
  onConfirm: (memberId: string, newStatus: TeamMember['status']) => void;
}

const getStatusLabel = (status: TeamMember['status']) => {
  switch (status) {
    case 'active':
      return '활성';
    case 'invited':
      return '초대중';
    case 'inactive':
      return '비활성';
    default:
      return status;
  }
};

export function ChangeStatusDialog({
  open,
  onOpenChange,
  member,
  onConfirm,
}: ChangeStatusDialogProps) {
  const [selectedStatus, setSelectedStatus] =
    useState<TeamMember['status']>('active');

  // 멤버가 변경되면 현재 상태로 초기화
  useEffect(() => {
    if (member) {
      setSelectedStatus(member.status);
    }
  }, [member]);

  const handleConfirm = () => {
    if (!member) return;

    if (selectedStatus === member.status) {
      toast.error('현재 상태와 동일합니다.');
      return;
    }

    onConfirm(member.id, selectedStatus);
    onOpenChange(false);
  };

  const handleCancel = () => {
    if (member) {
      setSelectedStatus(member.status);
    }
    onOpenChange(false);
  };

  if (!member) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>상태 변경</DialogTitle>
          <DialogDescription>
            {member.name}님의 상태를 변경합니다.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 py-4">
          <div className="grid gap-2">
            <Label>현재 이메일</Label>
            <div className="text-sm text-muted-foreground">{member.email}</div>
          </div>

          <div className="grid gap-2">
            <Label>현재 상태</Label>
            <div className="text-sm font-medium">
              {getStatusLabel(member.status)}
            </div>
          </div>

          <div className="grid gap-2">
            <Label htmlFor="new-status">
              새로운 상태 <span className="text-destructive">*</span>
            </Label>
            <Select
              value={selectedStatus}
              onValueChange={(value) =>
                setSelectedStatus(value as TeamMember['status'])
              }
            >
              <SelectTrigger id="new-status">
                <SelectValue placeholder="상태를 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="active">활성</SelectItem>
                <SelectItem value="inactive">비활성</SelectItem>
              </SelectContent>
            </Select>
            <p className="text-xs text-muted-foreground">
              활성: 정상 사용 | 비활성: 사용 중지
            </p>
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={handleCancel}>
            취소
          </Button>
          <Button onClick={handleConfirm}>변경하기</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
