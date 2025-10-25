'use client';

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { TimeSlotConfig } from './time-slot-config';
import { TimeSlotsGrid } from './time-slots-grid';
import type { TimeSlotStatus } from '@/lib/data/schedule/timeSlotStatus';

interface ScheduleEditorPanelProps {
  selectedDay?: string;
  startTime: string;
  endTime: string;
}

export function ScheduleEditorPanel({
  selectedDay,
  startTime,
  endTime,
}: ScheduleEditorPanelProps) {
  const [interval, setInterval] = useState(15);
  const [intervalUnit, setIntervalUnit] = useState<'minute' | 'hour'>('minute');
  const [slots, setSlots] = useState<
    Array<{ time: string; status: TimeSlotStatus }>
  >([]);

  const handleSlotStatusChange = (time: string, status: TimeSlotStatus) => {
    setSlots((prev) => {
      const existing = prev.find((s) => s.time === time);
      if (existing) {
        return prev.map((s) => (s.time === time ? { time, status } : s));
      }
      return [...prev, { time, status }];
    });
  };

  return (
    <Card className="flex-1">
      <CardHeader>
        <CardTitle>
          {selectedDay ? `${selectedDay} 예약 슬롯 편집` : '요일 예약 슬롯 편집'}
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        <TimeSlotConfig
          interval={interval}
          intervalUnit={intervalUnit}
          onIntervalChange={setInterval}
          onIntervalUnitChange={setIntervalUnit}
        />

        <TimeSlotsGrid
          startTime={startTime}
          endTime={endTime}
          interval={interval}
          intervalUnit={intervalUnit}
          slots={slots}
          onSlotStatusChange={handleSlotStatusChange}
        />
      </CardContent>
    </Card>
  );
}
