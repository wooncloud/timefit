'use client';

import { MoreHorizontal } from 'lucide-react';

import type { ReservationStatus } from '@/types/business/reservation';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

interface ReservationActionsDropdownProps {
  reservationId: string;
  status: ReservationStatus;
  onApprove: (id: string) => void;
  onReject: (id: string) => void;
  onComplete: (id: string) => void;
  onNoShow: (id: string) => void;
}

export function ReservationActionsDropdown({
                                             reservationId,
                                             status,
                                             onApprove,
                                             onReject,
                                             onComplete,
                                             onNoShow,
                                           }: ReservationActionsDropdownProps) {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">

        {/* PENDING: 승인 · 거절 */}
        {status === 'PENDING' && (
          <>
            <DropdownMenuItem onClick={() => onApprove(reservationId)}>
              승인
            </DropdownMenuItem>
            <DropdownMenuItem
              className="text-destructive"
              onClick={() => onReject(reservationId)}
            >
              거절
            </DropdownMenuItem>
            <DropdownMenuSeparator />
          </>
        )}

        {/* CONFIRMED: 완료 처리 · 노쇼 */}
        {status === 'CONFIRMED' && (
          <>
            <DropdownMenuItem onClick={() => onComplete(reservationId)}>
              완료 처리
            </DropdownMenuItem>
            <DropdownMenuItem
              className="text-destructive"
              onClick={() => onNoShow(reservationId)}
            >
              노쇼
            </DropdownMenuItem>
            <DropdownMenuSeparator />
          </>
        )}

        {/* 공통: 상세 보기 */}
        <DropdownMenuItem>상세 보기</DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}