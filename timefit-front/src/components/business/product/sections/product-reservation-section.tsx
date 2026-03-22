import { useState } from 'react';
import { toast } from 'sonner';

import type { MenuType, Product } from '@/types/product/product';
import { MENU_TYPES } from '@/lib/constants/product-categories';
import { SLOT_INTERVAL_OPTIONS } from '@/lib/constants/slot-options';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Switch } from '@/components/ui/switch';

interface ProductReservationSectionProps {
  formData: Partial<Product>;
  onFormDataChange: (data: Partial<Product>) => void;
}

export function ProductReservationSection({
  formData,
  onFormDataChange,
}: ProductReservationSectionProps) {
  const [durationInput, setDurationInput] = useState(
    formData.duration_minutes?.toString() || '60'
  );

  const isReservationBased = formData.menu_type === 'RESERVATION_BASED';

  const handleDurationBlur = () => {
    const numValue = durationInput.replace(/[^0-9]/g, '');

    if (!numValue) {
      toast.error('서비스 시간을 입력해주세요.');
      setDurationInput('60');
      onFormDataChange({ ...formData, duration_minutes: 60 });
      return;
    }

    const parsedDuration = parseInt(numValue, 10);

    if (parsedDuration < 5) {
      toast.error('서비스 시간은 최소 5분 이상이어야 합니다.');
      setDurationInput('60');
      onFormDataChange({ ...formData, duration_minutes: 60 });
      return;
    }

    if (parsedDuration > 1440) {
      toast.error('서비스 시간은 최대 1440분(24시간)을 초과할 수 없습니다.');
      setDurationInput('1440');
      onFormDataChange({ ...formData, duration_minutes: 1440 });
      return;
    }

    setDurationInput(parsedDuration.toString());
    onFormDataChange({ ...formData, duration_minutes: parsedDuration });
  };

  const handleDurationChange = (value: string) => {
    const cleaned = value.replace(/[^0-9]/g, '');
    setDurationInput(cleaned);
  };

  const handleAutoGenerateSlotsChange = (checked: boolean) => {
    onFormDataChange({
      ...formData,
      auto_generate_slots: checked,
      ...(checked
        ? {
            slot_interval_minutes:
              formData.slot_interval_minutes ??
              formData.duration_minutes ??
              60,
          }
        : {
            slot_start_date: undefined,
            slot_end_date: undefined,
            slot_interval_minutes: undefined,
          }),
    });
  };

  const today = new Date().toLocaleDateString('sv-SE');

  return (
    <div className="space-y-4 border-t pt-6">
      <div className="flex items-center gap-2">
        <div className="text-lg font-semibold">⏱️ 예약확정 방식</div>
      </div>

      <div className="space-y-3">
        <Label>서비스 유형</Label>
        <RadioGroup
          value={formData.menu_type}
          onValueChange={(value: MenuType) =>
            onFormDataChange({ ...formData, menu_type: value })
          }
        >
          {Object.entries(MENU_TYPES).map(([key, label]) => (
            <div key={key} className="flex items-center space-x-2">
              <RadioGroupItem value={key} id={key} />
              <Label htmlFor={key} className="font-normal">
                {label}
              </Label>
            </div>
          ))}
        </RadioGroup>
      </div>

      <div className="space-y-2">
        <Label htmlFor="duration">서비스 시간 (필수)</Label>
        <div className="flex items-center gap-2">
          <Input
            id="duration"
            type="text"
            inputMode="numeric"
            value={durationInput}
            onChange={e => handleDurationChange(e.target.value)}
            onBlur={handleDurationBlur}
            placeholder="60"
            required
          />
          <span className="text-sm text-muted-foreground">분</span>
        </div>
      </div>

      {isReservationBased && (
        <div className="space-y-4 rounded-lg border bg-muted/30 p-4">
          <div className="flex items-center justify-between">
            <div>
              <Label htmlFor="auto-generate-slots" className="text-sm font-medium">
                예약 슬롯 자동 생성
              </Label>
              <p className="text-xs text-muted-foreground">
                메뉴 생성 시 영업시간에 맞춰 예약 슬롯이 자동 생성됩니다
              </p>
            </div>
            <Switch
              id="auto-generate-slots"
              checked={formData.auto_generate_slots || false}
              onCheckedChange={handleAutoGenerateSlotsChange}
            />
          </div>

          {formData.auto_generate_slots && (
            <div className="space-y-3 border-t pt-3">
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1">
                  <Label htmlFor="slot-start-date" className="text-xs">
                    시작 날짜
                  </Label>
                  <Input
                    id="slot-start-date"
                    type="date"
                    min={today}
                    value={formData.slot_start_date || ''}
                    onChange={e =>
                      onFormDataChange({
                        ...formData,
                        slot_start_date: e.target.value,
                      })
                    }
                  />
                </div>
                <div className="space-y-1">
                  <Label htmlFor="slot-end-date" className="text-xs">
                    종료 날짜
                  </Label>
                  <Input
                    id="slot-end-date"
                    type="date"
                    min={formData.slot_start_date || today}
                    value={formData.slot_end_date || ''}
                    onChange={e =>
                      onFormDataChange({
                        ...formData,
                        slot_end_date: e.target.value,
                      })
                    }
                  />
                </div>
              </div>

              <div className="space-y-1">
                <Label htmlFor="slot-interval" className="text-xs">
                  슬롯 간격
                </Label>
                <Select
                  value={formData.slot_interval_minutes?.toString() || ''}
                  onValueChange={value =>
                    onFormDataChange({
                      ...formData,
                      slot_interval_minutes: parseInt(value, 10),
                    })
                  }
                >
                  <SelectTrigger id="slot-interval">
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
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
