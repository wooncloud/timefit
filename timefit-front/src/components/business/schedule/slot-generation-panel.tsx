'use client';

import { CalendarPlus } from 'lucide-react';

import type { Menu } from '@/types/customer/menu';
import type { OperatingHours } from '@/types/schedule/operating-hours';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

interface SlotGenerationPanelProps {
  businessId: string;
  menus: Menu[];
  operatingHours: OperatingHours;
}

export function SlotGenerationPanel({
  businessId: _businessId,
  menus,
  operatingHours: _operatingHours,
}: SlotGenerationPanelProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>예약 슬롯 생성</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col items-center justify-center py-16 text-center">
          <CalendarPlus className="mb-4 h-12 w-12 text-muted-foreground" />
          {menus.length === 0 ? (
            <>
              <p className="text-sm text-muted-foreground">
                예약형 서비스가 없습니다.
              </p>
              <p className="mt-1 text-xs text-muted-foreground">
                서비스 관리에서 예약형 서비스를 먼저 등록해주세요.
              </p>
            </>
          ) : (
            <p className="text-sm text-muted-foreground">
              슬롯 생성 기능이 곧 추가됩니다.
            </p>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
