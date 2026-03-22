'use client';

import { CalendarPlus } from 'lucide-react';

import type { Menu } from '@/types/customer/menu';
import type { OperatingHours } from '@/types/schedule/operating-hours';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';

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
        <Empty>
          <EmptyMedia>
            <CalendarPlus className="h-12 w-12" />
          </EmptyMedia>
          <EmptyHeader>
            {menus.length === 0 ? (
              <>
                <EmptyTitle>예약형 서비스가 없습니다</EmptyTitle>
                <EmptyDescription>
                  서비스 관리에서 예약형 서비스를 먼저 등록해주세요.
                </EmptyDescription>
              </>
            ) : (
              <EmptyTitle>슬롯 생성 기능이 곧 추가됩니다</EmptyTitle>
            )}
          </EmptyHeader>
        </Empty>
      </CardContent>
    </Card>
  );
}
