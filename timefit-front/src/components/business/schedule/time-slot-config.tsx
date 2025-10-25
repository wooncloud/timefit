'use client';

import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';

interface TimeSlotConfigProps {
  interval: number;
  intervalUnit: 'minute' | 'hour';
  onIntervalChange?: (value: number) => void;
  onIntervalUnitChange?: (unit: 'minute' | 'hour') => void;
}

export function TimeSlotConfig({
  interval,
  intervalUnit,
  onIntervalChange,
  onIntervalUnitChange,
}: TimeSlotConfigProps) {
  const handleUnitChange = (value: string) => {
    const unit = value as 'minute' | 'hour';
    onIntervalUnitChange?.(unit);

    // 시간 → 분: 15분으로 변경
    if (unit === 'minute') {
      onIntervalChange?.(15);
    }
    // 분 → 시간: 1시간으로 변경
    else if (unit === 'hour') {
      onIntervalChange?.(1);
    }
  };

  return (
    <div className="flex items-center gap-4 rounded-lg border p-4">
      <div className="flex items-center gap-2">
        <Label htmlFor="interval">예약 간격</Label>
        <Input
          id="interval"
          type="number"
          min="1"
          max="60"
          value={interval}
          onChange={e => onIntervalChange?.(Number(e.target.value))}
          className="w-20"
        />
      </div>

      <RadioGroup
        value={intervalUnit}
        onValueChange={handleUnitChange}
        className="flex gap-2"
      >
        <div className="flex items-center space-x-2">
          <RadioGroupItem value="minute" id="minute" />
          <Label htmlFor="minute" className="cursor-pointer">
            분
          </Label>
        </div>
        <div className="flex items-center space-x-2">
          <RadioGroupItem value="hour" id="hour" />
          <Label htmlFor="hour" className="cursor-pointer">
            시간
          </Label>
        </div>
      </RadioGroup>
    </div>
  );
}
