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

interface InviteMemberDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onInvite: (data: InviteMemberData) => void;
}

export interface InviteMemberData {
  email: string;
  invitationMessage?: string;
}

export function InviteMemberDialog({
  open,
  onOpenChange,
  onInvite,
}: InviteMemberDialogProps) {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');

  const handleInvite = () => {
    if (!email.trim()) {
      toast.error('이메일을 입력해주세요.');
      return;
    }

    onInvite({
      email: email.trim(),
      invitationMessage: message.trim() || undefined,
    });

    // 폼 초기화
    setEmail('');
    setMessage('');
    onOpenChange(false);
  };

  const handleCancel = () => {
    // 폼 초기화
    setEmail('');
    setMessage('');
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>팀원 초대하기</DialogTitle>
          <DialogDescription>
            초대할 팀원의 이메일을 입력해주세요. 초대된 팀원은 멤버(직원)
            권한으로 시작됩니다.
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
