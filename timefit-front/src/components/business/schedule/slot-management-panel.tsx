'use client';

import { ClipboardList } from 'lucide-react';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

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
        <div className="flex flex-col items-center justify-center py-16 text-center">
          <ClipboardList className="mb-4 h-12 w-12 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            슬롯 관리 기능이 곧 추가됩니다.
          </p>
        </div>
      </CardContent>
    </Card>
  );
}
