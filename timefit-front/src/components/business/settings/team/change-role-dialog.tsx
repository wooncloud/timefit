'use client';

import { useState, useEffect } from 'react';
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

interface ChangeRoleDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  member: TeamMember | null;
  onConfirm: (memberId: string, newRole: TeamMember['role']) => void;
}

export function ChangeRoleDialog({
  open,
  onOpenChange,
  member,
  onConfirm,
}: ChangeRoleDialogProps) {
  const [selectedRole, setSelectedRole] = useState<TeamMember['role']>('MEMBER');

  // 멤버가 변경되면 현재 역할로 초기화
  useEffect(() => {
    if (member) {
      setSelectedRole(member.role);
    }
  }, [member]);

  const handleConfirm = () => {
    if (!member) return;

    if (selectedRole === member.role) {
      alert('현재 역할과 동일합니다.');
      return;
    }

    onConfirm(member.id, selectedRole);
    onOpenChange(false);
  };

  const handleCancel = () => {
    if (member) {
      setSelectedRole(member.role);
    }
    onOpenChange(false);
  };

  if (!member) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>권한 변경</DialogTitle>
          <DialogDescription>
            {member.name}님의 권한을 변경합니다.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 py-4">
          <div className="grid gap-2">
            <Label>현재 이메일</Label>
            <div className="text-sm text-muted-foreground">{member.email}</div>
          </div>

          <div className="grid gap-2">
            <Label>현재 권한</Label>
            <div className="text-sm font-medium">
              {member.role === 'OWNER' ? '관리자' : member.role === 'MANAGER' ? '매니저' : '멤버(직원)'}
            </div>
          </div>

          <div className="grid gap-2">
            <Label htmlFor="new-role">
              새로운 권한 <span className="text-destructive">*</span>
            </Label>
            <Select
              value={selectedRole}
              onValueChange={(value) =>
                setSelectedRole(value as TeamMember['role'])
              }
            >
              <SelectTrigger id="new-role">
                <SelectValue placeholder="권한을 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="OWNER">관리자</SelectItem>
                <SelectItem value="MANAGER">매니저</SelectItem>
                <SelectItem value="MEMBER">멤버(직원)</SelectItem>
              </SelectContent>
            </Select>
            <p className="text-xs text-muted-foreground">
              관리자: 모든 권한 | 매니저: 관리 권한 | 멤버(직원): 기본 권한
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
