'use client';

import { useState } from 'react';
import { ClipboardList, Loader2, Trash2 } from 'lucide-react';

import type { BookingSlot } from '@/types/booking-slot/booking-slot';
import { useBookingSlots } from '@/hooks/booking-slot/use-booking-slots';
import { useDeleteBookingSlot } from '@/hooks/booking-slot/mutations/use-delete-booking-slot';
import { useDeletePastSlots } from '@/hooks/booking-slot/mutations/use-delete-past-slots';
import { useToggleBookingSlot } from '@/hooks/booking-slot/mutations/use-toggle-booking-slot';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import {
  Empty,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';

interface SlotManagementPanelProps {
  businessId: string;
}

export function SlotManagementPanel({ businessId }: SlotManagementPanelProps) {
  const today = new Date().toLocaleDateString('sv-SE');
  const [selectedDate, setSelectedDate] = useState(today);
  const [deleteTarget, setDeleteTarget] = useState<BookingSlot | null>(null);

  const { slots, loading, refetch } = useBookingSlots(businessId, selectedDate);
  const { deleteBookingSlot, loading: deleteLoading } =
    useDeleteBookingSlot(businessId);
  const { toggleSlot, loading: toggleLoading } =
    useToggleBookingSlot(businessId);
  const { deletePastSlots, loading: deletePastLoading } =
    useDeletePastSlots(businessId);

  const handleToggle = async (slot: BookingSlot) => {
    const result = await toggleSlot(slot.slotId, !slot.isAvailable);
    if (result) refetch();
  };

  const handleDeleteConfirm = async () => {
    if (!deleteTarget) return;
    const success = await deleteBookingSlot(deleteTarget.slotId);
    if (success) {
      setDeleteTarget(null);
      refetch();
    }
  };

  const handleDeletePast = async () => {
    const result = await deletePastSlots();
    if (result !== null) refetch();
  };

  const isPast = selectedDate < today;

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle>예약 슬롯 관리</CardTitle>
          <Button
            variant="outline"
            size="sm"
            onClick={handleDeletePast}
            disabled={deletePastLoading}
          >
            {deletePastLoading ? (
              <Loader2 className="mr-1 h-3 w-3 animate-spin" />
            ) : (
              <Trash2 className="mr-1 h-3 w-3" />
            )}
            과거 슬롯 정리
          </Button>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-2">
          <Label>날짜 선택</Label>
          <Input
            type="date"
            value={selectedDate}
            onChange={e => setSelectedDate(e.target.value)}
          />
        </div>

        {loading ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
          </div>
        ) : slots.length === 0 ? (
          <Empty>
            <EmptyMedia>
              <ClipboardList className="h-12 w-12" />
            </EmptyMedia>
            <EmptyHeader>
              <EmptyTitle>
                {selectedDate
                  ? `${selectedDate}에 슬롯이 없습니다`
                  : '날짜를 선택해주세요'}
              </EmptyTitle>
            </EmptyHeader>
          </Empty>
        ) : (
          <div className="space-y-2">
            <div className="text-sm text-muted-foreground">
              {slots.length}개 슬롯
            </div>
            <div className="max-h-[500px] space-y-2 overflow-y-auto">
              {slots.map(slot => (
                <div
                  key={slot.slotId}
                  className={`flex items-center gap-3 rounded-lg border p-3 ${
                    !slot.isAvailable ? 'bg-muted/50 opacity-60' : ''
                  }`}
                >
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <span className="font-medium">
                        {slot.startTime} ~ {slot.endTime}
                      </span>
                      <Badge variant="outline" className="text-xs">
                        {slot.menuName}
                      </Badge>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <Switch
                      checked={slot.isAvailable}
                      onCheckedChange={() => handleToggle(slot)}
                      disabled={toggleLoading || isPast}
                    />
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-8 w-8 text-red-600 hover:bg-red-50 hover:text-red-700"
                      onClick={() => setDeleteTarget(slot)}
                      disabled={deleteLoading}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        <ConfirmDialog
          open={!!deleteTarget}
          onOpenChange={open => {
            if (!open) setDeleteTarget(null);
          }}
          title="슬롯 삭제"
          description={
            deleteTarget
              ? `${deleteTarget.menuName} ${deleteTarget.startTime}~${deleteTarget.endTime} 슬롯을 삭제하시겠습니까?`
              : ''
          }
          confirmText="삭제"
          cancelText="취소"
          variant="destructive"
          onConfirm={handleDeleteConfirm}
        />
      </CardContent>
    </Card>
  );
}
