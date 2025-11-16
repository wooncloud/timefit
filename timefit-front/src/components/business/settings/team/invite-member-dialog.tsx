'use client';

import { useState } from 'react';
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
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

interface InviteMemberDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onInvite: (data: InviteMemberData) => void;
}

export interface InviteMemberData {
  email: string;
  role: 'MANAGER' | 'MEMBER';
  message: string;
}

export function InviteMemberDialog({
  open,
  onOpenChange,
  onInvite,
}: InviteMemberDialogProps) {
  const [email, setEmail] = useState('');
  const [role, setRole] = useState<'MANAGER' | 'MEMBER'>('MEMBER');
  const [message, setMessage] = useState('');

  const handleInvite = () => {
    if (!email.trim()) {
      toast.error('이메일을 입력해주세요.');
      return;
    }

    onInvite({
      email: email.trim(),
      role,
      message: message.trim(),
    });

    // 폼 초기화
    setEmail('');
    setRole('MEMBER');
    setMessage('');
    onOpenChange(false);
  };

  const handleCancel = () => {
    // 폼 초기화
    setEmail('');
    setRole('MEMBER');
    setMessage('');
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>팀원 초대하기</DialogTitle>
          <DialogDescription>
            초대할 팀원의 이메일과 역할을 입력해주세요.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 py-4">
          <div className="grid gap-2">
            <Label htmlFor="email">
              이메일 <span className="text-destructive">*</span>
            </Label>
            <Input
              id="email"
              type="email"
              placeholder="example@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoFocus
            />
          </div>

          <div className="grid gap-2">
            <Label htmlFor="role">
              역할 <span className="text-destructive">*</span>
            </Label>
            <Select
              value={role}
              onValueChange={(value) =>
                setRole(value as 'MANAGER' | 'MEMBER')
              }
            >
              <SelectTrigger id="role">
                <SelectValue placeholder="역할을 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="MANAGER">매니저</SelectItem>
                <SelectItem value="MEMBER">멤버(직원)</SelectItem>
              </SelectContent>
            </Select>
            <p className="text-xs text-muted-foreground">
              매니저: 관리 권한 | 멤버(직원): 기본 권한
            </p>
          </div>

          <div className="grid gap-2">
            <Label htmlFor="message">초대 메시지</Label>
            <Textarea
              id="message"
              placeholder="초대 메시지를 입력하세요 (선택사항)"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              className="min-h-[100px] resize-none"
            />
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={handleCancel}>
            취소
          </Button>
          <Button onClick={handleInvite}>초대 보내기</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
