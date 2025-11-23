'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

interface ScheduleEditorPanelProps {
  selectedDay?: string;
  startTime: string;
  endTime: string;
}

export function ScheduleEditorPanel({
  selectedDay,
}: ScheduleEditorPanelProps) {

  return (
    <Card className="flex-1">
      <CardHeader>
        <CardTitle>
          {selectedDay
            ? `${selectedDay} 예약 슬롯 편집`
            : '요일 예약 슬롯 편집'}
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
      </CardContent>
    </Card>
  );
}
