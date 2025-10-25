'use client';

import { useMemo } from 'react';
import { TimeSlotButton } from './time-slot-button';
import type { TimeSlotStatus } from '@/lib/data/schedule/timeSlotStatus';

interface TimeSlot {
  time: string;
  status: TimeSlotStatus;
}

interface TimeSlotsGridProps {
  startTime: string;
  endTime: string;
  interval: number;
  intervalUnit: 'minute' | 'hour';
  slots?: TimeSlot[];
  onSlotStatusChange?: (time: string, status: TimeSlotStatus) => void;
}

function generateTimeSlots(
  startTime: string,
  endTime: string,
  interval: number,
  intervalUnit: 'minute' | 'hour'
): string[] {
  const slots: string[] = [];
  const [startHour, startMinute] = startTime.split(':').map(Number);
  const [endHour, endMinute] = endTime.split(':').map(Number);

  const startMinutes = startHour * 60 + startMinute;
  const endMinutes = endHour * 60 + endMinute;
  const intervalMinutes = intervalUnit === 'hour' ? interval * 60 : interval;

  for (
    let current = startMinutes;
    current < endMinutes;
    current += intervalMinutes
  ) {
    const hours = Math.floor(current / 60);
    const minutes = current % 60;
    slots.push(
      `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`
    );
  }

  return slots;
}

export function TimeSlotsGrid({
  startTime,
  endTime,
  interval,
  intervalUnit,
  slots = [],
  onSlotStatusChange,
}: TimeSlotsGridProps) {
  const timeSlots = useMemo(
    () => generateTimeSlots(startTime, endTime, interval, intervalUnit),
    [startTime, endTime, interval, intervalUnit]
  );

  const slotMap = useMemo(() => {
    const map = new Map<string, TimeSlotStatus>();
    slots.forEach(slot => map.set(slot.time, slot.status));
    return map;
  }, [slots]);

  return (
    <div className="grid grid-cols-8 gap-2">
      {timeSlots.map(time => (
        <TimeSlotButton
          key={time}
          time={time}
          status={slotMap.get(time) || 'available'}
          onStatusChange={status => onSlotStatusChange?.(time, status)}
        />
      ))}
    </div>
  );
}
