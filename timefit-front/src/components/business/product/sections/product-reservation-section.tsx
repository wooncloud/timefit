import { useState } from 'react';
import { toast } from 'sonner';

import type { MenuType, Product } from '@/types/product/product';
import { MENU_TYPES } from '@/lib/constants/product-categories';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';

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
    </div>
  );
}
