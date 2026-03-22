'use client';

import { ClipboardList } from 'lucide-react';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Empty,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';

interface SlotManagementPanelProps {
  businessId: string;
}

export function SlotManagementPanel({
  businessId: _businessId,
}: SlotManagementPanelProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>예약 슬롯 관리</CardTitle>
      </CardHeader>
      <CardContent>
        <Empty>
          <EmptyMedia>
            <ClipboardList className="h-12 w-12" />
          </EmptyMedia>
          <EmptyHeader>
            <EmptyTitle>슬롯 관리 기능이 곧 추가됩니다</EmptyTitle>
          </EmptyHeader>
        </Empty>
      </CardContent>
    </Card>
  );
}
