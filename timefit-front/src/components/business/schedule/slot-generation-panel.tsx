'use client';

import { useMemo, useState } from 'react';
import { CalendarPlus, Loader2 } from 'lucide-react';

import type { Menu } from '@/types/customer/menu';
import type { OperatingHours } from '@/types/schedule/operating-hours';
import { useCreateBookingSlots } from '@/hooks/booking-slot/mutations/use-create-booking-slots';
import { SLOT_INTERVAL_OPTIONS } from '@/lib/constants/slot-options';
import {
  countSlotsForSchedules,
  generateSlotSchedules,
} from '@/lib/data/schedule/generate-slot-schedules';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

interface SlotGenerationPanelProps {
  businessId: string;
  menus: Menu[];
  operatingHours: OperatingHours;
}

export function SlotGenerationPanel({
  businessId,
  menus,
  operatingHours,
}: SlotGenerationPanelProps) {
  const [selectedMenuId, setSelectedMenuId] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [intervalMinutes, setIntervalMinutes] = useState('');

  const { createBookingSlots, loading } = useCreateBookingSlots(businessId);

  const selectedMenu = menus.find(m => m.menuId === selectedMenuId);
  const today = new Date().toLocaleDateString('sv-SE');

  const schedules = useMemo(() => {
    if (!startDate || !endDate) return [];
    return generateSlotSchedules(startDate, endDate, operatingHours);
  }, [startDate, endDate, operatingHours]);

  const estimatedSlotCount = useMemo(() => {
    if (!intervalMinutes || schedules.length === 0) return 0;
    return countSlotsForSchedules(schedules, parseInt(intervalMinutes, 10));
  }, [schedules, intervalMinutes]);

  const canGenerate =
    selectedMenuId &&
    startDate &&
    endDate &&
    intervalMinutes &&
    schedules.length > 0 &&
    !loading;

  const handleGenerate = async () => {
    if (!canGenerate) return;

    const result = await createBookingSlots({
      menuId: selectedMenuId,
      slotIntervalMinutes: parseInt(intervalMinutes, 10),
      schedules,
    });

    if (result) {
      setSelectedMenuId('');
      setStartDate('');
      setEndDate('');
      setIntervalMinutes('');
    }
  };

  if (menus.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>예약 슬롯 생성</CardTitle>
        </CardHeader>
        <CardContent>
          <Empty>
            <EmptyMedia>
              <CalendarPlus className="h-12 w-12" />
            </EmptyMedia>
            <EmptyHeader>
              <EmptyTitle>예약형 서비스가 없습니다</EmptyTitle>
              <EmptyDescription>
                서비스 관리에서 예약형 서비스를 먼저 등록해주세요.
              </EmptyDescription>
            </EmptyHeader>
          </Empty>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>예약 슬롯 생성</CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="grid gap-6 lg:grid-cols-2">
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>서비스 선택</Label>
              <Select value={selectedMenuId} onValueChange={setSelectedMenuId}>
                <SelectTrigger>
                  <SelectValue placeholder="서비스를 선택하세요" />
                </SelectTrigger>
                <SelectContent>
                  {menus.map(menu => (
                    <SelectItem key={menu.menuId} value={menu.menuId}>
                      {menu.serviceName} ({menu.durationMinutes}분)
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-2">
                <Label>시작 날짜</Label>
                <Input
                  type="date"
                  min={today}
                  value={startDate}
                  onChange={e => setStartDate(e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label>종료 날짜</Label>
                <Input
                  type="date"
                  min={startDate || today}
                  value={endDate}
                  onChange={e => setEndDate(e.target.value)}
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label>슬롯 간격</Label>
              <Select value={intervalMinutes} onValueChange={setIntervalMinutes}>
                <SelectTrigger>
                  <SelectValue placeholder="간격 선택" />
                </SelectTrigger>
                <SelectContent>
                  {SLOT_INTERVAL_OPTIONS.map(option => (
                    <SelectItem key={option.value} value={option.value}>
                      {option.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {selectedMenu && (
                <p className="text-xs text-muted-foreground">
                  서비스 소요시간: {selectedMenu.durationMinutes}분
                </p>
              )}
            </div>
          </div>

          <div className="space-y-4">
            <Label>미리보기</Label>
            <div className="rounded-lg border bg-muted/30 p-4">
              {schedules.length === 0 ? (
                <p className="text-center text-sm text-muted-foreground">
                  날짜를 선택하면 미리보기가 표시됩니다.
                </p>
              ) : (
                <div className="space-y-2">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">생성 대상 일수</span>
                    <span className="font-medium">{schedules.length}일</span>
                  </div>
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">예상 슬롯 수</span>
                    <span className="font-medium">
                      {estimatedSlotCount > 0
                        ? `약 ${estimatedSlotCount}개`
                        : '간격을 선택하세요'}
                    </span>
                  </div>
                  <div className="mt-3 max-h-48 space-y-1 overflow-y-auto text-xs">
                    {schedules.slice(0, 14).map(s => (
                      <div
                        key={s.date}
                        className="flex items-center justify-between rounded px-2 py-1 odd:bg-muted/50"
                      >
                        <span>{s.date}</span>
                        <span className="text-muted-foreground">
                          {s.timeRanges
                            .map(r => `${r.startTime}~${r.endTime}`)
                            .join(', ')}
                        </span>
                      </div>
                    ))}
                    {schedules.length > 14 && (
                      <p className="pt-1 text-center text-muted-foreground">
                        ...외 {schedules.length - 14}일
                      </p>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        <Button
          onClick={handleGenerate}
          disabled={!canGenerate}
          className="w-full"
          size="lg"
        >
          {loading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              생성 중...
            </>
          ) : (
            <>
              <CalendarPlus className="mr-2 h-4 w-4" />
              슬롯 생성하기
              {estimatedSlotCount > 0 && ` (약 ${estimatedSlotCount}개)`}
            </>
          )}
        </Button>
      </CardContent>
    </Card>
  );
}
